package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Participante;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ParticipanteRowMapper implements RowMapper<Participante> {
    @Override
    public Participante mapRow(ResultSet rs, int rowNum) throws SQLException {
        Participante participante = new Participante();
        participante.setDni(rs.getString("dni"));
        participante.setNombre(rs.getString("nombre"));
        participante.setApellido(rs.getString("apellido"));
        participante.setSexo(rs.getString("sexo"));
        participante.setFechaNacimiento(rs.getObject("fechaNacimiento", LocalDate.class));
        participante.setEmail(rs.getString("email"));
        participante.setMetodoDePago(rs.getString("metodoDePago"));
        participante.setClubCif(rs.getString("clubcif"));
        return participante;
    }
}
