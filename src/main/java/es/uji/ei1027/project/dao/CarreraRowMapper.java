package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Carrera;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CarreraRowMapper implements RowMapper<Carrera> {
    @Override
    public Carrera mapRow(ResultSet rs, int rowNum) throws SQLException {
        Carrera carrera = new Carrera();
        carrera.setCod(rs.getString("cod"));
        carrera.setDistancia(rs.getFloat("distancia"));
        carrera.setAltitud(rs.getFloat("altitud"));
        carrera.setInicio(rs.getString("inicio"));
        carrera.setFin(rs.getString("fin"));
        carrera.setCategoriaSexo(rs.getString("categoriaSexo"));
        carrera.setPrecio(rs.getFloat("precio"));
        carrera.setCodCompeticion(rs.getString("codCompeticion"));
        return carrera;
    }
}
