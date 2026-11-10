package es.uji.ei1027.project.validator;

import es.uji.ei1027.project.model.Voluntario;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoluntarioValidator implements Validator {

    // Patrón para DNI español (8 números y una letra)
    private static final String DNI_REGEX = "^[0-9]{8}[A-Za-z]$";
    private static final Pattern DNI_PATTERN = Pattern.compile(DNI_REGEX);

    // Patrón para formato de email básico
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public boolean supports(Class<?> clazz) {
        return Voluntario.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Voluntario voluntario = (Voluntario) target;

        // Validaciones para DNI
        if (voluntario.getDni() == null || voluntario.getDni().trim().isEmpty()) {
            errors.rejectValue("dni", "obligatorio", "El DNI es obligatorio.");
        } else {
            // Validar formato de DNI
            Matcher matcherDni = DNI_PATTERN.matcher(voluntario.getDni());
            if (!matcherDni.matches()) {
                errors.rejectValue("dni", "formato.dni", "El formato del DNI es inválido (ej. 12345678A).");
            }
        }

        // Validaciones para Nombre
        if (voluntario.getNombre() == null || voluntario.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "El nombre es obligatorio.");
        } else if (voluntario.getNombre().trim().length() < 2) {
            errors.rejectValue("nombre", "minlength", "El nombre debe tener al menos 2 caracteres.");
        } else if (voluntario.getNombre().trim().length() > 20) {
            errors.rejectValue("nombre", "maxlength", "El nombre no puede exceder los 20 caracteres.");
        }

        // Validaciones para Apellido
        if (voluntario.getApellido() == null || voluntario.getApellido().trim().isEmpty()) {
            errors.rejectValue("apellido", "obligatorio", "El apellido es obligatorio.");
        } else if (voluntario.getApellido().trim().length() < 2) {
            errors.rejectValue("apellido", "minlength", "El apellido debe tener al menos 2 caracteres.");
        } else if (voluntario.getApellido().trim().length() > 20) {
            errors.rejectValue("apellido", "maxlength", "El apellido no puede exceder los 20 caracteres.");
        }

        // Validaciones para Email
        if (voluntario.getEmail() == null || voluntario.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio.");
        } else {
            Matcher matcherEmail = EMAIL_PATTERN.matcher(voluntario.getEmail());
            if (!matcherEmail.matches()) {
                errors.rejectValue("email", "formato.email", "El formato del email es inválido (ej. nombre@dominio.com).");
            } else if (voluntario.getEmail().trim().length() > 50) {
                errors.rejectValue("email", "maxlength", "El email no puede exceder los 50 caracteres.");
            }
        }

        // Validaciones para Disponibilidad
        if (voluntario.getDisponibilidad() == null || voluntario.getDisponibilidad().trim().isEmpty()) {
            errors.rejectValue("disponibilidad", "obligatorio", "La disponibilidad es obligatoria.");
        } else if (voluntario.getDisponibilidad().trim().length() < 3) {
            errors.rejectValue("disponibilidad", "minlength", "La disponibilidad debe ser más descriptiva.");
        } else if (voluntario.getDisponibilidad().trim().length() > 100) {
            errors.rejectValue("disponibilidad", "maxlength", "La disponibilidad no puede exceder los 100 caracteres.");
        }
    }
}