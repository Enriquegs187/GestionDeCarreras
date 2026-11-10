package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Competicion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CompeticionDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addCompeticion(Competicion competicion) {
        jdbcTemplate.update("INSERT INTO competicion (cod, nombre, localizacion, fechainicio, fechafin, tasaderegistro, dnipromotor) VALUES(?, ?, ?, ?, ?, ?, ?)",
                competicion.getCod(),
                competicion.getNombre(),
                competicion.getLocalizacion(),
                competicion.getFechaInicio(),
                competicion.getFechaFin(),
                competicion.getTasaDeRegistro(),
                competicion.getDniPromotor());
    }

    public void deleteCompeticion(Competicion competicion) {
        jdbcTemplate.update("DELETE FROM competicion WHERE cod = ?", competicion.getCod());
    }

    public  void deleteCompeticion(String cod) {
        jdbcTemplate.update("DELETE FROM competicion WHERE cod = ?", cod);
    }

    public void updateCompeticion(Competicion competicion) {
        jdbcTemplate.update("UPDATE competicion SET nombre = ?, localizacion = ?, fechainicio = ?, fechafin = ?, tasaderegistro = ?, dnipromotor = ? WHERE cod = ?",
                competicion.getNombre(),
                competicion.getLocalizacion(),
                competicion.getFechaInicio(),
                competicion.getFechaFin(),
                competicion.getTasaDeRegistro(),
                competicion.getDniPromotor(),
                competicion.getCod());
    }

    public Competicion getCompeticion(String cod) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM competicion WHERE cod = ?",
                    new CompeticionRowMapper(), cod);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Competicion> getCompeticion() {
        try {
            return jdbcTemplate.query("SELECT * FROM competicion", new CompeticionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
