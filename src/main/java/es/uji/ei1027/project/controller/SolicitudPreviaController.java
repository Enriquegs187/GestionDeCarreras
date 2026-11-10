package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.SolicitudPreviaDao;
import es.uji.ei1027.project.model.SolicitudPrevia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/solicitudPrevia")
public class SolicitudPreviaController {

    private SolicitudPreviaDao solicitudPreviaDao;

    @Autowired
    public void setSolicitudPreviaDao(SolicitudPreviaDao solicitudPreviaDao) {
        this.solicitudPreviaDao = solicitudPreviaDao;
    }

    @RequestMapping("/list")
    public String listSolicitudesPrevias(Model model) {
        model.addAttribute("solicitudesPrevias", solicitudPreviaDao.getSolicitudesPrevias());
        return "solicitudPrevia/list";
    }

    @RequestMapping(value="/add")
    public String addSolicitudPrevia(Model model) {
        model.addAttribute("solicitudPrevia", new SolicitudPrevia());
        return "solicitudPrevia/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("solicitudPrevia") SolicitudPrevia solicitud,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "solicitudPrevia/add";
        solicitudPreviaDao.addSolicitudPrevia(solicitud);
        return "redirect:list";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editSolicitudPrevia(Model model, @PathVariable String id) {
        model.addAttribute("solicitudPrevia", solicitudPreviaDao.getSolicitudPrevia(id));
        return "solicitudPrevia/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("solicitudPrevia") SolicitudPrevia solicitud,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "solicitudPrevia/update";
        solicitudPreviaDao.updateSolicitudPrevia(solicitud);
        return "redirect:list";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        solicitudPreviaDao.deleteSolicitudPrevia(id);
        return "redirect:../list";
    }
}
