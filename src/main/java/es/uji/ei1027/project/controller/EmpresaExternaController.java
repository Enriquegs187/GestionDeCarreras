package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.EmpresaExternaDao;
import es.uji.ei1027.project.model.EmpresaExterna;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/empresaExterna")
public class EmpresaExternaController {
    private EmpresaExternaDao empresaExternaDao;

    @Autowired
    public void setEmpresaExternaDao(EmpresaExternaDao empresaExternaDao) {
        this.empresaExternaDao = empresaExternaDao;
    }

    @RequestMapping("/list")
    public String listEmpresaExterna(Model model) {
        model.addAttribute("empresasExternas", empresaExternaDao.getEmpresaExterna());
        return "empresaExterna/list";
    }

    @RequestMapping(value="/add")
    public String addEmpresaExterna(Model model) {
        model.addAttribute("empresaExterna", new EmpresaExterna());
        return "empresaExterna/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("empresaExterna") EmpresaExterna empresaExterna,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "empresaExterna/add";
        empresaExternaDao.addEmpresaExterna(empresaExterna);
        return "redirect:list";
    }

    @RequestMapping(value="/update/{cif}", method = RequestMethod.GET)
    public String editFichaClub(Model model, @PathVariable String cif) {
        model.addAttribute("empresaExterna", empresaExternaDao.getEmpresaExterna(cif));
        return "empresaExterna/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("empresaExterna") EmpresaExterna empresaExterna,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "empresaExterna/update";
        empresaExternaDao.updateEmpresaExterna(empresaExterna);
        return "redirect:list";
    }

    @RequestMapping(value="/delete/{cif}")
    public String processDelete(@PathVariable String cif) {
        empresaExternaDao.deleteEmpresaExterna(cif);
        return "redirect:../list";
    }
}
