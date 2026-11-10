package es.uji.ei1027.project.validator;

import es.uji.ei1027.project.model.Competicion;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CompeticionValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return Competicion.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Competicion competicion = (Competicion) target;

        // 1. Nombre no puede ser vacío
        if (competicion.getNombre() == null || competicion.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "El nombre de la competición es obligatorio.");
        } else if (competicion.getNombre().trim().length() < 3) {
            errors.rejectValue("nombre", "minlength", "El nombre debe tener al menos 3 caracteres.");
        }


        // 2. Localización no puede ser vacía
        if (competicion.getLocalizacion() == null || competicion.getLocalizacion().trim().isEmpty()) {
            errors.rejectValue("localizacion", "obligatorio", "La localización es obligatoria.");
        } else if (competicion.getLocalizacion().trim().length() < 3) {
            errors.rejectValue("localizacion", "minlength", "La localización debe tener al menos 3 caracteres.");
        }


        // 3. Fecha de Inicio no puede ser nula
        if (competicion.getFechaInicio() == null) {
            errors.rejectValue("fechaInicio", "obligatorio", "La fecha de inicio es obligatoria.");
        }


        // 4. Fecha de Fin no puede ser nula, y debe ser > fechaInicio
        if (competicion.getFechaFin() == null) {
            errors.rejectValue("fechaFin", "obligatorio", "La fecha de fin es obligatoria.");
        } else if (competicion.getFechaInicio() != null && competicion.getFechaFin().isBefore(competicion.getFechaInicio())) {
            errors.rejectValue("fechaFin", "fecha.inconsistente", "La fecha de fin debe ser posterior a la fecha de inicio.");
        }


        // 5. Tasa de Registro no puede ser negativa
        if (competicion.getTasaDeRegistro() < 0) {
            errors.rejectValue("tasaDeRegistro", "tasa.negativa", "La tasa de registro no puede ser negativa.");
        }


    }
}
