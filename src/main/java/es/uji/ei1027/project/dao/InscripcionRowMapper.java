package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Inscripcion;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class InscripcionRowMapper implements RowMapper<Inscripcion> {
    @Override
    public Inscripcion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setCod(rs.getString("cod"));
        inscripcion.setCodCarrera(rs.getString("codCarrera"));
        inscripcion.setDniParticipante(rs.getString("dniParticipante"));
        inscripcion.setDorsal(rs.getString("dorsal"));

        Time timeFromDb = rs.getTime("tiempometa");

        if (timeFromDb != null) {
            inscripcion.setTiempometa(timeFromDb.toLocalTime());
        }

        return inscripcion;
    }
}
