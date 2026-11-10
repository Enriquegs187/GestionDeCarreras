package es.uji.ei1027.project.controller;



import es.uji.ei1027.project.dao.CompeticionDao;
import es.uji.ei1027.project.dao.PromotorDao;
import es.uji.ei1027.project.dao.SponsorDao;
import es.uji.ei1027.project.model.Aportacion;
import es.uji.ei1027.project.model.Competicion;
import es.uji.ei1027.project.model.Promotor;
import es.uji.ei1027.project.model.Sponsor;
import es.uji.ei1027.project.validator.CompeticionValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/competicion")
public class CompeticionController {
    private CompeticionDao competicionDao;
    private SponsorDao sponsorDao;

    @Autowired
    public void setCompeticionDao(CompeticionDao competicionDao) {
        this.competicionDao = competicionDao;
    }

    @Autowired
    public void setSponsorDao(SponsorDao sponsorDao) {this.sponsorDao = sponsorDao;}

    @RequestMapping("/list")
    public String listCompeticiones(Model model,
                                    @RequestParam(required = false) String nombre,
                                    @RequestParam(required = false) String orden,
                                    @RequestParam(value = "confirmDeleteCod", required = false) String confirmDeleteCod) {
        List<Competicion> lista = competicionDao.getCompeticion();

        if (nombre != null && !nombre.isEmpty()) {
            lista = lista.stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .toList();
        }

        if (orden != null) {
            switch (orden) {
                case "nombre_asc" -> lista = lista.stream()
                        .sorted(Comparator.comparing(Competicion::getNombre))
                        .toList();
                case "nombre_desc" -> lista = lista.stream()
                        .sorted(Comparator.comparing(Competicion::getNombre).reversed())
                        .toList();
                case "fecha_asc" -> lista = lista.stream()
                        .sorted(Comparator.comparing(Competicion::getFechaInicio))
                        .toList();
                case "fecha_desc" -> lista = lista.stream()
                        .sorted(Comparator.comparing(Competicion::getFechaInicio).reversed())
                        .toList();
            }
        }

        model.addAttribute("competicion", lista);
        model.addAttribute("nombreBuscado", nombre);
        model.addAttribute("orden", orden);

        if (confirmDeleteCod != null && !confirmDeleteCod.isEmpty()) {
            Competicion competicionToDelete = competicionDao.getCompeticion(confirmDeleteCod);
            if (competicionToDelete != null) {
                model.addAttribute("competicionToDelete", competicionToDelete);
            } else {
                model.addAttribute("mensajeError", "La competición a borrar no existe.");
            }
        }

        return "competicion/list";
    }


    @RequestMapping("/viewersList")
    public String listarCompeticionesFiltradas(
            Model model,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "orden", required = false) String orden,
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            HttpSession session) {

        List<Competicion> lista = competicionDao.getCompeticion();

        if (nombre != null && !nombre.trim().isEmpty()) {
            String nombreLower = nombre.toLowerCase();
            lista.removeIf(c -> c.getNombre() == null || !c.getNombre().toLowerCase().contains(nombreLower));
        }

        if (orden != null) {
            switch (orden) {
                case "nombre_asc":
                    lista.sort(Comparator.comparing(Competicion::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)));
                    break;
                case "nombre_desc":
                    lista.sort(Comparator.comparing(Competicion::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)).reversed());
                    break;
                case "fecha_asc":
                    lista.sort(Comparator.comparing(Competicion::getFechaInicio, Comparator.nullsLast(LocalDate::compareTo)));
                    break;
                case "fecha_desc":
                    lista.sort(Comparator.comparing(Competicion::getFechaInicio, Comparator.nullsLast(LocalDate::compareTo)).reversed());
                    break;
            }
        }

        session.setAttribute("returnUrl", returnUrl != null ? returnUrl : "/participante/index");

        model.addAttribute("competicion", lista);
        model.addAttribute("nombreBuscado", nombre);
        model.addAttribute("orden", orden);

        return "competicion/viewersList";
    }


    @RequestMapping(value="/add")
    public String addCompeticiones(Model model, HttpSession session) {
        Competicion competicion = new Competicion();
        Promotor promotor = (Promotor) session.getAttribute("promotor");
        if (promotor != null) {
            competicion.setDniPromotor(promotor.getDni());
            model.addAttribute("dniPromotor", promotor.getDni());
        } else {
            model.addAttribute("dniPromotor", null);
        }
        model.addAttribute("competicion", competicion);
        return "competicion/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("competicion") Competicion competicion,
                                   BindingResult bindingResult) {

        CompeticionValidator competicionValidator = new CompeticionValidator();
        competicionValidator.validate(competicion, bindingResult);
        if (bindingResult.hasErrors()) {
            return "competicion/add";
        }

        String generatedCode = generateCompeticionCode();
        competicion.setCod(generatedCode);

        competicionDao.addCompeticion(competicion);

        return "redirect:list";
    }

    @RequestMapping(value="/update/{cod}", method = RequestMethod.GET)
    public String editClub(Model model, @PathVariable String cod) {
        model.addAttribute("competicion", competicionDao.getCompeticion(cod));
        return "competicion/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("competicion") Competicion competicion,
            BindingResult bindingResult) {
        CompeticionValidator competicionValidator = new CompeticionValidator();
        competicionValidator.validate(competicion, bindingResult);
        if (bindingResult.hasErrors()) {
            return "competicion/update";
        }
        competicionDao.updateCompeticion(competicion);
        return "redirect:list";
    }

    @RequestMapping(value="/confirmDeleteCompeticion", method = RequestMethod.POST)
    public String processConfirmedDeleteCompeticion(@RequestParam("codCompeticionToDelete") String cod,
                                                    RedirectAttributes redirectAttributes) {
        try {
            competicionDao.deleteCompeticion(cod);
            redirectAttributes.addFlashAttribute("mensajeExito", "Competición borrada correctamente.");
        } catch (DataIntegrityViolationException e) {

            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se puede borrar la competición. Asegúrate de que no tenga carreras o sponsors asociados.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error inesperado al intentar borrar la competición.");
        }

        return "redirect:/competicion/list";
    }

    @RequestMapping(value="/delete/{cod}", method = RequestMethod.GET)
    public String initiateDeleteConfirmation(@PathVariable String cod, RedirectAttributes redirectAttributes) {
        Competicion competicion = competicionDao.getCompeticion(cod);
        if (competicion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La competición a eliminar no fue encontrada.");
            return "redirect:/competicion/list";
        }
        return "redirect:/competicion/list?confirmDeleteCod=" + cod;
    }

    private String generateCompeticionCode() {
        return "C" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @RequestMapping("/{cod}/addSponsor")
    public String showAddSponsorForm(@PathVariable String cod, Model model) {
        model.addAttribute("competicion", competicionDao.getCompeticion(cod));
        model.addAttribute("sponsors", sponsorDao.getSponsors());
        model.addAttribute("aportacion", new Aportacion());
        return "aportacion/add";
    }

    @RequestMapping("/sponsor/porCompeticion/{cod}")
    public String verSponsorsPorCompeticion(
            @PathVariable String cod,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String orden,
            @RequestParam(value = "confirmDeleteCodSponsor", required = false) String confirmDeleteCodSponsor,

            Model model) {

        Competicion competicion = competicionDao.getCompeticion(cod);
        List<Sponsor> sponsors = sponsorDao.getSponsorsByCompeticion(cod);

        // Filtrar por nombre si se ha enviado parámetro
        if (nombre != null && !nombre.trim().isEmpty()) {
            String nombreLower = nombre.toLowerCase();
            sponsors = sponsors.stream()
                    .filter(s -> s.getNombre() != null && s.getNombre().toLowerCase().contains(nombreLower))
                    .collect(Collectors.toList());
        }

        // Ordenar por nombre si se ha enviado parámetro
        if ("asc".equalsIgnoreCase(orden)) {
            sponsors.sort(Comparator.comparing(Sponsor::getNombre, String.CASE_INSENSITIVE_ORDER));
        } else if ("desc".equalsIgnoreCase(orden)) {
            sponsors.sort(Comparator.comparing(Sponsor::getNombre, String.CASE_INSENSITIVE_ORDER).reversed());
        }

        model.addAttribute("competicion", competicion);
        model.addAttribute("sponsors", sponsors);
        model.addAttribute("nombre", nombre);
        model.addAttribute("orden", orden);

        if (confirmDeleteCodSponsor != null && !confirmDeleteCodSponsor.isEmpty()) {
            Sponsor sponsorToDelete = sponsorDao.getSponsor(confirmDeleteCodSponsor);
            if (sponsorToDelete != null) {
                model.addAttribute("sponsorToDelete", sponsorToDelete);
            } else {
                model.addAttribute("mensajeError", "El sponsor a desvincular no existe.");
            }
        }

        return "sponsor/porCompeticion";
    }


}
