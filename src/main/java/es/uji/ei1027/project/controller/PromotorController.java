package es.uji.ei1027.project.controller;


import es.uji.ei1027.project.dao.PromotorDao;
import es.uji.ei1027.project.dao.UsuarioDao;
import es.uji.ei1027.project.model.Promotor;
import es.uji.ei1027.project.model.UserDetails;
import es.uji.ei1027.project.validator.PromotorValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/promotor")
public class PromotorController {

    private PromotorDao promotorDao;
    private UsuarioDao usuarioDao;


    @Autowired
    public void setPromotorDao(PromotorDao promotorDao) {
        this.promotorDao = promotorDao;
    }

    @Autowired
    public void setUserDao(UsuarioDao usuarioDao) {this.usuarioDao=usuarioDao;}

    @RequestMapping("/index")
    public String acceso(){
        return "promotor/index";
    }

    @RequestMapping("/list")
    public String listPromotores(Model model){
        model.addAttribute("promotores", promotorDao.getPromotores());
        return "promotor/list";
    }

    @RequestMapping(value="/add")
    public String addPromotor(Model model){
        model.addAttribute("promotor", new Promotor());
        return "promotor/add";
    }

    @Transactional
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("promotor") Promotor promotor,
                                   @RequestParam("password") String password,
                                   BindingResult bindingResult,
                                   HttpSession session) {
        PromotorValidator promotorValidator = new PromotorValidator();
        promotorValidator.validate(promotor, bindingResult);
        if (bindingResult.hasErrors()){
            return "promotor/add";
        }
        try {
            UserDetails user = new UserDetails();
            user.setUsername(promotor.getDni());
            user.setPassword(password);
            user.setTipoUsuario("promotor");
            usuarioDao.addUser(user);
            promotorDao.addPromotor(promotor);

            session.setAttribute("user", user);
            session.setAttribute("promotor", promotor);

            return "redirect:index";
        }catch (DuplicateKeyException e){
            bindingResult.rejectValue("dni", "duplicado", "Este DNI ya está registrado como promotor.");
            return "promotor/add";
        } catch (Exception e){
            throw new ProjectException("Error al añadir nuevo promotor" + e.getMessage(), "Error inesperado");

        }

    }

    @RequestMapping(value="/update/{dni}", method = RequestMethod.GET)
    public String editPromotor(Model model, @PathVariable String dni){
        model.addAttribute("promotor", promotorDao.getPromotor(dni));
        return "promotor/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("promotor") Promotor promotor,
            BindingResult bindingResult,
            HttpSession session) {

        PromotorValidator promotorValidator = new PromotorValidator();
        promotorValidator.validate(promotor, bindingResult);
        if (bindingResult.hasErrors()){
            return "promotor/update";
        }
        try {
            promotorDao.updatePromotor(promotor);
            session.setAttribute("promotor", promotor);

            return "redirect:areaPersonal";
        }catch (DuplicateKeyException e){
            bindingResult.rejectValue("dni", "duplicado", "Este DNI ya está registrado como promotor.");
            return "promotor/update";
        }catch (Exception e){
            throw new ProjectException("Error al modificar promotor" + e.getMessage(), "Error inesperado");

        }
    }

    @RequestMapping(value="/delete/{dni}")
    public String processDelete(@PathVariable String dni) {
        promotorDao.deletePromotor(dni);
        return "redirect:../list";
    }

    @RequestMapping(value = "/areaPersonal", method = RequestMethod.GET)
    public String viewPromotorArea(Model model, HttpSession session) {
        Promotor promotor = (Promotor) session.getAttribute("promotor");
        if (promotor != null) {
            model.addAttribute("promotor", promotor);
            return "promotor/areaPersonal";
        } else {
            return "redirect:/login";
        }
    }

    @RequestMapping(value="/ajustes", method = RequestMethod.GET)
    public String mostrarAjustes(HttpSession session, Model model) {
        if (session.getAttribute("user") == null)
            return "redirect:/login/promotor";
        Promotor promotor = (Promotor) session.getAttribute("user");
        model.addAttribute("promotor", promotor);
        return "promotor/ajustes";
    }


}
