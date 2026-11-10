package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.FichaClubDao;
import es.uji.ei1027.project.model.FichaClub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fichaClub")
public class FichaClubController {

    private FichaClubDao fichaClubDao;

    @Autowired
    public void setFichaClubDao(FichaClubDao fichaClubDao) {
        this.fichaClubDao = fichaClubDao;
    }

    @RequestMapping("/list")
    public String listFichasClub(Model model) {
        model.addAttribute("fichasClub", fichaClubDao.getFichasClub());
        return "fichaClub/list";
    }

    @RequestMapping(value="/add")
    public String addFichaClub(Model model) {
        model.addAttribute("fichaClub", new FichaClub());
        return "fichaClub/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("fichaClub") FichaClub ficha,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "fichaClub/add";
        fichaClubDao.addFichaClub(ficha);
        return "redirect:list";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editFichaClub(Model model, @PathVariable String id) {
        model.addAttribute("fichaClub", fichaClubDao.getFichaClub(id));
        return "fichaClub/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("fichaClub") FichaClub ficha,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "fichaClub/update";
        fichaClubDao.updateFichaClub(ficha);
        return "redirect:list";
    }

    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable String id) {
        fichaClubDao.deleteFichaClub(id);
        return "redirect:../list";
    }
}
