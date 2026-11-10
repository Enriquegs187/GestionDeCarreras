package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Voluntario;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoluntarioRowMapper implements RowMapper<Voluntario> {
    @Override
    public Voluntario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Voluntario voluntario = new Voluntario();
        voluntario.setDni(rs.getString("dni"));
        voluntario.setNombre(rs.getString("nombre"));
        voluntario.setApellido(rs.getString("apellido"));
        voluntario.setEmail(rs.getString("email"));
        voluntario.setDisponibilidad(rs.getString("disponibilidad"));
        voluntario.setEstado(rs.getBoolean("estado"));
        return voluntario;
    }
}
