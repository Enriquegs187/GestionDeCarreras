package es.uji.ei1027.project.model;

public class Club {
    private String cif;
    private String nombre;
    private String dniPromotor;

    public Club() {
    }

    public Club(String cif, String nombre, String dniPromotor) {
        this.cif = cif;
        this.nombre = nombre;
        this.dniPromotor = dniPromotor;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDniPromotor() {
        return dniPromotor;
    }

    public void setDniPromotor(String dniPromotor) {
        this.dniPromotor = dniPromotor;
    }

    @Override
    public String toString() {
        return "Club{" +
                "cif='" + cif + '\'' +
                ", nombre='" + nombre + '\'' +
                ", dniPromotor='" + dniPromotor + '\'' +
                '}';
    }
}
