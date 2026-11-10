package es.uji.ei1027.project.dao;


import es.uji.ei1027.project.model.Competicion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CompeticionRowMapper implements RowMapper<Competicion> {
    @Override
    public Competicion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Competicion competicion = new Competicion();
        competicion.setCod(rs.getString("cod"));
        competicion.setNombre(rs.getString("nombre"));
        competicion.setLocalizacion(rs.getString("localizacion"));
        competicion.setFechaInicio(rs.getObject("fechaInicio", LocalDate.class));
        competicion.setFechaFin(rs.getObject("fechaFin", LocalDate.class));
        competicion.setTasaDeRegistro(rs.getDouble("tasaDeRegistro"));
        competicion.setDniPromotor(rs.getString("dniPromotor"));
        return competicion;
    }
}
