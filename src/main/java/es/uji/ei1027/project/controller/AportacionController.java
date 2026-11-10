package es.uji.ei1027.project.controller;


import es.uji.ei1027.project.dao.AportacionDao;
import es.uji.ei1027.project.dao.CompeticionDao;
import es.uji.ei1027.project.dao.SponsorDao;
import es.uji.ei1027.project.model.Aportacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aportacion")
public class AportacionController {
    private AportacionDao aportacionDao;
    private CompeticionDao competicionDao;
    private SponsorDao sponsorDao;
    @Autowired
    public void setAportacionDao(AportacionDao aportacionDao) {
        this.aportacionDao=aportacionDao;
    }

    @Autowired
    public void setCompeticionDao(CompeticionDao competicionDao) {  this.competicionDao = competicionDao;}

    @Autowired
    public void setSponsorDao(SponsorDao sponsorDao) {this.sponsorDao = sponsorDao;}

    @RequestMapping("/list")
    public String listAportacion(Model model) {
        model.addAttribute("aportacion", aportacionDao.getAportacion());
        return "aportacion/list";
    }

    @RequestMapping(value="/add/{codCompeticion}", method=RequestMethod.GET)
    public String addAportacion(@PathVariable String codCompeticion, Model model) {
        model.addAttribute("aportacion", new Aportacion());
        model.addAttribute("competicion", competicionDao.getCompeticion(codCompeticion));
        model.addAttribute("sponsors", sponsorDao.getSponsors());
        return "aportacion/add";
    }

    @RequestMapping(value = "/confirmDeleteAportacion", method = RequestMethod.POST)
    public String processDelete(@RequestParam("codCompeticion") String codCompeticion,
                                                   @RequestParam("codSponsorToDelete") String codSponsor,
                                                   RedirectAttributes redirectAttributes) {
        try {
            aportacionDao.deleteAportacion(codCompeticion, codSponsor);
            redirectAttributes.addFlashAttribute("mensajeExito", "Sponsor desvinculado de la competición correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Error");
        }

        return "redirect:/competicion/sponsor/porCompeticion/" + codCompeticion;
    }

    @PostMapping(value="/addSponsor/{codCompeticion}")
    public String addSponsorToCompeticion(@PathVariable String codCompeticion, @ModelAttribute("aportacion") Aportacion aportacion) {
        aportacion.setCodCompeticion(codCompeticion);
        aportacionDao.addAportacion(aportacion);
        return "redirect:/competicion/sponsor/porCompeticion/" + codCompeticion;
    }

    @RequestMapping(value = "/delete/{codCompeticion}/{codSponsor}", method = RequestMethod.GET)
    public String initiateDeleteConfirmation(@PathVariable String codCompeticion,
                                             @PathVariable String codSponsor,
                                             RedirectAttributes redirectAttributes) {

        Aportacion aportacion = aportacionDao.getAportacion(codCompeticion, codSponsor);
        if (aportacion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La aportación a eliminar no fue encontrada.");
            return "redirect:/aportacion/competicion/sponsor/porCompeticion/" + codCompeticion;
        }

        return "redirect:/competicion/sponsor/porCompeticion/" + codCompeticion + "?confirmDeleteCodSponsor=" + codSponsor;
    }
}
