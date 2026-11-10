package es.uji.ei1027.project.model;

import java.sql.Date;

public class FichaClub {
    private String id;
    private Date validezFecha;
    private String dniParticipante;
    private String cifClub;

    public FichaClub() {
    }

    public FichaClub(String id, Date validezFecha, String dniParticipante, String cifClub) {
        this.id = id;
        this.validezFecha = validezFecha;
        this.dniParticipante = dniParticipante;
        this.cifClub = cifClub;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getValidezFecha() {
        return validezFecha;
    }

    public void setValidezFecha(Date validezFecha) {
        this.validezFecha = validezFecha;
    }

    public String getDniParticipante() {
        return dniParticipante;
    }

    public void setDniParticipante(String dniParticipante) {
        this.dniParticipante = dniParticipante;
    }

    public String getCifClub() {
        return cifClub;
    }

    public void setCifClub(String cifClub) {
        this.cifClub = cifClub;
    }

    @Override
    public String toString() {
        return "FichaClub{" +
                "id='" + id + '\'' +
                ", validezFecha=" + validezFecha +
                ", dniParticipante='" + dniParticipante + '\'' +
                ", cifClub='" + cifClub + '\'' +
                '}';
    }
}

