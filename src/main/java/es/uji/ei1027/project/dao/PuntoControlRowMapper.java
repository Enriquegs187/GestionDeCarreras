package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.PuntoControl;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PuntoControlRowMapper implements RowMapper<PuntoControl> {
    @Override
    public PuntoControl mapRow(ResultSet rs, int rowNum) throws SQLException {
        PuntoControl puntoControl = new PuntoControl();
        puntoControl.setCod(rs.getString("cod"));
        puntoControl.setLocalizacion(rs.getString("localizacion"));
        puntoControl.setCodCarrera(rs.getString("codCarrera"));
        return puntoControl;
    }
}
