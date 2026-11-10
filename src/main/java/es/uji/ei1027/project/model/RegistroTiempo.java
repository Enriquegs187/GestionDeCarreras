package es.uji.ei1027.project.model;

import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

public class RegistroTiempo {

    private String cod;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime tiempo;

    private String comentarios;
    private String codPuntoControl;
    private String codInscripcion;

    public RegistroTiempo() { }

    public RegistroTiempo(String cod,
                          LocalTime tiempo,
                          String comentarios,
                          String codPuntoControl,
                          String codInscripcion) {
        this.cod = cod;
        this.tiempo = tiempo;
        this.comentarios = comentarios;
        this.codPuntoControl = codPuntoControl;
        this.codInscripcion = codInscripcion;
    }

    public String getCod() {
        return cod;
    }
    public void setCod(String cod) {
        this.cod = cod;
    }

    public LocalTime getTiempo() {
        return tiempo;
    }
    public void setTiempo(LocalTime tiempo) {
        this.tiempo = tiempo;
    }

    public String getComentarios() {
        return comentarios;
    }
    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getCodPuntoControl() {
        return codPuntoControl;
    }
    public void setCodPuntoControl(String codPuntoControl) {
        this.codPuntoControl = codPuntoControl;
    }

    public String getCodInscripcion() {
        return codInscripcion;
    }
    public void setCodInscripcion(String codInscripcion) {
        this.codInscripcion = codInscripcion;
    }

    @Override
    public String toString() {
        return "RegistroTiempo{" +
                "cod='" + cod + '\'' +
                ", tiempo=" + tiempo +
                ", comentarios='" + comentarios + '\'' +
                ", codPuntoControl='" + codPuntoControl + '\'' +
                ", codInscripcion='" + codInscripcion + '\'' +
                '}';
    }
}
