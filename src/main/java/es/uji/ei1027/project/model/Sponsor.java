package es.uji.ei1027.project.model;

public class Sponsor {

    private String cod;
    private String nombre;


    public Sponsor() {
    }

    public String getCod() { return cod;}
    public void setCod(String cod) { this.cod = cod;}

    public String getNombre() { return nombre;}
    public void setNombre(String nombre) { this.nombre = nombre;}

    @Override
    public String toString() {
        return "Sponsor{" +
                "cod='" + cod + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}


