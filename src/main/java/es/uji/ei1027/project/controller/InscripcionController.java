package es.uji.ei1027.project.controller;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import es.uji.ei1027.project.dao.CarreraDao;
import es.uji.ei1027.project.dao.InscripcionDao;
import es.uji.ei1027.project.dao.ParticipanteDao;
import es.uji.ei1027.project.model.Carrera;
import es.uji.ei1027.project.model.Competicion;
import es.uji.ei1027.project.model.Inscripcion;
import es.uji.ei1027.project.model.Participante;
import es.uji.ei1027.project.service.EmailService;
import es.uji.ei1027.project.service.InscripcionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@RequestMapping("/inscripcion")
public class InscripcionController {
    private InscripcionDao inscripcionDao;
    private CarreraDao carreraDao;
    private InscripcionService inscripcionService;
    private ParticipanteDao participanteDao;
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(InscripcionController.class);



    @Autowired
    public void setInscripcionDao(InscripcionDao inscripcionDao) {
        this.inscripcionDao = inscripcionDao;
    }
    @Autowired
    public void setCarreraDao(CarreraDao carreraDao) {this.carreraDao = carreraDao;}

    @Autowired
    public void setInscripcionService(InscripcionService inscripcionService) {this.inscripcionService = inscripcionService;}

    @Autowired
    public void setParticipanteDao(ParticipanteDao participanteDao) {
        this.participanteDao = participanteDao;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @RequestMapping("/list")
    public String listInscripcion(Model model) {
        model.addAttribute("inscripciones", inscripcionDao.getInscripcion());
        return "inscripcion/list";
    }

    @RequestMapping(value="/add")
    public String addInscripcion(Model model) {
        model.addAttribute("inscripcion", new Inscripcion());
        return "inscripcion/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("inscripcion") Inscripcion inscripcion,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "inscripcion/add";
        inscripcionDao.addInscripcion(inscripcion);
        return "redirect:list";
    }

    @RequestMapping(value="/update/{cod}", method = RequestMethod.GET)
    public String editInscripcion(Model model, @PathVariable String cod) {
        model.addAttribute("inscripcion", inscripcionDao.getInscripcion(cod));
        return "inscripcion/update";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("inscripcion") Inscripcion inscripcion,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "inscripcion/update";
        inscripcionDao.updateInscripcion(inscripcion);
        return "redirect:list";
    }

    @PostMapping("/confirmDesinscripcion")
    public String confirmDesinscripcion(@RequestParam("codCarreraToUnenroll") String codCarrera,
                                        @RequestParam("dniParticipanteToUnenroll") String dniParticipante,
                                        RedirectAttributes redirectAttributes) {

        Inscripcion inscripcion = inscripcionDao.getInscripcionPorParticipanteYCarrera(dniParticipante, codCarrera);
        if (inscripcion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se encontró la inscripción para desinscribir.");
            return "redirect:/carrera/porParticipante/" + dniParticipante;
        }

        String nombreCarrera = "desconocida";
        try {
            Carrera carrera = carreraDao.getCarrera(codCarrera);
            if (carrera != null) {
                nombreCarrera = carrera.getInicio();
            }

            inscripcionDao.deleteByCarreraAndParticipante(codCarrera, dniParticipante);
            redirectAttributes.addFlashAttribute("mensajeExito", "Te has desinscrito de la carrera '" + nombreCarrera + "' correctamente.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo completar la desinscripción de '" + nombreCarrera +
                            "'. Hay datos asociados que impiden el borrado.");
        } catch (Exception e) {
            logger.error("Error al desinscribir participante {} de carrera {}: {}", dniParticipante, codCarrera, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error inesperado al intentar desinscribirte de la carrera '" + nombreCarrera + "'.");
        }

        return "redirect:/carrera/porParticipante/" + dniParticipante;
    }

    @GetMapping("/delete/{codCarrera}/{dniParticipante}")
    public String initiateDesinscripcionConfirmation(@PathVariable String codCarrera,
                                                     @PathVariable String dniParticipante,
                                                     RedirectAttributes redirectAttributes) {
        Inscripcion inscripcion = inscripcionDao.getInscripcionPorParticipanteYCarrera(dniParticipante, codCarrera);
        if (inscripcion == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La inscripción a desinscribir no fue encontrada.");
            return "redirect:/carrera/porParticipante/" + dniParticipante;
        }

        Carrera carrera = carreraDao.getCarrera(codCarrera);
        if (carrera == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "La carrera asociada a la inscripción no fue encontrada.");
            return "redirect:/carrera/porParticipante/" + dniParticipante;
        }

        return "redirect:/carrera/porParticipante/" + dniParticipante +
                "?confirmDesinscripcionCodCarrera=" + codCarrera +
                "&confirmDesinscripcionDniParticipante=" + dniParticipante;
    }


    @GetMapping("/inscribirCarrera/{codCarrera}")
    public String inscribirCarreraPaso1(@PathVariable String codCarrera,
                                        HttpSession session,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        Participante p = (Participante) session.getAttribute("participante");
        if (p == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión como atleta para inscribirte.");
            return "redirect:/loginParticipante";
        }
        String dni = p.getDni();

        try {
            InscripcionService.InscripcionData inscripcionData =
                    inscripcionService.checkEligibility(dni, codCarrera);

            // Guardamos los datos temporales en sesión para el siguiente paso (pago)
            session.setAttribute("tempInscripcionData", inscripcionData);
            session.setAttribute("codCarrera", codCarrera);
            session.setAttribute("dniParticipante", dni);

            model.addAttribute("codCarrera", codCarrera);
            model.addAttribute("dniParticipante", dni);
            return "inscripciones/pago_simulado";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            String codCompeticion = carreraDao.getCarrera(codCarrera).getCodCompeticion();
            return "redirect:/carrera/viewersList/" + codCompeticion;
        }
    }

    @GetMapping("/justificante/{dni}/{dorsal}")
    public void descargarJustificante(HttpServletResponse response,
                                      @PathVariable String dni,
                                      @PathVariable String dorsal) {

        Participante participante = participanteDao.getParticipante(dni);
        Inscripcion inscripcion = inscripcionDao.getInscripcionByDniAndDorsal(dni, dorsal);
        Carrera carrera = carreraDao.getCarrera(inscripcion.getCodCarrera());
        Competicion competicion = carreraDao.getCompeticion(carrera.getCodCompeticion());

        // Generar el PDF en bytes
        byte[] pdfBytes;
        try {
            pdfBytes = generateJustificantePdfBytes(participante, carrera, competicion, dni, dorsal);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=justificante_inscripcion_" + dni + ".pdf");

            OutputStream out = response.getOutputStream();
            out.write(pdfBytes);
            out.flush();

        } catch (DocumentException e) {
            logger.error("Error al generar justificante: {}", e.getMessage());
        } catch (IOException e){
            logger.error("Error de E/S: {}", e.getMessage());

        }

    }




    @PostMapping("/procesarPagoSimulado")
    public String procesarPagoSimulado(@RequestParam("codCarrera") String codCarrera,
                                       @RequestParam("dniParticipante") String dniParticipante,
                                       @RequestParam("numeroTarjeta") String numeroTarjeta,
                                       @RequestParam("fechaCaducidad") String fechaCaducidad,
                                       @RequestParam("cvv") String cvv,
                                       Model model) {


        boolean yaInscrito = inscripcionDao.getInscripcion().stream()
                .anyMatch(i -> i.getCodCarrera().equals(codCarrera) && i.getDniParticipante().equals(dniParticipante));
        if (yaInscrito) {
            model.addAttribute("error", "Ya estabas inscrito en esta carrera. La inscripción ya existía.");
            return "inscripciones/error";
        }

        Inscripcion ins = new Inscripcion();
        ins.setCod(UUID.randomUUID().toString().substring(0,20));
        ins.setCodCarrera(codCarrera);
        ins.setDniParticipante(dniParticipante);

        String nuevoDorsal = generateDorsal();
        ins.setDorsal(nuevoDorsal);

        inscripcionDao.addInscripcion(ins);

        //  -- LÓGICA PARA ENVÍO DE CORREO ELECTRÓNICO --  //
        try {
            Participante participante = participanteDao.getParticipante(dniParticipante);
            Carrera carrera = carreraDao.getCarrera(codCarrera);
            Competicion competicion = carreraDao.getCompeticion(carrera.getCodCompeticion());

            if (participante != null && carrera != null && competicion != null) {
                // 1. Generar el PDF en bytes
                byte[] pdfBytes = generateJustificantePdfBytes(participante, carrera, competicion, dniParticipante, nuevoDorsal);

                // 2. Preparar el correo y enviarlo
                String toEmail = participante.getEmail();
                String subject = "Justificante de Inscripción a la carrera " + carrera.getInicio();
                String body = "<p>Estimado/a <strong>" + participante.getNombre() + "</strong>,</p>"
                        + "<p>Adjuntamos su justificante de inscripción a la carrera <strong>'" + carrera.getInicio() + "'</strong> "
                        + "de la competición <strong>'" + competicion.getNombre() + "'</strong>.</p>"
                        + "<p>Su dorsal asignado es: <strong>" + nuevoDorsal + "</strong>.</p>"
                        + "<p>¡Mucha suerte en la carrera!</p>"
                        + "<p>Atentamente,<br>El equipo de MoRMaS</p>";

                emailService.sendEmailWithAttachment(toEmail, subject, body, pdfBytes, "justificante_inscripcion_" + dniParticipante + ".pdf");

                model.addAttribute("mensaje", "¡Inscripción completada con éxito y justificante enviado a tu email!");
            } else {
                logger.error("No se pudo enviar el email: Faltan datos (Participante, Carrera o Competicion es null) para DNI: {}, Carrera: {}", dniParticipante, codCarrera);
                model.addAttribute("mensaje", "¡Inscripción completada con éxito! Pero hubo un error al preparar el justificante para enviar por email (datos no encontrados).");
            }
        } catch (Exception e) {
            logger.error("Error al enviar el correo con justificante para {}: {}", dniParticipante, e.getMessage(), e);
            model.addAttribute("mensaje", "¡Inscripción completada con éxito! Pero hubo un error al enviar el justificante por email: " + e.getMessage());
        }
        model.addAttribute("dorsal", nuevoDorsal);
        model.addAttribute("dni", dniParticipante);
        return "inscripciones/success";
    }

    private String generateDorsal() {
        return "N" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private byte[] generateJustificantePdfBytes(Participante participante, Carrera carrera, Competicion competicion, String dni, String dorsal) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        try {
            Image logo = Image.getInstance("src/main/resources/static/img/logo.png");
            logo.scaleToFit(250, 150);
            logo.setAlignment(Image.ALIGN_LEFT);
            document.add(logo);
        } catch (Exception e) {
            logger.error("Error al cargar la imagen del logo: {}", e.getMessage());
            document.add(new Paragraph("Logo no disponible"));
        }

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Paragraph titulo = new Paragraph("Justificante de Inscripción", titleFont);
        titulo.setAlignment(Element.ALIGN_LEFT);
        document.add(titulo);

        document.add(Chunk.NEWLINE);

        Font contentFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        document.add(new Paragraph("Nombre de la competición: " + competicion.getNombre(), contentFont));
        document.add(new Paragraph("Fecha de inicio: " + competicion.getFechaInicio().format(dateFormatter), contentFont));
        document.add(new Paragraph("Fecha de fin: " + competicion.getFechaFin().format(dateFormatter), contentFont));

        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("Precio de la inscripción: " + carrera.getPrecio(), contentFont));
        document.add(new Paragraph("Categorías de edad de la carrera: " + carrera.getCategoriasEdadString(), contentFont));
        document.add(new Paragraph("Categoria de sexo de la carrera: " + carrera.getCategoriaSexo(), contentFont));

        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("Nombre del participante: " + participante.getNombre(), contentFont));
        document.add(new Paragraph("DNI del participante: " + dni, contentFont));
        document.add(new Paragraph("Dorsal asignado: " + dorsal, contentFont));
        document.add(new Paragraph("Fecha de emisión: " + java.time.LocalDate.now().format(dateFormatter), contentFont));

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        try {
            Image qr = Image.getInstance("src/main/resources/static/img/qr.png");
            qr.scaleToFit(120, 120);
            qr.setAlignment(Image.ALIGN_LEFT);
            document.add(qr);
        } catch (Exception e) {
            logger.error("Error al cargar la imagen del QR: {}", e.getMessage());
            document.add(new Paragraph("QR no disponible"));
        }

        document.close();
        return baos.toByteArray();
    }
}