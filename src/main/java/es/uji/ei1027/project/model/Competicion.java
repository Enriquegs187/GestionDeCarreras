package es.uji.ei1027.project.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;


public class Competicion {
    private String cod;
    private String nombre;
    private String localizacion;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    private double tasaDeRegistro;
    private String dniPromotor;


    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getTasaDeRegistro() {
        return tasaDeRegistro;
    }

    public void setTasaDeRegistro(double tasaDeRegistro) {
        this.tasaDeRegistro = tasaDeRegistro;
    }

    public String getDniPromotor() {
        return dniPromotor;
    }

    public void setDniPromotor(String dniPromotor) {
        this.dniPromotor = dniPromotor;
    }

    @Override
    public String toString() {
        return "Competicion{" +
                "cod='" + cod + '\'' +
                ", nombre='" + nombre + '\'' +
                ", localizacion='" + localizacion + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", tasaDeRegistro=" + tasaDeRegistro +
                ", dniPromotor='" + dniPromotor + '\'' +
                '}';
    }
}
