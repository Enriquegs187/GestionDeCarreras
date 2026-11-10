package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.SponsorDao;
import es.uji.ei1027.project.model.Carrera;
import es.uji.ei1027.project.model.Sponsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sponsor")
public class SponsorController {

    private SponsorDao sponsorDao;

    @Autowired
    public void setSponsorDao(SponsorDao sponsorDao) {
        this.sponsorDao = sponsorDao;
    }

    @RequestMapping("/list")
    public String listSponsors(@RequestParam(required = false) String nombre,
                               @RequestParam(required = false) String orden,
                               @RequestParam(value = "confirmDelete", required = false) String confirmDeleteCodSponsor,
                               Model model) {

        List<Sponsor> sponsors = sponsorDao.getSponsors();

        if (nombre != null && !nombre.trim().isEmpty()) {
            String nombreLower = nombre.toLowerCase();
            sponsors = sponsors.stream()
                    .filter(s -> s.getNombre() != null && s.getNombre().toLowerCase().contains(nombreLower))
                    .collect(Collectors.toList());
        }

        if ("asc".equalsIgnoreCase(orden)) {
            sponsors.sort(Comparator.comparing(Sponsor::getNombre, String.CASE_INSENSITIVE_ORDER));
        } else if ("desc".equalsIgnoreCase(orden)) {
            sponsors.sort(Comparator.comparing(Sponsor::getNombre, String.CASE_INSENSITIVE_ORDER).reversed());
        }

        model.addAttribute("sponsors", sponsors);
        model.addAttribute("nombre", nombre);
        model.addAttribute("orden", orden);

        if (confirmDeleteCodSponsor != null && !confirmDeleteCodSponsor.isEmpty()) {
            Sponsor sponsorToDelete = sponsorDao.getSponsor(confirmDeleteCodSponsor);
            if (sponsorToDelete != null) {
                model.addAttribute("sponsorToDelete", sponsorToDelete);
            } else {
                model.addAttribute("mensajeError", "El sponsor a a borrar no existe.");
            }
        }

        return "sponsor/list";
    }

    @RequestMapping(value = "/delete/{cod}", method = RequestMethod.GET)
    public String initiateDeleteConfirmation(@PathVariable String cod, RedirectAttributes redirectAttributes) {
        Sponsor sponsor = sponsorDao.getSponsor(cod);
        if (sponsor == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "El sponsor a eliminar no fue encontrada.");
            return "redirect:/sponsor/list";
        }
        return "redirect:/sponsor/list?confirmDelete=" + cod;
    }


    @RequestMapping(value="/add")
    public String addSponsor(Model model) {
        model.addAttribute("sponsor", new Sponsor());
        return "sponsor/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("sponsor") Sponsor sponsor,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "sponsor/add";

        String generatedCode = generateSponsorCode();
        sponsor.setCod(generatedCode);

        sponsorDao.addSponsor(sponsor);
        return "redirect:/sponsor/list";
    }


    @RequestMapping(value="/update/{cod}", method = RequestMethod.GET)
    public String editSponsor(Model model, @PathVariable String cod) {
        model.addAttribute("sponsor", sponsorDao.getSponsor(cod));
        return "sponsor/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("sponsor") Sponsor sponsor,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "sponsor/update";
        sponsorDao.updateSponsor(sponsor);
        return "redirect:list";
    }

    @RequestMapping(value="/confirmDelete", method = RequestMethod.POST)
    public String processDelete(@RequestParam("codSponsorToDelete") String cod,
                                RedirectAttributes redirectAttributes) {
        Sponsor sponsor = sponsorDao.getSponsor(cod);

        if (sponsor == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "El sponsor que intentas borrar no existe.");
            return "redirect:/sponsor/list";
        }

        try {
            sponsorDao.deleteSponsor(cod);
            redirectAttributes.addFlashAttribute("mensajeExito", "Sponsor borrado correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se puede borrar el sponsor. Asegúrate de que no esté asociado a ninguna competición.");

            return "redirect:/sponsor/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error inesperado al intentar borrar el sponsor.");
            return "redirect:/sponsor/list";
        }
        return "redirect:/sponsor/list";
    }


    private String generateSponsorCode() {
        return "S" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
