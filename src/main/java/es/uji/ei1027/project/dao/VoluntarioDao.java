package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoluntarioDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addVoluntario(Voluntario voluntario) {
        jdbcTemplate.update("INSERT INTO Voluntario (dni, nombre, apellido, email, disponibilidad, estado) VALUES(?, ?, ?, ?, ?, ?)",
                voluntario.getDni(), voluntario.getNombre(), voluntario.getApellido(), voluntario.getEmail(), voluntario.getDisponibilidad(), voluntario.isEstado());
    }

    public void deleteVoluntario(String dni) {
        jdbcTemplate.update("DELETE FROM Voluntario WHERE dni=?", dni);
    }

    public void updateVoluntario(Voluntario voluntario) {
        jdbcTemplate.update("UPDATE Voluntario SET nombre=?, apellido=?, email=?, disponibilidad=?, estado=? WHERE dni=?",
                voluntario.getNombre(), voluntario.getApellido(), voluntario.getEmail(), voluntario.getDisponibilidad(),voluntario.isEstado(), voluntario.getDni());
    }

    public Voluntario getVoluntario(String dni) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Voluntario WHERE dni=?",
                    new VoluntarioRowMapper(), dni);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Voluntario> getVoluntarios() {
        try {
            return jdbcTemplate.query("SELECT * FROM Voluntario",
                    new VoluntarioRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Voluntario> getVoluntariosPorEstado(boolean estado) {
        try {
            return jdbcTemplate.query("SELECT * FROM Voluntario WHERE estado = ?",
                    new VoluntarioRowMapper(), estado);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
