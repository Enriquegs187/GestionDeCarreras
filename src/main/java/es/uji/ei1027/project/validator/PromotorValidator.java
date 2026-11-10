package es.uji.ei1027.project.validator;

import es.uji.ei1027.project.model.Promotor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PromotorValidator implements Validator {

    // Patrón para DNI español (8 números y una letra)
    private static final String DNI_REGEX = "^[0-9]{8}[A-Za-z]$";
    private static final Pattern DNI_PATTERN = Pattern.compile(DNI_REGEX);

    // Patrón para formato de email básico
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Patrón para número de teléfono (ejemplo: 9 dígitos)
    private static final String TELEFONO_REGEX = "^[0-9]{9}$";
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(TELEFONO_REGEX);

    @Override
    public boolean supports(Class<?> clazz) {
        return Promotor.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Promotor promotor = (Promotor) target;

        // Validaciones para DNI
        if (promotor.getDni() == null || promotor.getDni().trim().isEmpty()) {
            errors.rejectValue("dni", "obligatorio", "El DNI es obligatorio.");
        } else {
            // Validar formato de DNI
            Matcher matcherDni = DNI_PATTERN.matcher(promotor.getDni());
            if (!matcherDni.matches()) {
                errors.rejectValue("dni", "formato.dni", "El formato del DNI es inválido (ej. 12345678A).");
            }
        }

        // Validaciones para Nombre
        if (promotor.getNombre() == null || promotor.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "El nombre es obligatorio.");
        } else if (promotor.getNombre().trim().length() < 2) {
            errors.rejectValue("nombre", "minlength", "El nombre debe tener al menos 2 caracteres.");
        } else if (promotor.getNombre().trim().length() > 50) {
            errors.rejectValue("nombre", "maxlength", "El nombre no puede exceder los 50 caracteres.");
        }

        // Validaciones para Nombre de Organización (VARCHAR(100))
        if (promotor.getNombreOrganizacion() == null || promotor.getNombreOrganizacion().trim().isEmpty()) {
            errors.rejectValue("nombreOrganizacion", "obligatorio", "El nombre de la organización es obligatorio.");
        } else if (promotor.getNombreOrganizacion().trim().length() < 3) {
            errors.rejectValue("nombreOrganizacion", "minlength", "El nombre de la organización debe tener al menos 3 caracteres.");
        } else if (promotor.getNombreOrganizacion().trim().length() > 100) {
            errors.rejectValue("nombreOrganizacion", "maxlength", "El nombre de la organización no puede exceder los 100 caracteres.");
        }

        // Validaciones para Email
        if (promotor.getEmail() == null || promotor.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio.");
        } else {
            Matcher matcherEmail = EMAIL_PATTERN.matcher(promotor.getEmail());
            if (!matcherEmail.matches()) {
                errors.rejectValue("email", "formato.email", "El formato del email es inválido (ej. nombre@dominio.com).");
            } else if (promotor.getEmail().trim().length() > 100) {
                errors.rejectValue("email", "maxlength", "El email no puede exceder los 100 caracteres.");
            }
        }

        // Validaciones para Teléfono
        if (promotor.getTelefono() == null || promotor.getTelefono().trim().isEmpty()) {
            errors.rejectValue("telefono", "obligatorio", "El teléfono es obligatorio.");
        } else {
            Matcher matcherTelefono = TELEFONO_PATTERN.matcher(promotor.getTelefono());
            if (!matcherTelefono.matches()) {
                errors.rejectValue("telefono", "formato.telefono", "El formato del teléfono es inválido (ej. 9 dígitos).");
            } else if (promotor.getTelefono().trim().length() > 20) {
                errors.rejectValue("telefono", "maxlength", "El teléfono no puede exceder los 20 caracteres.");
            }
        }
    }
}