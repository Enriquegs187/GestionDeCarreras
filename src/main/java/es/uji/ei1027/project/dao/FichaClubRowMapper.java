package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.FichaClub;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class FichaClubRowMapper implements RowMapper<FichaClub> {
    @Override
    public FichaClub mapRow(ResultSet rs, int rowNum) throws SQLException {
        FichaClub fichaClub = new FichaClub();
        fichaClub.setId(rs.getString("id"));
        fichaClub.setValidezFecha(rs.getObject("validezFecha", Date.class));
        fichaClub.setDniParticipante(rs.getString("dniParticipante"));
        fichaClub.setCifClub(rs.getString("cifClub"));
        return fichaClub;
    }
}
