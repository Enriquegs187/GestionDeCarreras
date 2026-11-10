package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.FichaClub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FichaClubDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Añadir una ficha de club
    public void addFichaClub(FichaClub ficha) {
        jdbcTemplate.update("INSERT INTO FichaClub (id, validezFecha, dniParticipante, cifClub) VALUES(?, ?, ?, ?)",
                ficha.getId(), ficha.getValidezFecha(), ficha.getDniParticipante(), ficha.getCifClub());
    }

    // Eliminar una ficha de club
    public void deleteFichaClub(FichaClub ficha) {
        jdbcTemplate.update("DELETE FROM FichaClub WHERE id=?", ficha.getId());
    }

    public void deleteFichaClub(String id) {
        jdbcTemplate.update("DELETE FROM FichaClub WHERE id=?", id);
    }

    // Actualizar una ficha de club
    public void updateFichaClub(FichaClub ficha) {
        jdbcTemplate.update("UPDATE FichaClub SET validezFecha=?, dniParticipante=?, cifClub=? WHERE id=?",
                ficha.getValidezFecha(), ficha.getDniParticipante(), ficha.getCifClub(), ficha.getId());
    }

    // Obtener una ficha de club por ID
    public FichaClub getFichaClub(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM FichaClub WHERE id=?",
                    new FichaClubRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Obtener todas las fichas de club
    public List<FichaClub> getFichasClub() {
        try {
            return jdbcTemplate.query("SELECT * FROM FichaClub",
                    new FichaClubRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}

