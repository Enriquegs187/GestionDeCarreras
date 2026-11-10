package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.EmpresaExterna;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmpresaExternaDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addEmpresaExterna(EmpresaExterna empresaExterna) {
        jdbcTemplate.update("INSERT INTO EmpresaExterna (cif, nombre, dniPromotor) VALUES(?, ?, ?)",
                empresaExterna.getCif(), empresaExterna.getNombre(), empresaExterna.getDniPromotor());
    }

    public void deleteEmpresaExterna(EmpresaExterna empresaExterna) {
        jdbcTemplate.update("DELETE FROM EmpresaExterna WHERE cif=?", empresaExterna.getCif());
    }

    public void deleteEmpresaExterna(String cif) {
        jdbcTemplate.update("DELETE FROM EmpresaExterna WHERE cif=?", cif);
    }


    public void updateEmpresaExterna(EmpresaExterna empresaExterna) {
        jdbcTemplate.update("UPDATE EmpresaExterna SET nombre=?, dniPromotor=? WHERE cif=?",
                empresaExterna.getNombre(), empresaExterna.getDniPromotor(), empresaExterna.getCif());
    }


    public EmpresaExterna getEmpresaExterna(String cif) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM EmpresaExterna WHERE cif=?",
                    new EmpresaExternaRowMapper(), cif);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<EmpresaExterna> getEmpresaExterna() {
        try {
            return jdbcTemplate.query("SELECT * FROM EmpresaExterna",
                    new EmpresaExternaRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
