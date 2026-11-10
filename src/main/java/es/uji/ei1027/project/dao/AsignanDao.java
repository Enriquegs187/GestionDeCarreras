package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.controller.ProjectException;
import es.uji.ei1027.project.model.Asignan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@Repository
public class AsignanDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addAsignan(Asignan asignan) {
        try {
            jdbcTemplate.update("INSERT INTO Asignan (dniVoluntario, codPuntoControl) VALUES(?, ?)",
                    asignan.getDniVoluntario(), asignan.getCodPuntoControl());
        }catch (DuplicateKeyException e){
            throw new ProjectException("El voluntario con DNI " + asignan.getDniVoluntario() + " ya está asignado a este punto de control.",
                    "Asignación duplicada"
            );
        }
    }

    public void deleteAsignan(String dniVoluntario, String codPuntoControl) {
        jdbcTemplate.update("DELETE FROM Asignan WHERE dniVoluntario=? AND codPuntoControl=?",
               dniVoluntario, codPuntoControl);
    }
    public Asignan getAsignan(String dniVoluntario, String codPuntoControl) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Asignan WHERE dniVoluntario=? AND codPuntoControl=?",
                    new AsignanRowMapper(), dniVoluntario, codPuntoControl);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Asignan> getAsignan() {
        try {
            return jdbcTemplate.query("SELECT * FROM Asignan",
                    new AsignanRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }



    public List<Asignan> getVoluntariosByPuntoControl(String codPuntoControl) {
        try {
            return jdbcTemplate.query("SELECT * FROM Asignan WHERE codPuntoControl = ?",
                    new AsignanRowMapper(), codPuntoControl);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public boolean tieneAsignacionesActivas(String dniVoluntario) {
        String sql = "SELECT COUNT(*) FROM Asignan WHERE dniVoluntario = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{dniVoluntario}, Integer.class);
        return count != null && count > 0;
    }

}
