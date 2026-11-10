package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.RegistroTiempo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class RegistroTiempoRowMapper implements RowMapper<RegistroTiempo> {
    @Override
    public RegistroTiempo mapRow(ResultSet rs, int rowNum) throws SQLException {
        RegistroTiempo registro = new RegistroTiempo();
        registro.setCod(rs.getString("cod"));
        registro.setTiempo(rs.getTime("tiempo").toLocalTime());
        registro.setComentarios(rs.getString("comentarios"));
        registro.setCodPuntoControl(rs.getString("codPuntoControl"));
        registro.setCodInscripcion(rs.getString("codInscripcion"));
        return registro;
    }
}
