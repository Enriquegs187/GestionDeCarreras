package es.uji.ei1027.project.model;

public class PuntoControl {
    private String cod;
    private String localizacion;
    private String codCarrera;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getCodCarrera() {
        return codCarrera;
    }

    public void setCodCarrera(String codCarrera) {
        this.codCarrera = codCarrera;
    }

    @Override
    public String toString() {
        return "PuntoControl{" +
                "cod='" + cod + '\'' +
                ", localizacion='" + localizacion + '\'' +
                '}';
    }
}
