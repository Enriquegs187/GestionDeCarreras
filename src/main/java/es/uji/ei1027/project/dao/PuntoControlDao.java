package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.PuntoControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PuntoControlDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void deletePuntoControl(PuntoControl puntoControl){
        jdbcTemplate.update("DELETE FROM PuntoControl WHERE cod=?", puntoControl.getCod());
    }

    public void deletePuntoControl(String cod){
        jdbcTemplate.update("DELETE FROM PuntoControl WHERE cod=?", cod);
    }

    public void updatePuntoControl(PuntoControl puntoControl){
        jdbcTemplate.update("UPDATE PuntoControl SET cod=?, localizacion=? WHERE cod=?",
                puntoControl.getCod(), puntoControl.getLocalizacion(), puntoControl.getCod());
    }

    public PuntoControl getPuntoControl(String cod){
        try{
            return jdbcTemplate.queryForObject("SELECT * FROM PuntoControl WHERE cod=?",
                    new PuntoControlRowMapper(),
                    cod);
        }
        catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<PuntoControl> getPuntosControl(){
        try{
            return jdbcTemplate.query("SELECT * FROM PuntoControl",
                    new PuntoControlRowMapper());

        }
        catch(EmptyResultDataAccessException e){
            return new ArrayList<>();
        }
    }

    public List<PuntoControl> getPuntosPorCarrera(String codCarrera) {
        try {
            return jdbcTemplate.query("SELECT * FROM PuntoControl WHERE codCarrera = ?",
                    new PuntoControlRowMapper(), codCarrera);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void addPuntoControl(PuntoControl puntoControl){
        jdbcTemplate.update("INSERT INTO PuntoControl (cod, localizacion, codCarrera) VALUES (?, ?, ?)",
                puntoControl.getCod(), puntoControl.getLocalizacion(), puntoControl.getCodCarrera());
    }



}
