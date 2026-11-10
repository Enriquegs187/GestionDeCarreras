package es.uji.ei1027.project.model;

public class Voluntario {
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String disponibilidad;
    private boolean estado;


    public Voluntario() {
    }

    public Voluntario(String dni, String nombre, String apellido, String email, String disponibilidad) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.disponibilidad = disponibilidad;
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

    public boolean isEstado() {return estado;}
    public void setEstado(boolean estado) {this.estado = estado;}

    @Override
    public String toString() {
        return "Voluntario{" +
                "dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", disponibilidad='" + disponibilidad + '\'' +
                ", estado=" + estado + // AÑADIDO
                '}';
    }
}
