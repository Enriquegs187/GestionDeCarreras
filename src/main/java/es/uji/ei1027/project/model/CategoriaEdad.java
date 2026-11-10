package es.uji.ei1027.project.model;

public class CategoriaEdad {
    private String cod;
    private String nombre;
    private int edadMinima;
    private Integer edadMaxima;

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

    public int getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    public Integer getEdadMaxima() {
        return edadMaxima;
    }

    public void setEdadMaxima(Integer edadMaxima) {
        this.edadMaxima = edadMaxima;
    }

    @Override
    public String toString() {
        return "CategoriaEdad{" +
                "cod='" + cod + '\'' +
                ", nombre='" + nombre + '\'' +
                ", edadMinima=" + edadMinima +
                ", edadMaxima=" + edadMaxima +
                '}';
    }
}
