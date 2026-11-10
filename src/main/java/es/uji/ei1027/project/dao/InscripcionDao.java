package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.controller.ProjectException;
import es.uji.ei1027.project.model.Inscripcion;
import es.uji.ei1027.project.model.RegistroTiempo;
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
public class InscripcionDao {
    private static final Logger LOG = LoggerFactory.getLogger(InscripcionDao.class);
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addInscripcion(Inscripcion inscripcion) {
        try {
            jdbcTemplate.update("INSERT INTO inscripcion (cod, tiempometa, codCarrera, dniParticipante,dorsal) VALUES (?, ?, ?, ?, ?)",
                    inscripcion.getCod(), null, inscripcion.getCodCarrera(), inscripcion.getDniParticipante(), inscripcion.getDorsal());

        } catch (Exception e) {
            throw new ProjectException("Error al crear inscripción", "Error");
        }
    }

    public void deleteInscripcion(Inscripcion inscripcion) {
        jdbcTemplate.update("DELETE FROM inscripcion WHERE cod=?", inscripcion.getCod());
    }

    public void deleteInscripcion(String cod) {
        jdbcTemplate.update("DELETE FROM inscripcion WHERE cod=?", cod);
    }


    public void updateInscripcion(Inscripcion inscripcion) {
        jdbcTemplate.update("UPDATE inscripcion SET tiempometa=?, codCarrera=?, dniParticipante=?,dorsal=? WHERE cod=?",
                inscripcion.getTiempometa(), inscripcion.getCodCarrera(), inscripcion.getDniParticipante(),inscripcion.getDorsal(), inscripcion.getCod());
    }


    public Inscripcion getInscripcion(String cod) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM inscripcion WHERE cod=?",
                    new InscripcionRowMapper(), cod);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Inscripcion getInscripcionByCarreraAndParticipante(String codCarrera, String dniParticipante) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM inscripcion WHERE codCarrera = ? AND dniParticipante = ?",
                    new InscripcionRowMapper(), codCarrera, dniParticipante);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Inscripcion> getInscripcion() {
        try {
            return jdbcTemplate.query("SELECT * FROM inscripcion", new InscripcionRowMapper());
        } catch (Exception e) {
            LOG.error("Error al obtener inscripciones: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public void deleteByCarreraAndParticipante(String codCarrera, String dniParticipante) {
        jdbcTemplate.update("DELETE FROM inscripcion WHERE codCarrera = ? AND dniParticipante = ?",
                codCarrera, dniParticipante);
    }

    public Inscripcion getInscripcionByDniAndDorsal(String dniParticipante, String dorsal) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM Inscripcion WHERE dniParticipante = ? AND dorsal = ?",
                    new InscripcionRowMapper(),
                    dniParticipante, dorsal
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Inscripcion> getInscripcionesPorCarrera(String codCarrera) {
        try {
            return jdbcTemplate.query("SELECT * FROM inscripcion WHERE codCarrera = ?",
                    new InscripcionRowMapper(), codCarrera);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Inscripcion> getClasificacionPorCarrera(String codCarrera, String orden) {
        String orderBy = "ASC";
        if ("desc".equalsIgnoreCase(orden)) {
            orderBy = "DESC";
        }


        String sql = "SELECT * FROM inscripcion " +
                "WHERE codCarrera = ? AND tiempometa IS NOT NULL " +
                "ORDER BY tiempometa " + orderBy;
        try {
            return jdbcTemplate.query(sql, new InscripcionRowMapper(), codCarrera);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Inscripcion getInscripcionPorParticipanteYCarrera(String dniParticipante, String codCarrera) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM inscripcion WHERE dniParticipante = ? AND codCarrera = ?",
                    new InscripcionRowMapper(), dniParticipante, codCarrera);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public List<Inscripcion> getInscripcionesSinTiempo(String codCarrera) {
        try {
            return jdbcTemplate.query("SELECT * FROM inscripcion WHERE codCarrera = ? AND tiempometa IS NULL",
                    new InscripcionRowMapper(), codCarrera);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


}
