package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClubDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Añadir un club
    public void addClub(Club club) {
        jdbcTemplate.update("INSERT INTO Club (cif, nombre, dniPromotor) VALUES(?, ?, ?)",
                club.getCif(), club.getNombre(), club.getDniPromotor());
    }

    // Eliminar un club por objeto
    public void deleteClub(Club club) {
        jdbcTemplate.update("DELETE FROM Club WHERE cif=?", club.getCif());
    }

    // Eliminar un club por CIF
    public void deleteClub(String cif) {
        jdbcTemplate.update("DELETE FROM Club WHERE cif=?", cif);
    }

    // Actualizar un club
    public void updateClub(Club club) {
        jdbcTemplate.update("UPDATE Club SET nombre=?, dniPromotor=? WHERE cif=?",
                club.getNombre(), club.getDniPromotor(), club.getCif());
    }

    // Obtener un club por CIF
    public Club getClub(String cif) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Club WHERE cif=?",
                    new ClubRowMapper(), cif);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getNombrePromotor(String dniPromotor) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT nombre FROM Promotor WHERE dni=?",
                    String.class,
                    dniPromotor
            );
        } catch (EmptyResultDataAccessException e) {
            return "Promotor no encontrado";
        }
    }

    // Obtener todos los clubes
    public List<Club> getClubs() {
        try {
            return jdbcTemplate.query("SELECT * FROM Club",
                    new ClubRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
