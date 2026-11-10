package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Carrera;
import es.uji.ei1027.project.model.CategoriaEdad;
import es.uji.ei1027.project.model.Competicion;
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
public class CarreraDao {
    private static final Logger LOG = LoggerFactory.getLogger( CarreraDao.class);
    private JdbcTemplate jdbcTemplate;
    private CarreraCategoriaEdadDao carreraCategoriaEdadDao;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public void setCarreraCategoriaEdadDao(CarreraCategoriaEdadDao carreraCategoriaEdadDao) {
        this.carreraCategoriaEdadDao = carreraCategoriaEdadDao;
    }

    public void addCarrera(Carrera carrera) {
        jdbcTemplate.update("INSERT INTO Carrera VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                carrera.getCod(), carrera.getDistancia(), carrera.getAltitud(), carrera.getInicio(), carrera.getFin(), carrera.getCodCompeticion(), carrera.getPrecio(), carrera.getCategoriaSexo());
    }
    public void deleteCarrera(Carrera carrera) {
        jdbcTemplate.update("DELETE FROM Carrera WHERE cod=?", carrera.getCod());
    }

    public void deleteCarrera(String cod) {
        jdbcTemplate.update("DELETE FROM Carrera WHERE cod=?", cod);
    }




    public void updateCarrera(Carrera carrera) {
        jdbcTemplate.update("UPDATE Carrera SET distancia=?, altitud=?, inicio=?, fin=?, precio=?, codCompeticion=?, categoriaSexo=? WHERE cod=?",
                carrera.getDistancia(), carrera.getAltitud(), carrera.getInicio(), carrera.getFin(), carrera.getPrecio(), carrera.getCodCompeticion(),carrera.getCategoriaSexo(), carrera.getCod());
    }

    public Carrera getCarrera(String codCarrera) {
        try {
            Carrera carrera = jdbcTemplate.queryForObject("SELECT * FROM Carrera WHERE cod=?",
                    new CarreraRowMapper(),
                    codCarrera);
            if(carrera != null) {
                carrera.setCategoriasEdadSeleccionadas(carreraCategoriaEdadDao.getNombresCategoriasByCarrera(codCarrera));
            }
            return carrera;
        }
        catch(EmptyResultDataAccessException e) {
            LOG.error("Error al obtener carrera: {}", e.getMessage());
            return null;
        }
    }

    public List<Carrera> getCarrera() {
        try {
            return jdbcTemplate.query("SELECT * FROM Carrera",
                    new CarreraRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            LOG.error("Error al obtener carreras: {}", e.getMessage());
            return new ArrayList<>();
        }

    }

    public List<Carrera> getCarrerasPorCompeticion(String codCompeticion) {
        try {
            List<Carrera> carreras = jdbcTemplate.query("SELECT * FROM Carrera WHERE codCompeticion = ?",
                    new CarreraRowMapper(), codCompeticion);

            for (Carrera carrera : carreras) {
                carrera.setCategoriasEdadSeleccionadas(
                        carreraCategoriaEdadDao.getNombresCategoriasByCarrera(carrera.getCod())
                );
            }
            return carreras;
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Error al obtener carreras por competicion: {}", e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<Carrera> getCarrerasPorParticipante(String dniParticipante) {
        try {
            List<Carrera> carreras = jdbcTemplate.query(
                    "SELECT c.* FROM Carrera c JOIN Inscripcion i ON c.cod = i.codCarrera WHERE i.dniParticipante = ?",
                    new CarreraRowMapper(), dniParticipante);
            for (Carrera carrera : carreras) {
                carrera.setCategoriasEdadSeleccionadas(
                        carreraCategoriaEdadDao.getNombresCategoriasByCarrera(carrera.getCod())
                );
            }
            return carreras;
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Error al obtener carreras por participante: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public Carrera getCarreraCod(String cod) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Carrera WHERE cod=?", new CarreraRowMapper(), cod);
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Error al obtener codigo de la carrera: {}", e.getMessage());
            return null;
        }
    }
    public Competicion getCompeticion(String codCompeticion) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Competicion WHERE cod=?", new CompeticionRowMapper(), codCompeticion);
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Error al obtener competicion: {}", e.getMessage());
            return null;
        }
    }

    public List<CategoriaEdad> getCategoriasEdadByCarrera(String codCarrera) {
        try {
            return carreraCategoriaEdadDao.getCategoriasByCarrera(codCarrera);
        }catch(EmptyResultDataAccessException e) {
            LOG.error("Error al obtener categorias edad de la carrera: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

}
