package es.uji.ei1027.project.model;

public class Asignan {
    private String dniVoluntario;
    private String codPuntoControl;

    public String getDniVoluntario() {
        return dniVoluntario;
    }

    public void setDniVoluntario(String dniVoluntario) {
        this.dniVoluntario = dniVoluntario;
    }

    public String getCodPuntoControl() {
        return codPuntoControl;
    }

    public void setCodPuntoControl(String codPuntoControl) {
        this.codPuntoControl = codPuntoControl;
    }

    @Override
    public String toString() {
        return "Asignan{" +
                "dniVoluntario='" + dniVoluntario + '\'' +
                ", codPuntoControl='" + codPuntoControl + '\'' +
                '}';
    }
}
