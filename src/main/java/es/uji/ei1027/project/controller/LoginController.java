package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.ParticipanteDao;
import es.uji.ei1027.project.dao.PromotorDao;
import es.uji.ei1027.project.dao.UsuarioDao;
import es.uji.ei1027.project.model.Participante;
import es.uji.ei1027.project.model.Promotor;
import es.uji.ei1027.project.validator.UserValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import es.uji.ei1027.project.model.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginController {

    private UsuarioDao usuarioDao;
    private PromotorDao promotorDao;
    private ParticipanteDao participanteDao;

    @Autowired
    public void setUserDao(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Autowired
    public void setPromotorDao(PromotorDao promotorDao) {
        this.promotorDao = promotorDao;
    }

    @Autowired
    public void setParticipanteDao(ParticipanteDao participanteDao) {
        this.participanteDao = participanteDao;
    }

    @GetMapping("/login/promotor")
    public String loginPromotorForm(Model model, HttpSession session) {
        if (session.getAttribute("user") != null && ((UserDetails) session.getAttribute("user")).getTipoUsuario().equals("promotor")) {
            return "redirect:/promotor/index";
        }
        model.addAttribute("user", new UserDetails());
        return "login";
    }

    @PostMapping("/login/promotor")
    public String checkLoginPromotor(@ModelAttribute("user") UserDetails user,
                                     BindingResult bindingResult, HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        UserValidator userValidator = new UserValidator();
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "login";
        }

        UserDetails loggedUser = usuarioDao.loadUserByUsername(user.getUsername(), user.getPassword());
        if (loggedUser == null || !loggedUser.getTipoUsuario().equals("promotor")) {
            bindingResult.rejectValue("password", "badpw", "Credenciales incorrectas o usuario no es promotor.");
            return "login";
        }

        session.setAttribute("user", loggedUser);

        String nextUrl = (String) session.getAttribute("nextUrl");
        if (nextUrl != null) {
            session.removeAttribute("nextUrl");
            String promotorDni = loggedUser.getUsername();
            Promotor promotor = promotorDao.getPromotor(promotorDni);
            if (promotor != null) {
                session.setAttribute("promotor", promotor);
            }
            return "redirect:" + nextUrl;
        }

        String promotorDni = loggedUser.getUsername();
        Promotor promotor = promotorDao.getPromotor(promotorDni);
        if (promotor != null) {
            session.setAttribute("promotor", promotor);
            return "redirect:/promotor/index";
        } else {
            session.invalidate();
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró información del promotor.");
            return "redirect:/login/promotor";
        }
    }


    @GetMapping("/login/participante")
    public String loginParticipanteForm(Model model, HttpSession session) {
        if (session.getAttribute("user") != null && ((UserDetails) session.getAttribute("user")).getTipoUsuario().equals("participante")) {
            return "redirect:/participante/index";
        }
        model.addAttribute("user", new UserDetails());
        return "loginParticipante";
    }

    @PostMapping("/login/participante")
    public String checkLoginParticipante(@ModelAttribute("user") UserDetails user,
                                         BindingResult bindingResult, HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        UserValidator userValidator = new UserValidator();
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "loginParticipante";
        }

        UserDetails loggedUser = usuarioDao.loadUserByUsername(user.getUsername(), user.getPassword());
        // Añadir comprobación de rol específico
        if (loggedUser == null || !loggedUser.getTipoUsuario().equals("participante")) {
            bindingResult.rejectValue("password", "badpw", "Credenciales incorrectas o usuario no es participante.");
            return "loginParticipante";
        }

        session.setAttribute("user", loggedUser);

        String nextUrl = (String) session.getAttribute("nextUrl");
        if (nextUrl != null) {
            session.removeAttribute("nextUrl");
            // Cargar y setear promotor/participante en sesión antes de redirigir
            String participanteDni = loggedUser.getUsername();
            Participante participante = participanteDao.getParticipante(participanteDni);
            if (participante != null) {
                session.setAttribute("participante", participante);
            }
            return "redirect:" + nextUrl;
        }

        String participanteDni = loggedUser.getUsername();
        Participante participante = participanteDao.getParticipante(participanteDni);
        if (participante != null) {
            session.setAttribute("participante", participante);
            return "redirect:/participante/index";
        } else {
            session.invalidate();
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró información del participante.");
            return "redirect:/login/participante";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "Sesión cerrada correctamente.");
        return "redirect:/";
    }
}
