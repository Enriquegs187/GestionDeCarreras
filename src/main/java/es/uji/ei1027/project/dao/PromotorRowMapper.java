package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Promotor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PromotorRowMapper implements RowMapper<Promotor> {
    @Override
    public Promotor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Promotor promotor = new Promotor();
        promotor.setDni(rs.getString("dni"));
        promotor.setNombre(rs.getString("nombre"));
        promotor.setNombreOrganizacion(rs.getString("nombreOrganizacion"));
        promotor.setEmail(rs.getString("email"));
        promotor.setTelefono(rs.getString("telefono"));
        return promotor;
    }
}
