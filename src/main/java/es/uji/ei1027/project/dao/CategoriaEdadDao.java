package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.CategoriaEdad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoriaEdadDao {
    private static final Logger LOG = LoggerFactory.getLogger(CategoriaEdadDao.class);
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addCategoriaEdad(CategoriaEdad categoriaEdad) {
        jdbcTemplate.update("INSERT INTO CategoriaEdad (cod, nombre, edadMinima, edadMaxima) VALUES(?, ?, ?, ?)",
                categoriaEdad.getCod(), categoriaEdad.getNombre(), categoriaEdad.getEdadMinima(), categoriaEdad.getEdadMaxima());
    }

    public void deleteCategoriaEdad(String cod) {
        jdbcTemplate.update("DELETE FROM CategoriaEdad WHERE cod=?", cod);
    }

    public void updateCategoriaEdad(CategoriaEdad categoriaEdad) {
        jdbcTemplate.update("UPDATE CategoriaEdad SET nombre=?, edadMinima=?, edadMaxima=? WHERE cod=?",
                categoriaEdad.getNombre(), categoriaEdad.getEdadMinima(), categoriaEdad.getEdadMaxima(), categoriaEdad.getCod());
    }

    public CategoriaEdad getCategoriaEdad(String cod) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM CategoriaEdad WHERE cod=?",
                    new CategoriaEdadRowMapper(),
                    cod);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<CategoriaEdad> getCategoriaEdad() {
        try {
            return jdbcTemplate.query("SELECT * FROM CategoriaEdad",
                    new CategoriaEdadRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}