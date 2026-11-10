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
public class CarreraCategoriaEdadDao {
    private static final Logger LOG = LoggerFactory.getLogger(CarreraCategoriaEdadDao.class);
    private JdbcTemplate jdbcTemplate;

    private CategoriaEdadRowMapper categoriaEdadRowMapper;

    @Autowired
    public void setCategoriaEdadRowMapper(CategoriaEdadRowMapper categoriaEdadRowMapper) {
        this.categoriaEdadRowMapper = categoriaEdadRowMapper;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addCarreraCategoriaEdad(String codCarrera, String codCategoriaEdad) {
        jdbcTemplate.update("INSERT INTO Carrera_CategoriaEdad (codCarrera, codCategoriaEdad) VALUES(?, ?)",
                codCarrera, codCategoriaEdad);
    }

    public void deleteCarreraCategoriaEdad(String codCarrera, String codCategoriaEdad) {
        jdbcTemplate.update("DELETE FROM Carrera_CategoriaEdad WHERE codCarrera=? AND codCategoriaEdad=?",
                codCarrera, codCategoriaEdad);
    }

    public void deleteCategoriasByCarrera(String codCarrera) {
        jdbcTemplate.update("DELETE FROM Carrera_CategoriaEdad WHERE codCarrera=?", codCarrera);
    }


    public List<CategoriaEdad> getCategoriasByCarrera(String codCarrera) {
        try {
            return jdbcTemplate.query(
                    "SELECT ce.* FROM CategoriaEdad ce " +
                            "JOIN Carrera_CategoriaEdad cce ON ce.cod = cce.codCategoriaEdad " +
                            "WHERE cce.codCarrera = ?",
                    categoriaEdadRowMapper,
                    codCarrera);
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Error al obtener categorias: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> getCarrerasByCategoria(String codCategoriaEdad) {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT codCarrera FROM Carrera_CategoriaEdad WHERE codCategoriaEdad = ?",
                    String.class,
                    codCategoriaEdad);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<String> getNombresCategoriasByCarrera(String codCarrera) {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT ce.cod FROM CategoriaEdad ce " +
                            "JOIN Carrera_CategoriaEdad cce ON ce.cod = cce.codCategoriaEdad " +
                            "WHERE cce.codCarrera = ?",
                    String.class,
                    codCarrera);
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Error al obtener nombres de categorias: {}", e.getMessage());

            return new ArrayList<>();
        }
    }
}