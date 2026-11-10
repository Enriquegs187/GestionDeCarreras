package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Aportacion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AportacionRowMapper implements RowMapper<Aportacion>{
    @Override
    public Aportacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Aportacion aportacion = new Aportacion();
        aportacion.setCodCompeticion(rs.getString("codCompeticion"));
        aportacion.setCodSponsor(rs.getString("codSponsor"));
        return aportacion;
    }


}

