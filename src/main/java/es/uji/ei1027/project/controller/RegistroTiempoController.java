package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.*;
import es.uji.ei1027.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;


@Controller
@RequestMapping("/registroTiempo")
public class RegistroTiempoController {

    @Autowired
    private RegistroTiempoDao registroTiempoDao;

    @Autowired
    private CarreraDao carreraDao;

    @Autowired
    private ParticipanteDao participanteDao;

    @Autowired
    private PuntoControlDao puntoControlDao;

    @Autowired
    private InscripcionDao inscripcionDao;


    @Autowired
    public void setRegistroTiempoDao(RegistroTiempoDao registroTiempoDao) {
        this.registroTiempoDao = registroTiempoDao;
    }

    @RequestMapping("/list")
    public String listRegistrosTiempo(Model model) {
        model.addAttribute("registrosTiempo", registroTiempoDao.getRegistrosTiempo());
        return "registroTiempo/list";
    }

    @GetMapping("/add")
    public String addRegistroTiempo(Model model, @RequestParam("codCarrera") String codCarrera, @RequestParam("codCompeticion") String codCompeticion) {
        Carrera carrera = carreraDao.getCarrera(codCarrera);

        List<Inscripcion> inscripciones = inscripcionDao.getInscripcionesPorCarrera(codCarrera);
        List<PuntoControl> puntosControl = puntoControlDao.getPuntosPorCarrera(codCarrera);

        Map<String, String> nombresParticipantes = new HashMap<>();
        for (Inscripcion inscripcion : inscripciones) {
            Participante p = participanteDao.getParticipante(inscripcion.getDniParticipante());
            if (p != null) {
                nombresParticipantes.put(inscripcion.getCod(), p.getNombre() + " " + p.getApellido());
            }
        }

        RegistroTiempo nuevoRegistro = new RegistroTiempo();
        nuevoRegistro.setCod("RT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        model.addAttribute("registroTiempo", nuevoRegistro);
        model.addAttribute("carrera", carrera);
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("inscripciones", inscripciones);
        model.addAttribute("puntosControl", puntosControl);
        model.addAttribute("nombresParticipantes", nombresParticipantes);

        return "registroTiempo/add";
    }

    @PostMapping("/add")
    public String processAddSubmit(@ModelAttribute("registroTiempo") RegistroTiempo registro,
                                   BindingResult bindingResult,
                                   @RequestParam String codCarrera,
                                   @RequestParam String codCompeticion,
                                   Model model) {

        if (registroTiempoDao.existeRegistroParaInscripcionYPunto(registro.getCodInscripcion(), registro.getCodPuntoControl())) {
            bindingResult.rejectValue("codPuntoControl", "error.registroTiempo",
                    "Ya se ha registrado un tiempo para este punto de control.");
        }

        if (bindingResult.hasErrors()) {


            Inscripcion inscripcion = inscripcionDao.getInscripcion(registro.getCodInscripcion());
            if (inscripcion == null) { return "redirect:/"; } // Seguridad

            model.addAttribute("participante", participanteDao.getParticipante(inscripcion.getDniParticipante()));
            model.addAttribute("carrera", carreraDao.getCarrera(codCarrera));
            model.addAttribute("codCompeticion", codCompeticion);
            model.addAttribute("puntosControl", puntoControlDao.getPuntosPorCarrera(codCarrera));

            return "registroTiempo/add_tiempo_paso";
        }

        registroTiempoDao.addRegistroTiempo(registro);

        Inscripcion inscripcion = inscripcionDao.getInscripcion(registro.getCodInscripcion());
        String dniParticipante = (inscripcion != null) ? inscripcion.getDniParticipante() : "";

        return "redirect:/registroTiempo/porParticipante/" + dniParticipante
                + "?codCarrera=" + codCarrera
                + "&codCompeticion=" + codCompeticion;
    }



    @GetMapping("/update/{cod}")
    public String editRegistroTiempo(Model model,
                                     @PathVariable("cod") String cod,
                                     @RequestParam("codCarrera") String codCarrera,
                                     @RequestParam("codCompeticion") String codCompeticion) {

        addRegistroTiempo(model, codCarrera, codCompeticion);
        model.addAttribute("registroTiempo", registroTiempoDao.getRegistroTiempo(cod));
        return "registroTiempo/update";
    }

    @PostMapping("/update")
    public String processUpdateSubmit(@ModelAttribute("registroTiempo") RegistroTiempo registro,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "registroTiempo/update";
        }
        registroTiempoDao.updateRegistroTiempo(registro);

        Inscripcion inscripcion = inscripcionDao.getInscripcion(registro.getCodInscripcion());
        String dniParticipante = (inscripcion != null) ? inscripcion.getDniParticipante() : "";
        String codCarrera = (inscripcion != null) ? inscripcion.getCodCarrera() : "";
        String codCompeticion = (inscripcion != null) ? carreraDao.getCarrera(codCarrera).getCodCompeticion() : "";

        return "redirect:/registroTiempo/porParticipante/" + dniParticipante
                + "?codCarrera=" + codCarrera
                + "&codCompeticion=" + codCompeticion;
    }

    @GetMapping("/delete/{cod}")
    public String initiateDeleteTiempoConfirmation(
            @PathVariable String cod,
            @RequestParam("codCarrera") String codCarrera,
            @RequestParam("codCompeticion") String codCompeticion,
            Model model,
            RedirectAttributes redirectAttributes) {

        RegistroTiempo registroABorrar = registroTiempoDao.getRegistroTiempo(cod);
        if (registroABorrar == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "El tiempo de paso a borrar no fue encontrado.");
            Inscripcion inscripcion = inscripcionDao.getInscripcion(registroABorrar != null ? registroABorrar.getCodInscripcion() : null);
            String dniParticipante = (inscripcion != null) ? inscripcion.getDniParticipante() : "";
            return "redirect:/registroTiempo/porParticipante/" + dniParticipante
                    + "?codCarrera=" + codCarrera
                    + "&codCompeticion=" + codCompeticion;
        }

        Inscripcion inscripcion = inscripcionDao.getInscripcion(registroABorrar.getCodInscripcion());
        if (inscripcion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontró la inscripción asociada al tiempo de paso.");
            String dniParticipante = (inscripcion != null) ? inscripcion.getDniParticipante() : "";
            return "redirect:/registroTiempo/porParticipante/" + dniParticipante
                    + "?codCarrera=" + codCarrera
                    + "&codCompeticion=" + codCompeticion;
        }

        PuntoControl puntoControl = puntoControlDao.getPuntoControl(registroABorrar.getCodPuntoControl());
        String nombrePuntoControl = (puntoControl != null) ? puntoControl.getLocalizacion() : "Desconocido";
        Participante participante = participanteDao.getParticipante(inscripcion.getDniParticipante());
        String nombreParticipante = (participante != null) ? participante.getNombre() + " " + participante.getApellido() : "Desconocido";

        model.addAttribute("registroTiempoToDelete", registroABorrar);
        model.addAttribute("nombrePuntoControlToDelete", nombrePuntoControl);
        model.addAttribute("nombreParticipanteToDelete", nombreParticipante);
        model.addAttribute("codCarrera", codCarrera);
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("dniParticipante", inscripcion.getDniParticipante());

        // ✅ Llamada con nombrePunto null
        listTiemposPorParticipante(inscripcion.getDniParticipante(), codCarrera, codCompeticion, null, model);

        return "registroTiempo/detallePorParticipante";
    }


    @PostMapping("/confirmDeleteTiempoPaso")
    public String processConfirmedDeleteTiempoPaso(
            @RequestParam("codRegistroTiempo") String codRegistroTiempo,
            @RequestParam("codCarrera") String codCarrera,
            @RequestParam("codCompeticion") String codCompeticion,
            @RequestParam("dniParticipante") String dniParticipante,
            RedirectAttributes redirectAttributes) {

        try {
            registroTiempoDao.deleteRegistroTiempo(codRegistroTiempo);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Tiempo de paso eliminado correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo eliminar el tiempo de paso debido a restricciones de datos.");

        }

        return "redirect:/registroTiempo/porParticipante/" + dniParticipante
                + "?codCarrera=" + codCarrera
                + "&codCompeticion=" + codCompeticion;
    }

    @GetMapping("/porCarrera/{codCarrera}")
    public String listByCarrera(
            @PathVariable String codCarrera,
            @RequestParam("codCompeticion") String codCompeticion,
            @RequestParam(required = false) String nombreParticipante,
            @RequestParam(required = false) String puntoControl,
            @RequestParam(required = false, defaultValue = "asc") String orden, // Por defecto, ordenamos como clasificación
            Model model) {

        List<RegistroTiempo> registrosTiempo = registroTiempoDao.getRegistrosFiltradosYOrdenados(
                codCarrera, nombreParticipante, puntoControl, orden);

        Carrera carrera = carreraDao.getCarrera(codCarrera);
        Map<String, String> nombresParticipantes = new HashMap<>();
        Map<String, String> nombresPuntosControl = new HashMap<>();

        for (RegistroTiempo rt : registrosTiempo) {
            Inscripcion inscripcion = inscripcionDao.getInscripcion(rt.getCodInscripcion());
            if (inscripcion != null) {
                Participante participante = participanteDao.getParticipante(inscripcion.getDniParticipante());
                if (participante != null) {
                    nombresParticipantes.put(rt.getCodInscripcion(), participante.getNombre() + " " + participante.getApellido());
                }
            }
            PuntoControl pc = puntoControlDao.getPuntoControl(rt.getCodPuntoControl());
            if (pc != null) {
                nombresPuntosControl.put(rt.getCodPuntoControl(), pc.getLocalizacion());
            }
        }

        model.addAttribute("carrera", carrera);
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("registrosTiempo", registrosTiempo);
        model.addAttribute("nombresParticipantes", nombresParticipantes);
        model.addAttribute("nombresPuntosControl", nombresPuntosControl);

        model.addAttribute("puntosDeControlDisponibles", puntoControlDao.getPuntosPorCarrera(codCarrera));

        model.addAttribute("nombreBuscado", nombreParticipante);
        model.addAttribute("puntoSeleccionado", puntoControl);
        model.addAttribute("orden", orden);

        return "registroTiempo/listByCarrera";
    }
    @GetMapping("/porCarreraViewer/{codCarrera}")
    public String listByCarreraViewer(
            @PathVariable String codCarrera,
            @RequestParam("codCompeticion") String codCompeticion,
            @RequestParam(required = false) String nombreParticipante,
            @RequestParam(required = false) String puntoControl,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Model model) {

        listByCarrera(codCarrera, codCompeticion, nombreParticipante, puntoControl, orden, model);

        return "registroTiempo/listByCarreraViewer";
    }


    @GetMapping("/porParticipante/{dniParticipante}")
    public String listTiemposPorParticipante(
            @PathVariable String dniParticipante,
            @RequestParam String codCarrera,
            @RequestParam String codCompeticion,
            @RequestParam(required = false) String nombrePunto,
            Model model) {

        Inscripcion inscripcion = inscripcionDao.getInscripcionPorParticipanteYCarrera(dniParticipante, codCarrera);

        List<RegistroTiempo> registrosTiempo = new ArrayList<>();
        if (inscripcion != null) {
            registrosTiempo = registroTiempoDao.getRegistrosPorInscripcion(inscripcion.getCod());
        }

        Map<String, String> nombresPuntosControl = new HashMap<>();
        for (RegistroTiempo rt : registrosTiempo) {
            PuntoControl pc = puntoControlDao.getPuntoControl(rt.getCodPuntoControl());
            if (pc != null) {
                nombresPuntosControl.put(rt.getCodPuntoControl(), pc.getLocalizacion());
            }
        }

        // Filtro por nombre del punto de control
        if (nombrePunto != null && !nombrePunto.isEmpty()) {
            String filtro = nombrePunto.trim().toLowerCase();
            registrosTiempo = registrosTiempo.stream()
                    .filter(rt -> {
                        String nombre = nombresPuntosControl.get(rt.getCodPuntoControl());
                        return nombre != null && nombre.toLowerCase().contains(filtro);
                    })
                    .toList();
        }

        model.addAttribute("participante", participanteDao.getParticipante(dniParticipante));
        model.addAttribute("carrera", carreraDao.getCarrera(codCarrera));
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("registrosTiempo", registrosTiempo);
        model.addAttribute("nombresPuntosControl", nombresPuntosControl);
        model.addAttribute("dniParticipante", dniParticipante);
        model.addAttribute("nombrePunto", nombrePunto);

        return "registroTiempo/detallePorParticipante";
    }


    @GetMapping("/add-tiempo-paso")
    public String addTiempoPasoForm(
            @RequestParam String dniParticipante,
            @RequestParam String codCarrera,
            @RequestParam String codCompeticion,
            Model model) {

        Participante participante = participanteDao.getParticipante(dniParticipante);
        Carrera carrera = carreraDao.getCarrera(codCarrera);

        Inscripcion inscripcion = inscripcionDao.getInscripcionPorParticipanteYCarrera(dniParticipante, codCarrera);

        List<PuntoControl> puntosControl = puntoControlDao.getPuntosPorCarrera(codCarrera);

        RegistroTiempo nuevoRegistro = new RegistroTiempo();
        nuevoRegistro.setCod("RT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        if (inscripcion != null) {
            nuevoRegistro.setCodInscripcion(inscripcion.getCod());
        }

        model.addAttribute("registroTiempo", nuevoRegistro);
        model.addAttribute("participante", participante);
        model.addAttribute("carrera", carrera);
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("puntosControl", puntosControl);

        return "registroTiempo/add_tiempo_paso";
    }

    @GetMapping("/detalle-publico/{dniParticipante}")
    public String verDetallePublico(
            @PathVariable String dniParticipante,
            @RequestParam String codCarrera,
            @RequestParam(required = false) String nombrePunto,
            Model model) {

        Carrera carrera = carreraDao.getCarrera(codCarrera);
        String codCompeticion = (carrera != null) ? carrera.getCodCompeticion() : "";

        // Llamada con el nuevo filtro
        listTiemposPorParticipante(dniParticipante, codCarrera, codCompeticion, nombrePunto, model);

        return "registroTiempo/detalle_participante_publico";
    }



}
