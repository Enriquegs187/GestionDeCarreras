package es.uji.ei1027.project.model;

import java.sql.Date;

public class SolicitudPrevia {
    private String id;
    private String nombre;
    private String estadoSolicitud;
    private Date fechaAprobacion;
    private String motivo;
    private String personaContacto;
    private String mailEmpresaContacto;
    private String dniPromotor;

    public SolicitudPrevia() {
    }

    public SolicitudPrevia(String id, String nombre, String estadoSolicitud, Date fechaAprobacion,
                           String motivo, String personaContacto, String mailEmpresaContacto, String dniPromotor) {
        this.id = id;
        this.nombre = nombre;
        this.estadoSolicitud = estadoSolicitud;
        this.fechaAprobacion = fechaAprobacion;
        this.motivo = motivo;
        this.personaContacto = personaContacto;
        this.mailEmpresaContacto = mailEmpresaContacto;
        this.dniPromotor = dniPromotor;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstadoSolicitud() { return estadoSolicitud; }
    public void setEstadoSolicitud(String estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }

    public Date getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(Date fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getPersonaContacto() { return personaContacto; }
    public void setPersonaContacto(String personaContacto) { this.personaContacto = personaContacto; }

    public String getMailEmpresaContacto() { return mailEmpresaContacto; }
    public void setMailEmpresaContacto(String mailEmpresaContacto) { this.mailEmpresaContacto = mailEmpresaContacto; }

    public String getDniPromotor() { return dniPromotor; }
    public void setDniPromotor(String dniPromotor) { this.dniPromotor = dniPromotor; }

    @Override
    public String toString() {
        return "SolicitudPrevia{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estadoSolicitud='" + estadoSolicitud + '\'' +
                ", fechaAprobacion=" + fechaAprobacion +
                ", motivo='" + motivo + '\'' +
                ", personaContacto='" + personaContacto + '\'' +
                ", mailEmpresaContacto='" + mailEmpresaContacto + '\'' +
                ", dniPromotor='" + dniPromotor + '\'' +
                '}';
    }
}
