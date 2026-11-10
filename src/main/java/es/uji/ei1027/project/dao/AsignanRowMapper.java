package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Asignan;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AsignanRowMapper implements RowMapper<Asignan> {
    @Override
    public Asignan mapRow(ResultSet rs, int rowNum) throws SQLException {
        Asignan asignan = new Asignan();
        asignan.setDniVoluntario(rs.getString("dniVoluntario"));
        asignan.setCodPuntoControl(rs.getString("codPuntoControl"));
        return asignan;
    }
}
