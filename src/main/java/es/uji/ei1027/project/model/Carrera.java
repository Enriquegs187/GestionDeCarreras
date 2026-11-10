package es.uji.ei1027.project.model;

import java.util.List;

public class Carrera {
    private String cod;
    private float distancia;
    private float altitud;
    private String inicio;
    private String fin;
    private String categoriaSexo;
    private float precio;
    private String codCompeticion;
    private List<String> categoriasEdadSeleccionadas;


    public Carrera() {
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public float getAltitud() {
        return altitud;
    }

    public void setAltitud(float altitud) {
        this.altitud = altitud;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public float getPrecio() {
        return precio;
    }
    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getCategoriaSexo() {return categoriaSexo;}

    public void setCategoriaSexo(String categoriaSexo) {this.categoriaSexo = categoriaSexo;}

    public String getCodCompeticion() {
        return codCompeticion;
    }

    public void setCodCompeticion(String codCompeticion) {
        this.codCompeticion = codCompeticion;
    }

    public List<String> getCategoriasEdadSeleccionadas() {
        return categoriasEdadSeleccionadas;
    }

    public void setCategoriasEdadSeleccionadas(List<String> categoriasEdadSeleccionadas) {
        this.categoriasEdadSeleccionadas = categoriasEdadSeleccionadas;
    }

    @Override
    public String toString() {
        return "Carrera{" +
                "cod='" + cod + '\'' +
                ", distancia=" + distancia +
                ", altitud=" + altitud +
                ", inicio='" + inicio + '\'' +
                ", fin='" + fin + '\'' +
                ", sexo='" + categoriaSexo + '\'' +
                ", precio=" + precio + '\'' +
                '}';
    }

    public String getCategoriasEdadString() {
        if (this.categoriasEdadSeleccionadas == null || this.categoriasEdadSeleccionadas.isEmpty()) {
            return "N/A";
        }
        return String.join(", ", this.categoriasEdadSeleccionadas);
    }
}
