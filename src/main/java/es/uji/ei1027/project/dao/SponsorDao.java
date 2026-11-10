package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.controller.ProjectException;
import es.uji.ei1027.project.model.Sponsor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SponsorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addSponsor(Sponsor sponsor) {
        try {
            jdbcTemplate.update("INSERT INTO Sponsor (cod, nombre) VALUES(?, ?)",
                    sponsor.getCod(), sponsor.getNombre());
        }catch (DuplicateKeyException e){
            throw new ProjectException("Ya existe el sponsor " + sponsor.getNombre(),
                    "Sponsor duplicado");
        }
    }


    public void deleteSponsor(Sponsor sponsor) {
        jdbcTemplate.update("DELETE FROM Sponsor WHERE cod=?", sponsor.getCod());
    }

    public void deleteSponsor(String cod) {
        jdbcTemplate.update("DELETE FROM Sponsor WHERE cod=?", cod);
    }

    public void updateSponsor(Sponsor sponsor) {
        jdbcTemplate.update("UPDATE Sponsor SET nombre=? WHERE cod=?",
                sponsor.getNombre(), sponsor.getCod());
    }

    public Sponsor getSponsor(String codSponsor) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Sponsor WHERE cod=?",
                    new SponsorRowMapper(),
                    codSponsor);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Sponsor> getSponsors() {
        try {
            return jdbcTemplate.query("SELECT * FROM Sponsor",
                    new SponsorRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Sponsor> getSponsorsByCompeticion(String cod) {
        String sql = "SELECT s.* FROM sponsor s " +
                "JOIN aportacion a ON s.cod = a.codSponsor " +
                "WHERE a.codCompeticion = ?";

        return jdbcTemplate.query(sql, new Object[]{cod}, new SponsorRowMapper());
    }

}
