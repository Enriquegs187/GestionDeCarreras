package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Club;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClubRowMapper implements RowMapper<Club> {
    @Override
    public Club mapRow(ResultSet rs, int rowNum) throws SQLException {
        Club club = new Club();
        club.setCif(rs.getString("cif"));
        club.setNombre(rs.getString("nombre"));
        club.setDniPromotor(rs.getString("dniPromotor"));
        return club;
    }
}

