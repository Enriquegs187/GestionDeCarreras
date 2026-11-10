package es.uji.ei1027.project.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);


    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo electrónico con un archivo PDF adjunto.
     * @param toEmail Dirección de correo del destinatario.
     * @param subject Asunto del correo.
     * @param body Cuerpo del correo (puede ser HTML).
     * @param pdfBytes Contenido del PDF como array de bytes.
     * @param pdfFileName Nombre del archivo PDF adjunto.
     */
    public void sendEmailWithAttachment(String toEmail, String subject, String body, byte[] pdfBytes, String pdfFileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("al426967@uji.es");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            // Adjuntar el PDF
            helper.addAttachment(pdfFileName, new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            logger.info("Correo enviado a " + toEmail + " con adjunto: " + pdfFileName);

        } catch (MessagingException e) {
            logger.error("Error al enviar el correo a " + toEmail + ": " + e.getMessage());
        }
    }
}