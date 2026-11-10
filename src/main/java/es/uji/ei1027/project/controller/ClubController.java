package es.uji.ei1027.project.controller;

import es.uji.ei1027.project.dao.ClubDao;
import es.uji.ei1027.project.dao.ParticipanteDao;
import es.uji.ei1027.project.model.Club;
import es.uji.ei1027.project.model.Participante;
import es.uji.ei1027.project.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/club")
public class ClubController {

    private ClubDao clubDao;
    private ParticipanteDao participanteDao;

    @Autowired
    public void setClubDao(ClubDao clubDao) {
        this.clubDao = clubDao;
    }

    @Autowired
    public void setParticipanteDao(ParticipanteDao participanteDao) {
        this.participanteDao = participanteDao;
    }

    @RequestMapping("/list")
    public String listClubs(@RequestParam(required = false) String nombre, Model model) {
        List<Club> clubs = clubDao.getClubs();

        if (nombre != null && !nombre.trim().isEmpty()) {
            clubs = clubs.stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("nombre", nombre);
        model.addAttribute("clubs", clubs);
        return "club/list";
    }


    @RequestMapping("/areaPersonalClub")
    public String areaPersonalClub(Model model, HttpSession session,
                                   @RequestParam(value = "confirmSalirDni", required = false) String confirmSalirDni) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null || !user.getTipoUsuario().equals("participante")) {
            return "redirect:/login/participante";
        }

        String dniParticipante = user.getUsername();
        Participante participante = participanteDao.getParticipante(dniParticipante);

        if (participante == null) {
            session.invalidate();
            model.addAttribute("message", "Error: No se pudieron cargar sus datos de participante. Por favor, inicie sesión de nuevo.");
            return "redirect:/login/participante";
        }
        model.addAttribute("participante", participante);

        // --- Lógica para mostrar la información del club ---
        if (participante.getClubCif() != null && !participante.getClubCif().isEmpty()) {
            Club club = clubDao.getClub(participante.getClubCif());
            if (club != null) {
                model.addAttribute("club", club);
                String nombrePromotor = clubDao.getNombrePromotor(club.getDniPromotor());
                List<Participante> miembros = participanteDao.getParticipantesByClub(club.getCif());

                model.addAttribute("nombrePromotor", nombrePromotor);
                model.addAttribute("miembros", miembros);
            } else {
                // El participante tiene un CIF de club, pero el club no existe en la BD
                model.addAttribute("message", "Hubo un error al cargar la información de tu club. El club podría no existir.");
            }
        } else {
            // El participante no tiene un club asignado (clubCif es null o vacío)
            model.addAttribute("message", "Actualmente no estás asociado a ningún club. ¡Anímate a unirte a uno!");
        }
        if (confirmSalirDni != null && !confirmSalirDni.isEmpty()) {
            // Aseguramos que el DNI del participante en sesión sea el mismo que se pidió confirmar
            if (participante.getDni().equals(confirmSalirDni)) {
                model.addAttribute("confirmSalirParticipanteDni", participante.getDni());
                model.addAttribute("confirmSalirClubNombre", (participante.getClubCif() != null && !participante.getClubCif().isEmpty() && clubDao.getClub(participante.getClubCif()) != null) ? clubDao.getClub(participante.getClubCif()).getNombre() : "tu club");
            } else {
                model.addAttribute("mensajeError", "Solicitud de confirmación inválida.");
            }
        }

        return "club/areaPersonalClub";
    }

    @RequestMapping(value="/add")
    public String addClub(Model model) {
        model.addAttribute("club", new Club());
        return "club/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("club") Club club,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "club/add";
        clubDao.addClub(club);
        return "redirect:list";
    }

    @RequestMapping(value="/update/{cif}", method = RequestMethod.GET)
    public String editClub(Model model, @PathVariable String cif) {
        model.addAttribute("club", clubDao.getClub(cif));
        return "club/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("club") Club club,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "club/update";
        clubDao.updateClub(club);
        return "redirect:list";
    }

    @RequestMapping(value="/delete/{cif}")
    public String processDelete(@PathVariable String cif) {
        clubDao.deleteClub(cif);
        return "redirect:../list";
    }

    @GetMapping("/unirse/{cif}")
    public String unirseClub(@PathVariable String cif,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null || !user.getTipoUsuario().equals("participante")) {
            return "redirect:/login/participante";
        }
        Participante participante = participanteDao.getParticipante(user.getUsername());

        Club club = clubDao.getClub(cif);
        if (club == null) {
            redirectAttributes.addFlashAttribute("error", "El club al que intentas unirte no existe.");
            return "redirect:/club/list";
        }
        if (participante.getClubCif() != null && !participante.getClubCif().isEmpty()) {
            if (participante.getClubCif().equals(cif)) {
                redirectAttributes.addFlashAttribute("info", "Ya eres miembro de este club.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Ya eres miembro del club '" + clubDao.getClub(participante.getClubCif()).getNombre() + "'. Debes salir de tu club actual para unirte a otro.");
            }
            return "redirect:/club/list";
        }
        try {
            participante.setClubCif(cif);
            participanteDao.updateParticipante(participante);

            session.setAttribute("participante", participante);

            redirectAttributes.addFlashAttribute("mensajeExito", "¡Te has unido correctamente al club '" + club.getNombre() + "'!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al unirte al club '" + club.getNombre() + "': " + e.getMessage());
        }

        return "redirect:/club/areaPersonalClub";
    }

    @PostMapping("/confirmSalir")
    public String confirmSalirClub(HttpSession session,
                                   RedirectAttributes redirectAttributes,
                                   @RequestParam("participanteDni") String participanteDniConfirm) {

        Participante participante = (Participante) session.getAttribute("participante");
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (participante == null || user == null || !user.getTipoUsuario().equals("participante") || !participante.getDni().equals(participanteDniConfirm)) {
            redirectAttributes.addFlashAttribute("error", "Acceso denegado o sesión inválida para esta acción.");
            return "redirect:/login/participante";
        }

        if (participante.getClubCif() == null || participante.getClubCif().isEmpty()) {
            redirectAttributes.addFlashAttribute("info", "Actualmente no estás asociado a ningún club.");
            return "redirect:/club/areaPersonalClub";
        }

        String cifClubActual = participante.getClubCif();
        String nombreClub = null;

        try {
            Club clubActual = clubDao.getClub(cifClubActual);
            if (clubActual != null) {
                nombreClub = clubActual.getNombre();
            }

            participante.setClubCif(null);
            participanteDao.updateParticipante(participante);

            session.setAttribute("participante", participante);

            redirectAttributes.addFlashAttribute("mensajeExito", "Has salido del club '" + nombreClub + "' correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al salir del club '" + nombreClub + "': " + e.getMessage());
        }

        return "redirect:/club/areaPersonalClub";
    }

    @GetMapping("/salir")
    public String initiateSalirClubConfirmation(HttpSession session,
                                                RedirectAttributes redirectAttributes) {
        Participante participante = (Participante) session.getAttribute("participante");
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (participante == null || user == null || !user.getTipoUsuario().equals("participante")) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión como atleta para realizar esta acción.");
            return "redirect:/login/participante";
        }

        if (participante.getClubCif() == null || participante.getClubCif().isEmpty()) {
            redirectAttributes.addFlashAttribute("info", "Actualmente no estás asociado a ningún club para poder salir.");
            return "redirect:/club/areaPersonalClub";
        }

        return "redirect:/club/areaPersonalClub?confirmSalirDni=" + participante.getDni();
    }
}

