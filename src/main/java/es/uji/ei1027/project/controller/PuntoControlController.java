package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.CarreraDao;
import es.uji.ei1027.project.dao.PuntoControlDao;
import es.uji.ei1027.project.model.Carrera;
import es.uji.ei1027.project.model.PuntoControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/puntoControl")
public class PuntoControlController {

    private PuntoControlDao puntoControlDao;
    private CarreraDao carreraDao;

    @Autowired
    public void setPuntoControlDao(PuntoControlDao puntoControlDao){
        this.puntoControlDao = puntoControlDao;
    }

    @Autowired
    public void setCarreraDao(CarreraDao carreraDao) {
        this.carreraDao = carreraDao;
    }

    @RequestMapping("/list")
    public String listPuntosControl(Model model){
        model.addAttribute("puntosControl", puntoControlDao.getPuntosControl());
        return "puntoControl/list";
    }

    @RequestMapping(value="/add")
    public String addPuntoControl(Model model,  @RequestParam("codCarrera") String codCarrera){
        String codCompeticion = carreraDao.getCarrera(codCarrera).getCodCompeticion();
        PuntoControl puntoControl = new PuntoControl();
        puntoControl.setCodCarrera(codCarrera);
        model.addAttribute("inicio", carreraDao.getCarrera(codCarrera).getInicio());
        model.addAttribute("codCarrera", codCarrera);
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("puntoControl", puntoControl);
        return "puntoControl/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("puntoControl") PuntoControl puntoControl,
                                   BindingResult bindingResult){
        String codCarrera = puntoControl.getCodCarrera();
        String codCompeticion = carreraDao.getCarrera(codCarrera).getCodCompeticion();
        if(bindingResult.hasErrors()){
            return "puntoControl/add";
        }

        String codPunto = generatePointCode();
        puntoControl.setCod(codPunto);

        puntoControlDao.addPuntoControl(puntoControl);
        return "redirect:porCarrera/" + codCarrera + "?codCompeticion=" + codCompeticion;
    }

    @RequestMapping(value="/update/{cod}", method = RequestMethod.GET)
    public String editPuntoControl(Model model, @PathVariable String cod) {
        PuntoControl puntoControl = puntoControlDao.getPuntoControl(cod);
        String codCarrera = puntoControl.getCodCarrera();
        model.addAttribute("codCarrera", codCarrera);
        model.addAttribute("puntoControl", puntoControl);
        model.addAttribute("codCompeticion", carreraDao.getCarrera(puntoControl.getCodCarrera()).getCodCompeticion());
        return "puntoControl/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("puntoControl") PuntoControl puntoControl,
                                      BindingResult bindingResult) {
        String codCarrera = puntoControl.getCodCarrera();
        String codCompeticion = carreraDao.getCarrera(codCarrera).getCodCompeticion();
        if (bindingResult.hasErrors()) {
            return "puntoControl/update";
        }
        puntoControlDao.updatePuntoControl(puntoControl);
        return "redirect:porCarrera/" + codCarrera + "?codCompeticion=" + codCompeticion;
    }

    @RequestMapping(value="/confirmDelete", method = RequestMethod.POST)
    public String processDelete(@RequestParam("codPuntoControlToDelete") String cod,
                                @RequestParam("codCarrera") String codCarrera,
                                @RequestParam("codCompeticion") String codCompeticion,
                                RedirectAttributes redirectAttributes) {
        PuntoControl puntoControl = puntoControlDao.getPuntoControl(cod);

        if (puntoControl == null) {
            return "redirect:/puntoControl/porCarrera/" + codCarrera + "?codCompeticion=" + codCompeticion;
        }

        try {
            puntoControlDao.deletePuntoControl(cod);
            redirectAttributes.addFlashAttribute("mensajeExito", "Punto de control borrado correctamente.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se puede borrar el punto de control. Asegúrate de que no tenga registros de tiempo asociados.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error inesperado al intentar borrar la competición.");
        }
        return "redirect:/puntoControl/porCarrera/" + codCarrera + "?codCompeticion=" + codCompeticion;
    }

    @RequestMapping("/porCarrera/{codCarrera}")
    public String listPuntosPorCarrera(
            @PathVariable String codCarrera,
            @RequestParam("codCompeticion") String codCompeticion,
            @RequestParam(name = "localizacion", required = false) String localizacion,
            @RequestParam(value = "confirmDelete", required = false) String confirmDeleteCodPuntoControl,
            Model model) {

        List<PuntoControl> puntos = puntoControlDao.getPuntosPorCarrera(codCarrera);

        if (localizacion != null && !localizacion.trim().isEmpty()) {
            puntos = puntos.stream()
                    .filter(p -> p.getLocalizacion().toLowerCase().contains(localizacion.toLowerCase()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("codCarrera", codCarrera);
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("nombre", carreraDao.getCarrera(codCarrera).getInicio());
        model.addAttribute("puntosControl", puntos);
        model.addAttribute("localizacion", localizacion);

        if (confirmDeleteCodPuntoControl != null && !confirmDeleteCodPuntoControl.isEmpty()) {
            PuntoControl puntoControlToDelete = puntoControlDao.getPuntoControl(confirmDeleteCodPuntoControl);
            if (puntoControlToDelete != null) {
                model.addAttribute("puntoControlToDelete", puntoControlToDelete);
            } else {
                model.addAttribute("mensajeError", "El punto de control a borrar no existe.");
            }
        }

        return "puntoControl/listByCarrera";
    }

    @RequestMapping(value = "/delete/{cod}", method = RequestMethod.GET)
    public String initiateDeleteConfirmation(@PathVariable String cod,
                                             Model model) {

        PuntoControl puntoControl = puntoControlDao.getPuntoControl(cod);
        if (puntoControl == null) {
            return "redirect:/puntoControl/list";
        }

        String codCarrera = puntoControl.getCodCarrera();
        String codCompeticion = carreraDao.getCarrera(codCarrera).getCodCompeticion();

        return "redirect:/puntoControl/porCarrera/" + codCarrera + "?codCompeticion=" + codCompeticion + "&confirmDelete=" + cod;
    }

    @RequestMapping("/competicion/{codCompeticion}/list")
    public String listCompeticionCarreras(@PathVariable String codCompeticion, Model model) {
        // Obtener todas las carreras de la competición
        List<Carrera> carreras = carreraDao.getCarrerasPorCompeticion(codCompeticion);
        model.addAttribute("carreras", carreras);
        model.addAttribute("codCompeticion", codCompeticion);
        return "competicion/list";
    }


    private String generatePointCode() {
        return "R" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }





}
