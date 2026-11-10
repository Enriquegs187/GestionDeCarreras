package es.uji.ei1027.project.dao;


import es.uji.ei1027.project.controller.ProjectException;
import es.uji.ei1027.project.model.Aportacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AportacionDao {
    private static final Logger LOG = LoggerFactory.getLogger( AportacionDao.class);
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAportacion(Aportacion aportacion) {
        try {
            jdbcTemplate.update("INSERT INTO Aportacion (codCompeticion, codSponsor) VALUES (?, ?)",
                    aportacion.getCodCompeticion(), aportacion.getCodSponsor());
        } catch (DuplicateKeyException e){
            throw new ProjectException("Ya has asignado el sponsor a esta competición",
                    "Aportación duplicada"
            );
        }
    }

    public void deleteAportacion(Aportacion aportacion) {
        jdbcTemplate.update("DELETE FROM Aportacion WHERE codCompeticion=? AND codSponsor=?",aportacion.getCodCompeticion(), aportacion.getCodSponsor());
    }

    public void deleteAportacion(String codCompeticon, String codSponsor) {
        jdbcTemplate.update("DELETE FROM Aportacion WHERE codCompeticion=? AND codSponsor=?", codCompeticon, codSponsor);
    }

    public Aportacion getAportacion(String codCompeticion, String codSponsor) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Aportacion WHERE codCompeticion=? AND codSponsor=?",
                    new AportacionRowMapper(),
                    codCompeticion, codSponsor);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Aportacion> getAportacion() {
        try {
            return jdbcTemplate.query("SELECT * FROM Aportacion",
                    new AportacionRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }

    }
}
