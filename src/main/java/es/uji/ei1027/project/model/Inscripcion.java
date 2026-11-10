package es.uji.ei1027.project.model;

import java.time.LocalTime;

public class Inscripcion {
    private String cod;
    private LocalTime tiempometa;
    private String codCarrera;
    private String dniParticipante;
    private String dorsal;

    public Inscripcion() {
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public LocalTime getTiempometa() {
        return tiempometa;
    }

    public void setTiempometa(LocalTime tiempometa) {
        this.tiempometa = tiempometa;
    }

    public String getCodCarrera() {
        return codCarrera;
    }

    public void setCodCarrera(String codCarrera) {
        this.codCarrera = codCarrera;
    }

    public String getDniParticipante() {
        return dniParticipante;
    }

    public void setDniParticipante(String dniParticipante) {
        this.dniParticipante = dniParticipante;
    }


    public void setDorsal(String dorsal) {this.dorsal = dorsal;}

    public String getDorsal(){return this.dorsal;}

    @Override
    public String toString() {
        return "Inscripcion{" +
                "cod='" + cod + '\'' +
                ", tiempometa=" + tiempometa +
                ", codCarrera='" + codCarrera + '\'' +
                ", dniParticipante='" + dniParticipante + '\'' +
                ", dorsal='" + dorsal + '\'' +
                '}';
    }
}
