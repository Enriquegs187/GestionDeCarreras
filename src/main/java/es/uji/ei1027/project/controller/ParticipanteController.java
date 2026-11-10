package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.ClubDao;
import es.uji.ei1027.project.dao.UsuarioDao;
import es.uji.ei1027.project.model.Club;
import es.uji.ei1027.project.model.UserDetails;
import es.uji.ei1027.project.validator.ParticipanteValidator;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import es.uji.ei1027.project.model.Participante;
import es.uji.ei1027.project.dao.ParticipanteDao;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/participante")
public class ParticipanteController {
    private ParticipanteDao participanteDao;
    private UsuarioDao usuarioDao;
    private ClubDao clubDao;

    private static final Logger LOG = LoggerFactory.getLogger(ParticipanteController.class);


    @Autowired
    public void setParticipanteDao(ParticipanteDao participanteDao) {
        this.participanteDao=participanteDao;
    }

    @Autowired
    public void setUserDao(UsuarioDao usuarioDao) {this.usuarioDao=usuarioDao;}

    @Autowired
    public void setClubDao(ClubDao clubDao) {this.clubDao=clubDao;}

    @RequestMapping("/list")
    public String listParticipantes(Model model) {
        model.addAttribute("participantes", participanteDao.getParticipantes());
        return "participante/list";
    }

    @GetMapping("/add")
    public String addParticipante(Model model) {
        model.addAttribute("participante", new Participante());
        model.addAttribute("clubs", clubDao.getClubs());
        return "participante/add";
    }

    @Transactional
    @PostMapping(value="/add")
    public String processAddSubmit(@ModelAttribute("participante") Participante participante,
                                   BindingResult bindingResult,
                                   @RequestParam("password") String password,
                                   Model model,
                                   HttpSession session) {
        ParticipanteValidator validator = new ParticipanteValidator();
        validator.validate(participante, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("clubs", clubDao.getClubs());
            return "participante/add";
        }
        try {
            UserDetails user = new UserDetails();
            user.setUsername(participante.getDni());
            user.setPassword(password);
            user.setTipoUsuario("participante");
            usuarioDao.addUser(user);

            if (participante.getClubCif() != null && participante.getClubCif().isEmpty()) {
                participante.setClubCif(null);
                LOG.info("ClubCif ajustado a null porque era una cadena vacía.");
            }


            participanteDao.addParticipante(participante);

            session.setAttribute("user", user);
            session.setAttribute("participante", participante);

        }catch (DuplicateKeyException e){
            bindingResult.rejectValue("dni", "duplicate", "Este DNI ya está registrado.");
        }catch (Exception e){
            throw new ProjectException("Error al procesar el registro: " + e.getMessage(), "Error de Registro");

        }
        return "redirect:index";
    }

    @GetMapping(value="/update/{dni}")
    public String editParticipante(Model model, @PathVariable String dni) {
        model.addAttribute("participante", participanteDao.getParticipante(dni));
        return "participante/update";
    }

    @PostMapping(value="/update")
    public String processUpdateSubmit(
            @ModelAttribute("participante") Participante participante,
            BindingResult bindingResult, Model model,
            HttpSession session) {

        if (participante.getClubCif() != null && participante.getClubCif().isEmpty()) {
            participante.setClubCif(null);
            LOG.info("ClubCif ajustado a null porque era una cadena vacía.");
        }

        ParticipanteValidator validator = new ParticipanteValidator();
        validator.validate(participante, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("clubs", clubDao.getClubs());
            return "participante/update";
        }
        participanteDao.updateParticipante(participante);
        session.setAttribute("participante", participante);

        return "redirect:/participante/areaPersonalAtleta";
    }

    @RequestMapping(value="/delete/{dni}")
    public String processDelete(@PathVariable String dni) {
        usuarioDao.deleteUser(dni);
        participanteDao.deleteParticipante(dni);
        return "redirect:../list";
    }

    @GetMapping("/index")
    public String indexParticipante(HttpSession session, Model model) {
        String dni = (String) session.getAttribute("dni");
        Participante participante = participanteDao.getParticipante(dni);

        if (participante != null) {
            model.addAttribute("participante", participante);

            if (participante.getClubCif() != null) {
                Club club = clubDao.getClub(participante.getClubCif());
                model.addAttribute("clubNombre", club.getNombre());
            }
        } else {
            model.addAttribute("participante", new Participante());
        }

        return "participante/index";
    }

    @PostMapping("/guardar")
    public String guardarParticipante(@ModelAttribute Participante participante) {
        if (participante.getClubCif() != null || participante.getClubCif().isEmpty()) {
            participante.setClubCif(null);
        }
        participanteDao.updateParticipante(participante);
        return "redirect:/participante/index";
    }


    @GetMapping(value = "/areaPersonalAtleta")
    public String viewParticipanteArea(Model model, HttpSession session) {
        Participante participante = (Participante) session.getAttribute("participante");

        if (participante != null) {
            model.addAttribute("participante", participante);

            if (participante.getClubCif() != null) {
                Club club = clubDao.getClub(participante.getClubCif());
                model.addAttribute("clubNombre", club.getNombre());
            }

            return "participante/areaPersonalAtleta";
        } else {
            return "redirect:/participante/index";
        }
    }

    @GetMapping("/participante/confirm-delete/{dni}")
    public String confirmDelete(@PathVariable String dni, Model model) {
        Participante participante = participanteDao.getParticipante(dni);
        model.addAttribute("participante", participante);
        return "participante/confirm-delete";
    }

    @PostMapping("/participante/delete")
    public String deleteParticipante(@RequestParam String dni, RedirectAttributes redirectAttributes) {
        participanteDao.deleteParticipante(dni);
        redirectAttributes.addFlashAttribute("message", "Participante eliminado correctamente.");
        return "redirect:/participante/list";
    }

}
