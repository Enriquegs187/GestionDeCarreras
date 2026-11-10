package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.AsignanDao;
import es.uji.ei1027.project.dao.CarreraDao;
import es.uji.ei1027.project.dao.VoluntarioDao;
import es.uji.ei1027.project.model.Asignan;
import es.uji.ei1027.project.model.Carrera;
import es.uji.ei1027.project.model.PuntoControl;
import es.uji.ei1027.project.dao.PuntoControlDao;
import es.uji.ei1027.project.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/asignan")
public class AsignanController {
    private AsignanDao asignanDao;
    private PuntoControlDao puntoControlDao;
    private CarreraDao carreraDao;
    private VoluntarioDao voluntarioDao;

    @Autowired
    public void setAsignanDao(AsignanDao asignanDao) {
        this.asignanDao = asignanDao;
    }

    @Autowired
    public void setPuntoControlDao(PuntoControlDao puntoControlDao) {
        this.puntoControlDao = puntoControlDao;
    }

    @Autowired
    public void setCarreraDao(CarreraDao carreraDao) {
        this.carreraDao = carreraDao;
    }

    @Autowired
    public void setVoluntarioDao(VoluntarioDao voluntarioDao) {
        this.voluntarioDao = voluntarioDao;
    }

    @RequestMapping("/list")
    public String listAsignanDao(Model model) {
        model.addAttribute("Asignaciones", asignanDao.getAsignan());
        return "asignan/list";
    }

    @PostMapping(value="/add")
    public String processAddSubmit(@ModelAttribute Asignan asignan) {
        asignanDao.addAsignan(asignan);

        PuntoControl puntoControl = puntoControlDao.getPuntoControl(asignan.getCodPuntoControl());

        if (puntoControl != null) {
            String codCarrera = puntoControl.getCodCarrera();
            String codCompeticion = carreraDao.getCarrera(codCarrera).getCodCompeticion();

            return "redirect:/puntoControl/porCarrera/" + codCarrera + "?codCompeticion=" + codCompeticion;
        }

        return "redirect:/error";
    }

    // En AsignanController.java
    @GetMapping(value = "/add")
    public String addAsignan(@RequestParam(required = false) String codPuntoControl, Model model) {
        Asignan asignan = new Asignan();
        if (codPuntoControl != null) {
            asignan.setCodPuntoControl(codPuntoControl);
        }

        PuntoControl puntoControl = puntoControlDao.getPuntoControl(codPuntoControl);
        if (puntoControl != null) {
            String codCarrera = puntoControl.getCodCarrera();
            model.addAttribute("codCarrera", codCarrera);

            Carrera carrera = carreraDao.getCarrera(codCarrera);
            if (carrera != null) {
                String codCompeticion = carrera.getCodCompeticion();
                model.addAttribute("codCompeticion", codCompeticion);
            }
            model.addAttribute("localizacion", puntoControl.getLocalizacion());
        }


        List<Voluntario> voluntariosActivos = voluntarioDao.getVoluntariosPorEstado(true);
        model.addAttribute("voluntarios", voluntariosActivos); // La vista usará esta lista

        model.addAttribute("asignan", asignan);
        model.addAttribute("codPuntoControl", codPuntoControl);
        return "asignan/add";
    }


    /*@GetMapping(value = "/delete/{dniVoluntario}/{codPuntoControl}")
    public String deleteAsignan(@PathVariable String dniVoluntario, @PathVariable String codPuntoControl) {
        asignanDao.deleteAsignan(dniVoluntario, codPuntoControl);
        return "redirect:/asignan/voluntarios/" + codPuntoControl;
    }*/

    @RequestMapping(value = "/confirmDeleteAsignan", method = RequestMethod.POST)
    public String processDelete(@RequestParam("dniVoluntarioToDelete") String dniVoluntario,
                                @RequestParam("codPuntoControl") String codPuntoControlString) {
        asignanDao.deleteAsignan(dniVoluntario, codPuntoControlString);
        return "redirect:/asignan/voluntarios/" + codPuntoControlString;
    }

    @RequestMapping("/voluntarios/{codPuntoControl}")
    public String listVoluntariosPorPuntoControl(@PathVariable String codPuntoControl,
                                                 @RequestParam(value = "confirmDeleteDni", required = false) String confirmDeleteDni,
                                                 Model model) {
        List<Asignan> asignaciones = asignanDao.getVoluntariosByPuntoControl(codPuntoControl);
        List<Voluntario> voluntarios = new ArrayList<>();
        for (Asignan asignacion : asignaciones) {
            Voluntario v = voluntarioDao.getVoluntario(asignacion.getDniVoluntario());
            if (v != null) {
                voluntarios.add(v);
            }
        }
        model.addAttribute("voluntarios", voluntarios);


        PuntoControl puntoControl = puntoControlDao.getPuntoControl(codPuntoControl);
        String codCarrera = puntoControl.getCodCarrera();
        String codCompeticion = carreraDao.getCarrera(codCarrera).getCodCompeticion();

        model.addAttribute("voluntarios", voluntarios);
        model.addAttribute("codPuntoControl", codPuntoControl);
        model.addAttribute("localizacion", puntoControl.getLocalizacion());
        model.addAttribute("codCarrera", codCarrera);
        model.addAttribute("codCompeticion", codCompeticion);

        if (confirmDeleteDni != null && !confirmDeleteDni.isEmpty()) {
            Voluntario voluntarioToDelete = voluntarioDao.getVoluntario(confirmDeleteDni);
            if (voluntarioToDelete != null) {
                model.addAttribute("voluntarioToDelete", voluntarioToDelete);
                // Ya tenemos el codPuntoControl y el dniVoluntarioToDelete, suficiente para el formulario
            } else {
                model.addAttribute("mensajeError", "El voluntario a desasignar no existe.");
            }
        }
        return "asignan/voluntarios";
    }

    @RequestMapping(value = "/delete/{dniVoluntario}/{codPuntoControl}", method = RequestMethod.GET)
    public String initiateDeleteConfirmation(@PathVariable String dniVoluntario,
                                             @PathVariable String codPuntoControl,
                                             Model model) {

        Asignan asignan = asignanDao.getAsignan(dniVoluntario, codPuntoControl);
        if (asignan == null) {
            model.addAttribute("mensajeError", "La asignación a eliminar no fue encontrada.");
        }

        return "redirect:/asignan/voluntarios/" + codPuntoControl + "?confirmDeleteDni=" + dniVoluntario;
    }

    @RequestMapping("/porCarrera/{codCarrera}")
    public String listPuntosPorCarrera(@PathVariable String codCarrera,
                                       @RequestParam(value = "codCompeticion", required = false) String codCompeticion,
                                       Model model) {
        List<PuntoControl> puntos = puntoControlDao.getPuntosPorCarrera(codCarrera);
        model.addAttribute("puntosControl", puntos);
        model.addAttribute("codCarrera", codCarrera);
        model.addAttribute("codCompeticion", codCompeticion);
        return "puntoControl/listByCarrera";
    }
}