package es.uji.ei1027.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class ConfirmacionController {

    @GetMapping("/confirmar-borrado")
    public String confirmarBorrado(
            @RequestParam String mensaje,
            @RequestParam String accion,
            @RequestParam String cancelar,
            @RequestParam Map<String, String> allParams,
            Model model) {

        allParams.remove("mensaje");
        allParams.remove("accion");
        allParams.remove("cancelar");

        model.addAttribute("mensaje", mensaje);
        model.addAttribute("accion", accion);
        model.addAttribute("cancelar", cancelar);
        model.addAttribute("parametros", allParams.entrySet());

        return "common/confirm-delete";
    }
}
