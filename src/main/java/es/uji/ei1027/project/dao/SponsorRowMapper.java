package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Sponsor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SponsorRowMapper implements RowMapper<Sponsor> {
    @Override
    public Sponsor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Sponsor sponsor = new Sponsor();
        sponsor.setCod(rs.getString("cod"));
        sponsor.setNombre(rs.getString("nombre"));
        return sponsor;
    }
}
