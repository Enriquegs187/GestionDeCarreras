package es.uji.ei1027.project.validator;

import es.uji.ei1027.project.model.Carrera;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

public class CarreraValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        // Este validador solo soporta la clase Carrera
        return Carrera.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Carrera carrera = (Carrera) target;


        // 1. Distancia > 0
        if (carrera.getDistancia() <= 0) {
            errors.rejectValue("distancia", "positivo", "La distancia debe ser mayor que 0.");
        } else if (carrera.getDistancia() > 999.99) {
            errors.rejectValue("distancia", "maxvalue", "La distancia no puede exceder 999.99 km.");
        }

        // 2. Altitud >= 0
        if (carrera.getAltitud() < 0) {
            errors.rejectValue("altitud", "noNegativo", "La altitud no puede ser negativa.");
        }


        // 3. Punto de inicio no puede ser vacío
        if (carrera.getInicio() == null || carrera.getInicio().trim().isEmpty()) {
            errors.rejectValue("inicio", "obligatorio", "El punto de inicio es obligatorio.");
        }

        // 4. Punto de fin no puede ser vacío
        if (carrera.getFin() == null || carrera.getFin().trim().isEmpty()) {
            errors.rejectValue("fin", "obligatorio", "El punto de fin es obligatorio.");
        }


        // 5. Categoría Sexo = 'Masculino', 'Femenino' o 'Mixto'
        if (carrera.getCategoriaSexo() == null || carrera.getCategoriaSexo().trim().isEmpty()) {
            errors.rejectValue("categoriaSexo", "obligatorio", "La categoría de sexo es obligatoria.");
        } else if (!carrera.getCategoriaSexo().equals("Masculino") &&
                !carrera.getCategoriaSexo().equals("Femenino") &&
                !carrera.getCategoriaSexo().equals("Mixto")) {
            errors.rejectValue("categoriaSexo", "valor.invalido", "La categoría de sexo debe ser 'Masculino', 'Femenino' o 'Mixto'.");
        }


        // 6. Precio >= 0)
        if (carrera.getPrecio() < 0) {
            errors.rejectValue("precio", "noNegativo", "El precio no puede ser negativo.");
        }


        // 7. Comprobación de que al menos se ha seleccionado una opción en las checkboxes de Categorías de Edad
        List<String> categoriasEdadSeleccionadas = carrera.getCategoriasEdadSeleccionadas();
        if (categoriasEdadSeleccionadas == null || categoriasEdadSeleccionadas.isEmpty()) {
            errors.rejectValue("categoriasEdadSeleccionadas", "obligatorio", "Debe seleccionar al menos una categoría de edad.");
        }
    }
}