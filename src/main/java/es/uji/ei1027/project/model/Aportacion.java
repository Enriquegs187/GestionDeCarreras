package es.uji.ei1027.project.model;

public class Aportacion {
    private String codCompeticion;
    private String codSponsor;


    public String getCodCompeticion() {
        return codCompeticion;
    }

    public void setCodCompeticion(String codCompeticion) {
        this.codCompeticion = codCompeticion;
    }

    public String getCodSponsor() {
        return codSponsor;
    }

    public void setCodSponsor(String codSponsor) {
        this.codSponsor = codSponsor;
    }

    @Override
    public String toString() {
        return "Aportacion{" +
                "codCompeticion='" + codCompeticion + '\'' +
                ", codSponsor='" + codSponsor + '\'' +
                '}';
    }
}
