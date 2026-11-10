package es.uji.ei1027.project.validator;

import es.uji.ei1027.project.model.Participante;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticipanteValidator implements Validator {

    // Patrón para DNI español (8 números y una letra)
    private static final String DNI_REGEX = "^[0-9]{8}[A-Za-z]$";
    private static final Pattern DNI_PATTERN = Pattern.compile(DNI_REGEX);

    // Patrón para formato de email básico
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public boolean supports(Class<?> clazz) {
        return Participante.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Participante participante = (Participante) target;

        // Validaciones para campos obligatorios y formato
        if (participante.getDni() == null || participante.getDni().trim().isEmpty()) {
            errors.rejectValue("dni", "obligatorio", "El DNI es obligatorio.");
        } else {
            // Validar formato de DNI
            Matcher matcherDni = DNI_PATTERN.matcher(participante.getDni());
            if (!matcherDni.matches()) {
                errors.rejectValue("dni", "formato.dni", "El formato del DNI es inválido (ej. 12345678A).");
            }

        }

        if (participante.getNombre() == null || participante.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "El nombre es obligatorio.");
        } else if (participante.getNombre().trim().length() < 2) {
            errors.rejectValue("nombre", "minlength", "El nombre debe tener al menos 2 caracteres.");
        }

        if (participante.getApellido() == null || participante.getApellido().trim().isEmpty()) {
            errors.rejectValue("apellido", "obligatorio", "El apellido es obligatorio.");
        } else if (participante.getApellido().trim().length() < 2) {
            errors.rejectValue("apellido", "minlength", "El apellido debe tener al menos 2 caracteres.");
        }

        if (participante.getSexo() == null || participante.getSexo().trim().isEmpty()) {
            errors.rejectValue("sexo", "obligatorio", "El sexo es obligatorio.");
        } else if (!participante.getSexo().equals("Masculino") && !participante.getSexo().equals("Femenino")) {
            errors.rejectValue("sexo", "valor.invalido", "El sexo debe ser 'Masculino' o 'Femenino'.");
        }

        if (participante.getFechaNacimiento() == null) {
            errors.rejectValue("fechaNacimiento", "obligatorio", "La fecha de nacimiento es obligatoria.");
        } else if (participante.getFechaNacimiento().isAfter(LocalDate.now())) {
            errors.rejectValue("fechaNacimiento", "fecha.futura", "La fecha de nacimiento no puede ser futura.");
        } else if (participante.getFechaNacimiento().isBefore(LocalDate.of(1900, 1, 1))) {
            errors.rejectValue("fechaNacimiento", "fecha.rango", "La fecha de nacimiento es demasiado antigua.");
        }

        if (participante.getEmail() == null || participante.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio.");
        } else {
            // Validar formato de email
            Matcher matcherEmail = EMAIL_PATTERN.matcher(participante.getEmail());
            if (!matcherEmail.matches()) {
                errors.rejectValue("email", "formato.email", "El formato del email es inválido (ej. nombre@dominio.com).");
            }
        }

        if (participante.getMetodoDePago() == null || participante.getMetodoDePago().trim().isEmpty()) {
            errors.rejectValue("metodoDePago", "obligatorio", "El método de pago es obligatorio.");
        } else if (!participante.getMetodoDePago().matches("^(Bizum|Efectivo|Tarjeta|Paypal)$")) {
            errors.rejectValue("metodoDePago", "valor.invalido", "Método de pago inválido.");
        }
    }
}
