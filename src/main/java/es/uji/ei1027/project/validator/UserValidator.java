package es.uji.ei1027.project.validator;

import es.uji.ei1027.project.model.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return UserDetails.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        UserDetails user = (UserDetails) obj;
        if (user.getUsername().trim().isEmpty())
            errors.rejectValue("username", "obligatorio",
                    "Hace falta introducir un valor");

        if (user.getPassword().trim().isEmpty())
            errors.rejectValue("password", "obligatoria",
                    "Hace falta introducir un valor");

    }
}