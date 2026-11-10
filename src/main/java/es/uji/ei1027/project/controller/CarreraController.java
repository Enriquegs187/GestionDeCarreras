package es.uji.ei1027.project.controller;
import es.uji.ei1027.project.dao.*;
import es.uji.ei1027.project.model.Competicion;
import es.uji.ei1027.project.model.Inscripcion;
import es.uji.ei1027.project.model.Participante;
import es.uji.ei1027.project.validator.CarreraValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import es.uji.ei1027.project.model.Carrera;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/carrera")
public class CarreraController {

    private CarreraDao carreraDao;
    private CategoriaEdadDao categoriaEdadDao;
    private CarreraCategoriaEdadDao carreraCategoriaEdadDao;
    private CompeticionDao competicionDao;
    private InscripcionDao inscripcionDao;
    private ParticipanteDao participanteDao;

    @Autowired
    public void setCarreraDao(CarreraDao carreraDao) {
        this.carreraDao=carreraDao;
    }

    @Autowired
    public void setCategoriaEdadDao(CategoriaEdadDao categoriaEdadDao) {
        this.categoriaEdadDao = categoriaEdadDao;
    }

    @Autowired
    public void setCompeticionDao(CompeticionDao competicionDao) {
        this.competicionDao = competicionDao;
    }

    @Autowired
    public void setCarreraCategoriaEdadDao(CarreraCategoriaEdadDao carreraCategoriaEdadDao) {
        this.carreraCategoriaEdadDao = carreraCategoriaEdadDao;
    }

    @Autowired
    public void setInscripcionDao(InscripcionDao inscripcionDao) {this.inscripcionDao = inscripcionDao;}

    @Autowired
    public void setParticipanteDao(ParticipanteDao participanteDao) {this.participanteDao = participanteDao;}

    @RequestMapping("/list")
    public String listCarreras(Model model) {
        model.addAttribute("carrera", carreraDao.getCarrera());
        return "carrera/list";
    }

    @RequestMapping("/viewersList/{codCompeticion}")
    public String viewersListCarreras(@PathVariable String codCompeticion, Model model, HttpSession session){
        model.addAttribute("carreras", carreraDao.getCarrerasPorCompeticion(codCompeticion));
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("nombre", carreraDao.getCompeticion(codCompeticion).getNombre());

        session.getAttribute("returnUrl");

        return "carrera/viewersList";
    }

    @GetMapping(value="/add")
    public String addCarrera(@RequestParam String codCompeticion,
                             Model model) {
        Carrera carrera = new Carrera();
        if (codCompeticion != null) {
            carrera.setCodCompeticion(codCompeticion);
            model.addAttribute("nombre", carreraDao.getCompeticion(codCompeticion).getNombre());
        }
        model.addAttribute("categoriasEdadDisponibles", categoriaEdadDao.getCategoriaEdad());
        model.addAttribute("carrera", carrera);
        return "carrera/add";
    }

    @PostMapping(value="/add")
    public String processAddSubmit(@ModelAttribute("carrera") Carrera carrera,
                                   BindingResult bindingResult,
                                   Model model) {
        CarreraValidator carreraValidator = new CarreraValidator();
        carreraValidator.validate(carrera, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("categoriasEdadDisponibles", categoriaEdadDao.getCategoriaEdad());
            if (carrera.getCodCompeticion() != null && !carrera.getCodCompeticion().isEmpty()) {
                Competicion competicion = carreraDao.getCompeticion(carrera.getCodCompeticion());
                if (competicion != null) {
                    model.addAttribute("nombre", competicion.getNombre());
                }
            }
            return "carrera/add";
        }
        String generatedCode = generateRaceCode();
        carrera.setCod(generatedCode);

        carreraDao.addCarrera(carrera);

        List<String> codCategoriasSeleccionadas = carrera.getCategoriasEdadSeleccionadas();
        if (codCategoriasSeleccionadas != null && !codCategoriasSeleccionadas.isEmpty()) {
            for (String codCategoria : codCategoriasSeleccionadas) {
                carreraCategoriaEdadDao.addCarreraCategoriaEdad(carrera.getCod(), codCategoria);
            }
        }

        return "redirect:/carrera/porCompeticion/" + carrera.getCodCompeticion();
    }

    @RequestMapping(value="/update/{cod}", method = RequestMethod.GET)
    public String editCarrera(Model model, @PathVariable String cod) {
        Carrera carrera = carreraDao.getCarrera(cod);
        model.addAttribute("carrera", carrera);

        model.addAttribute("categoriasEdadDisponibles", categoriaEdadDao.getCategoriaEdad());
        return "carrera/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("carrera") Carrera carrera,
                                      BindingResult bindingResult,
                                      Model model) {
        CarreraValidator carreraValidator = new CarreraValidator();
        carreraValidator.validate(carrera, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoriasEdadDisponibles", categoriaEdadDao.getCategoriaEdad());
            if (carrera.getCodCompeticion() != null && !carrera.getCodCompeticion().isEmpty()) {
                Competicion competicion = carreraDao.getCompeticion(carrera.getCodCompeticion());
                if (competicion != null) {
                    model.addAttribute("nombre", competicion.getNombre());
                }
            }
            return "carrera/update";
        }
        carreraDao.updateCarrera(carrera);

        carreraCategoriaEdadDao.deleteCategoriasByCarrera(carrera.getCod());
        List<String> codCategoriasSeleccionadas = carrera.getCategoriasEdadSeleccionadas();
        if (codCategoriasSeleccionadas != null && !codCategoriasSeleccionadas.isEmpty()) {
            for (String codCategoria : codCategoriasSeleccionadas) {
                carreraCategoriaEdadDao.addCarreraCategoriaEdad(carrera.getCod(), codCategoria);
            }
        }

        return "redirect:/carrera/porCompeticion/" + carrera.getCodCompeticion();
    }

    @RequestMapping(value="/confirmDelete", method = RequestMethod.POST)
    public String processDelete(@RequestParam("codCarreraToDelete") String cod,
                                         @RequestParam("codCompeticion") String codCompeticion,
                                         RedirectAttributes redirectAttributes) {
        Carrera carrera = carreraDao.getCarrera(cod);

        if (carrera == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La carrera que intentas borrar no existe.");
            return "redirect:/competicion/list";
        }

        try {
            carreraCategoriaEdadDao.deleteCategoriasByCarrera(cod);
            carreraDao.deleteCarrera(cod);
            redirectAttributes.addFlashAttribute("mensajeExito", "Carrera '" + carrera.getInicio() + "' borrada correctamente.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se puede borrar la carrera '" + carrera.getInicio() +
                            "'. Asegúrate de que no tenga puntos de control con voluntarios asignados.");
            return "redirect:/carrera/porCompeticion/" + codCompeticion;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error inesperado al intentar borrar la carrera '" + carrera.getInicio() + "'.");
            return "redirect:/carrera/porCompeticion/" + codCompeticion;
        }

        return "redirect:/carrera/porCompeticion/" + codCompeticion;
    }


    @RequestMapping(value = "/delete/{cod}", method = RequestMethod.GET)
    public String initiateDeleteConfirmation(@PathVariable String cod, RedirectAttributes redirectAttributes) {
        Carrera carrera = carreraDao.getCarrera(cod);
        if (carrera == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La carrera a eliminar no fue encontrada.");
            return "redirect:/competicion/list";
        }
        return "redirect:/carrera/porCompeticion/" + carrera.getCodCompeticion() + "?confirmDelete=" + cod;
    }


    @RequestMapping("/porCompeticion/{codCompeticion}")
    public String listCarrerasPorCompeticion(
            @PathVariable String codCompeticion,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) String filtroNombre,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) String categoria,
            @RequestParam(value = "confirmDelete", required = false) String confirmDeleteCodCarrera,
            Model model) {

        List<Carrera> carreras = carreraDao.getCarrerasPorCompeticion(codCompeticion);

        if (sexo != null && !sexo.isEmpty()) {
            carreras.removeIf(c -> !c.getCategoriaSexo().equalsIgnoreCase(sexo));
        }
        if (categoria != null && !categoria.isEmpty()) {
            carreras.removeIf(c -> !c.getCategoriasEdadString().contains(categoria));
        }
        if (filtroNombre != null && !filtroNombre.isEmpty()) {
            carreras.removeIf(c -> !c.toString().toLowerCase().contains(filtroNombre.toLowerCase()));
        }
        if ("asc".equals(orden)) {
            carreras.sort(Comparator.comparingDouble(Carrera::getDistancia));
        } else if ("desc".equals(orden)) {
            carreras.sort(Comparator.comparingDouble(Carrera::getDistancia).reversed());
        }

        model.addAttribute("carreras", carreras);
        model.addAttribute("codCompeticion", codCompeticion);
        model.addAttribute("nombreCompeticion", carreraDao.getCompeticion(codCompeticion).getNombre());

        model.addAttribute("orden", orden);
        model.addAttribute("sexo", sexo);
        model.addAttribute("categoria", categoria);
        model.addAttribute("filtroNombre", filtroNombre);


        if (confirmDeleteCodCarrera != null && !confirmDeleteCodCarrera.isEmpty()) {
            Carrera carreraToDelete = carreraDao.getCarrera(confirmDeleteCodCarrera);
            if (carreraToDelete != null) {
                model.addAttribute("carreraToDelete", carreraToDelete);
            } else {
                model.addAttribute("mensajeError", "La carrera a borrar no existe.");
            }
        }

        return "carrera/listByCompeticion";
    }



    @RequestMapping("/porParticipante/{dni}")
    public String listCarrerasPorParticipante(@PathVariable String dni,
                                              @RequestParam(required = false) String orden,
                                              @RequestParam(required = false) String sexo,
                                              @RequestParam(value = "confirmDesinscripcionCodCarrera", required = false) String confirmDesinscripcionCodCarrera,
                                              @RequestParam(value = "confirmDesinscripcionDniParticipante", required = false) String confirmDesinscripcionDniParticipante,
                                              Model model) {
        List<Carrera> carreras = carreraDao.getCarrerasPorParticipante(dni);

        if (sexo != null && !sexo.isEmpty()) {
            carreras = carreras.stream()
                    .filter(c -> c.getCategoriaSexo().equalsIgnoreCase(sexo))
                    .toList();
        }

        if ("asc".equalsIgnoreCase(orden)) {
            carreras.sort(Comparator.comparing(Carrera::getDistancia));
        } else if ("desc".equalsIgnoreCase(orden)) {
            carreras.sort(Comparator.comparing(Carrera::getDistancia).reversed());
        }

        model.addAttribute("codParticipante", dni);
        model.addAttribute("carreras", carreras);
        model.addAttribute("orden", orden);
        model.addAttribute("sexo", sexo);

        if (confirmDesinscripcionCodCarrera != null && !confirmDesinscripcionCodCarrera.isEmpty() &&
                confirmDesinscripcionDniParticipante != null && !confirmDesinscripcionDniParticipante.isEmpty()) {

            Inscripcion inscripcionToUnenroll = inscripcionDao.getInscripcionPorParticipanteYCarrera(confirmDesinscripcionDniParticipante, confirmDesinscripcionCodCarrera);
            Carrera carreraToUnenroll = carreraDao.getCarrera(confirmDesinscripcionCodCarrera);

            if (inscripcionToUnenroll != null && carreraToUnenroll != null) {
                model.addAttribute("inscripcionToUnenroll", inscripcionToUnenroll);
                model.addAttribute("carreraToUnenroll", carreraToUnenroll);
                model.addAttribute("participanteToUnenrollDni", confirmDesinscripcionDniParticipante);
                model.addAttribute("carreraToUnenrollNombre", carreraToUnenroll.getInicio());
            } else {
                model.addAttribute("mensajeError", "La inscripción o carrera a desinscribir no fue encontrada.");
            }
        }

        return "carrera/listByParticipante";
    }




    private String generateRaceCode() {
        return "R" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    @GetMapping("/{codCarrera}/clasificacion")
    public String verClasificacion(
            @PathVariable String codCarrera,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            @RequestParam(required = false) String nombre,
            @RequestParam(value = "confirmDeleteInscripcionCod", required = false) String confirmDeleteInscripcionCod,
            Model model) {

        Carrera carrera = carreraDao.getCarrera(codCarrera);
        if (carrera == null) {
            return "redirect:/competicion/list";
        }

        List<Inscripcion> clasificacion = inscripcionDao.getClasificacionPorCarrera(codCarrera, orden);

        Map<String, String> nombresParticipantes = new HashMap<>();
        for (Inscripcion inscripcion : clasificacion) {
            Participante p = participanteDao.getParticipante(inscripcion.getDniParticipante());
            if (p != null) {
                String nombreCompleto = p.getNombre() + " " + p.getApellido();
                nombresParticipantes.put(inscripcion.getDniParticipante(), nombreCompleto);
            }
        }

        if (nombre != null && !nombre.trim().isEmpty()) {
            String nombreLower = nombre.toLowerCase();
            clasificacion = clasificacion.stream()
                    .filter(insc -> {
                        String nombreCompleto = nombresParticipantes.get(insc.getDniParticipante());
                        return nombreCompleto != null && nombreCompleto.toLowerCase().contains(nombreLower);
                    })
                    .collect(Collectors.toList());
        }

        model.addAttribute("carrera", carrera);
        model.addAttribute("clasificacion", clasificacion);
        model.addAttribute("nombresParticipantes", nombresParticipantes);
        model.addAttribute("orden", orden);
        model.addAttribute("nombre", nombre);

        if (confirmDeleteInscripcionCod != null && !confirmDeleteInscripcionCod.isEmpty()) {
            Inscripcion inscripcionToDelete = inscripcionDao.getInscripcion(confirmDeleteInscripcionCod);
            if (inscripcionToDelete != null) {
                Participante participanteDelete = participanteDao.getParticipante(inscripcionToDelete.getDniParticipante());
                model.addAttribute("inscripcionToDelete", inscripcionToDelete);
                model.addAttribute("participanteToDeleteNombre", (participanteDelete != null) ? (participanteDelete.getNombre() + " " + participanteDelete.getApellido()) : "participante desconocido");
                model.addAttribute("tiempoToDelete", inscripcionToDelete.getTiempometa() != null ? inscripcionToDelete.getTiempometa().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) : "no registrado");
            } else {
                model.addAttribute("mensajeError", "El tiempo a borrar no existe o la inscripción no fue encontrada.");
            }
        }

        return "carrera/clasificacion";
    }


    @GetMapping("/{codCarrera}/add-tiempo-final")
    public String addTiempoFinalForm(@PathVariable String codCarrera, Model model) {
        Carrera carrera = carreraDao.getCarrera(codCarrera);


        List<Inscripcion> inscripcionesSinTiempo = inscripcionDao.getInscripcionesSinTiempo(codCarrera);

        Map<String, String> nombresParticipantes = new HashMap<>();
        for(Inscripcion i : inscripcionesSinTiempo) {
            Participante p = participanteDao.getParticipante(i.getDniParticipante());
            if (p != null) {
                nombresParticipantes.put(i.getCod(), p.getNombre() + " " + p.getApellido());
            }
        }

        model.addAttribute("inscripcion", new Inscripcion());
        model.addAttribute("carrera", carrera);
        model.addAttribute("inscripcionesSinTiempo", inscripcionesSinTiempo);
        model.addAttribute("nombresParticipantes", nombresParticipantes);

        return "carrera/add_tiempo_final";
    }


    @PostMapping("/add-tiempo-final")
    public String processAddTiempoFinal(
            @RequestParam String codInscripcion,
            @RequestParam String tiempometa,
            @RequestParam String codCarrera) {

        Inscripcion inscripcion = inscripcionDao.getInscripcion(codInscripcion);
        if (inscripcion != null) {
            LocalTime tiempoFinal = LocalTime.parse(tiempometa);
            inscripcion.setTiempometa(tiempoFinal);
            inscripcionDao.updateInscripcion(inscripcion);
        }

        return "redirect:/carrera/" + codCarrera + "/clasificacion";
    }

    @GetMapping("/inscripcion/{codInscripcion}/editar-tiempo")
    public String showUpdateTiempoFinalForm(@PathVariable String codInscripcion, Model model) {
        Inscripcion inscripcion = inscripcionDao.getInscripcion(codInscripcion);
        model.addAttribute("inscripcion", inscripcion);
        model.addAttribute("carrera", carreraDao.getCarrera(inscripcion.getCodCarrera()));
        model.addAttribute("participante", participanteDao.getParticipante(inscripcion.getDniParticipante()));
        return "carrera/update_tiempo_final";
    }

    @PostMapping("/update-tiempo-final")
    public String processUpdateTiempoFinal(@ModelAttribute("inscripcion") Inscripcion inscripcion) {
        inscripcionDao.updateInscripcion(inscripcion);
        return "redirect:/carrera/" + inscripcion.getCodCarrera() + "/clasificacion";
    }


    @GetMapping("/inscripcion/{codInscripcion}/delete-tiempo")
    public String initiateDeleteTiempoFinalConfirmation(@PathVariable String codInscripcion,
                                                        RedirectAttributes redirectAttributes) {
        Inscripcion inscripcion = inscripcionDao.getInscripcion(codInscripcion);
        if (inscripcion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La inscripción con el tiempo a eliminar no fue encontrada.");
            return "redirect:/competicion/list"; // O a una página de error más genérica si no hay carrera asociada
        }
        return "redirect:/carrera/" + inscripcion.getCodCarrera() + "/clasificacion" +
                "?confirmDeleteInscripcionCod=" + codInscripcion;
    }


    @RequestMapping(value="/inscripcion/confirmDeleteTiempo", method = RequestMethod.POST)
    public String confirmDeleteTiempoFinal(@RequestParam("codInscripcionToDelete") String codInscripcion,
                                           @RequestParam("codCarreraRetorno") String codCarreraRetorno, // Para la redirección
                                           RedirectAttributes redirectAttributes) {

        Inscripcion inscripcion = inscripcionDao.getInscripcion(codInscripcion);

        if (inscripcion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "El tiempo final a borrar no fue encontrado.");
            return "redirect:/carrera/" + codCarreraRetorno + "/clasificacion";
        }

        try {
            inscripcion.setTiempometa(null);
            inscripcionDao.updateInscripcion(inscripcion);

            Carrera carrera = carreraDao.getCarrera(inscripcion.getCodCarrera());
            Participante participante = participanteDao.getParticipante(inscripcion.getDniParticipante());
            String nombreParticipante = (participante != null) ? (participante.getNombre() + " " + participante.getApellido()) : "desconocido";
            String nombreCarrera = (carrera != null) ? carrera.getInicio() : "desconocida";


            redirectAttributes.addFlashAttribute("mensajeExito",
                    "El tiempo final del participante '" + nombreParticipante + "' en la carrera '" + nombreCarrera + "' ha sido borrado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error inesperado al intentar borrar el tiempo final: " + e.getMessage());
        }

        return "redirect:/carrera/" + codCarreraRetorno + "/clasificacion";
    }
    @GetMapping("/{codCarrera}/clasificacion_publica")
    public String verClasificacionPublica(
            @PathVariable String codCarrera,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            @RequestParam(required = false) String nombre,
            Model model) {

        verClasificacion(codCarrera, orden, nombre, null, model);
        return "carrera/clasificacion_publica";
    }
}
