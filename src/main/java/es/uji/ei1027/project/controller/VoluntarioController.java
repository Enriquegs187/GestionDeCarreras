package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.AsignanDao;
import es.uji.ei1027.project.dao.VoluntarioDao;
import es.uji.ei1027.project.model.Voluntario;
import es.uji.ei1027.project.validator.VoluntarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/voluntario")
public class VoluntarioController {

    private VoluntarioDao voluntarioDao;

    @Autowired
    public void setVoluntarioDao(VoluntarioDao voluntarioDao) {
        this.voluntarioDao = voluntarioDao;
    }


    private AsignanDao asignanDao;

    @Autowired
    public void setAsignanDao(AsignanDao asignanDao) {this.asignanDao = asignanDao;}

    @RequestMapping("/list")
    public String listVoluntarios(@RequestParam(required = false) String nombre,
                                  @RequestParam(required = false) String orden,
                                  Model model) {

        List<Voluntario> voluntarios = voluntarioDao.getVoluntarios();

        // Filtrado por nombre (contiene, insensible a mayúsculas)
        if (nombre != null && !nombre.trim().isEmpty()) {
            String nombreLower = nombre.toLowerCase();
            voluntarios = voluntarios.stream()
                    .filter(v -> v.getNombre() != null && v.getNombre().toLowerCase().contains(nombreLower))
                    .collect(Collectors.toList());
        }

        //  Ordenación por nombre y estado
        if (orden != null) {
            switch (orden) {
                case "asc":
                    voluntarios.sort(Comparator.comparing(Voluntario::getNombre, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "desc":
                    voluntarios.sort(Comparator.comparing(Voluntario::getNombre, String.CASE_INSENSITIVE_ORDER).reversed());
                    break;
                case "estado_activo_primero":
                    voluntarios.sort(Comparator.comparing(Voluntario::isEstado).reversed());
                    break;
                case "estado_baja_primero":
                    voluntarios.sort(Comparator.comparing(Voluntario::isEstado));
                    break;
            }
        }

        model.addAttribute("voluntarios", voluntarios);
        model.addAttribute("nombre", nombre);
        model.addAttribute("orden", orden);
        return "voluntario/list";
    }


    @GetMapping("/add")
    public String addVoluntario(Model model) {
        Voluntario voluntario = new Voluntario();
        model.addAttribute("voluntario", voluntario);
        return "voluntario/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("voluntario") Voluntario voluntario, BindingResult bindingResult) {
        VoluntarioValidator voluntarioValidator = new VoluntarioValidator();
        voluntarioValidator.validate(voluntario, bindingResult);
        if (bindingResult.hasErrors()){
            return "voluntario/add";
        }
        voluntarioDao.addVoluntario(voluntario);
        return "redirect:list";
    }

    @RequestMapping(value="/update/{dni}", method = RequestMethod.GET)
    public String editVoluntario(Model model, @PathVariable String dni) {
        model.addAttribute("voluntario", voluntarioDao.getVoluntario(dni));
        return "voluntario/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("voluntario") Voluntario voluntario, BindingResult bindingResult) {
        VoluntarioValidator voluntarioValidator = new VoluntarioValidator();
        voluntarioValidator.validate(voluntario, bindingResult);
        if (bindingResult.hasErrors()){
            return "voluntario/update";
        }
        voluntarioDao.updateVoluntario(voluntario);
        return "redirect:list";
    }

    @PostMapping("/confirmDeleteVoluntario")
    public String processDelete(
            @RequestParam("dniVoluntario") String dniVoluntario,
            RedirectAttributes redirectAttributes) {

        try {
            if (asignanDao.tieneAsignacionesActivas(dniVoluntario)) {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "No se pudo borrar el voluntario porque sigue teniendo asignaciones activas.");
                return "redirect:/voluntario/list";
            }

            voluntarioDao.deleteVoluntario(dniVoluntario);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Voluntario eliminado correctamente!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo eliminar el voluntario debido a restricciones de datos (ej. está asignado a un punto de control).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error inesperado al intentar eliminar el voluntario: " + e.getMessage());
        }
        return "redirect:/voluntario/list";
    }

    @GetMapping("/delete/{dni}")
    public String initiateDeleteVoluntarioConfirmation(
            @PathVariable String dni,
            Model model,
            RedirectAttributes redirectAttributes) {

        Voluntario voluntarioToDelete = voluntarioDao.getVoluntario(dni);
        if (voluntarioToDelete == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "El voluntario a borrar no fue encontrado.");
            return "redirect:/voluntario/list";
        }

        if (asignanDao.tieneAsignacionesActivas(dni)) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se puede borrar al voluntario '" + voluntarioToDelete.getNombre() + " " + voluntarioToDelete.getApellido() +
                            "' porque está asignado a uno o más puntos de control. Por favor, desasígnelo primero.");
            return "redirect:/voluntario/list";
        }

        model.addAttribute("voluntarioToDelete", voluntarioToDelete);

        List<Voluntario> voluntarios = voluntarioDao.getVoluntarios();
        model.addAttribute("voluntarios", voluntarios);

        return "voluntario/list";
    }

    @GetMapping("/cambiarEstado/{dni}")
    public String cambiarEstadoVoluntario(@PathVariable String dni,
                                          @RequestParam boolean nuevoEstado,
                                          RedirectAttributes redirectAttributes) {
        Voluntario voluntario = voluntarioDao.getVoluntario(dni);
        if (voluntario == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Voluntario no encontrado.");
            return "redirect:/voluntario/list";
        }

        if (!nuevoEstado) {
            if (asignanDao.tieneAsignacionesActivas(dni)) {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "No se puede dar de baja al voluntario '" + voluntario.getNombre() + " " + voluntario.getApellido() +
                                "' porque está asignado a uno o más puntos de control. Por favor, desasígnelo primero.");
                return "redirect:/voluntario/list";
            }
        }

        try {
            voluntario.setEstado(nuevoEstado);
            voluntarioDao.updateVoluntario(voluntario);
            String estadoMsg = nuevoEstado ? "Activado" : "Dado de Baja";
            redirectAttributes.addFlashAttribute("mensajeExito", "Voluntario '" + voluntario.getNombre() + " " + voluntario.getApellido() + "' " + estadoMsg + " correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al cambiar el estado del voluntario: " + e.getMessage());
        }

        return "redirect:/voluntario/list";
    }




}
