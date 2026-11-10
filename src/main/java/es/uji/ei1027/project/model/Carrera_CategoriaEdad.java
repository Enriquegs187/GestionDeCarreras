package es.uji.ei1027.project.model;

public class Carrera_CategoriaEdad {
    private String codCarrera;
    private String codCategoriaEdad;

    public String getCodCarrera() {
        return codCarrera;
    }

    public void setCodCarrera(String codCarrera) {
        this.codCarrera = codCarrera;
    }

    public String getCodCategoriaEdad() {
        return codCategoriaEdad;
    }

    public void setCodCategoriaEdad(String codCategoriaEdad) {
        this.codCategoriaEdad = codCategoriaEdad;
    }

    @Override
    public String toString() {
        return "Carrera_CategoriaEdad{" +
                "codCarrera='" + codCarrera + '\'' +
                ", codCategoriaEdad='" + codCategoriaEdad + '\'' +
                '}';
    }
}
