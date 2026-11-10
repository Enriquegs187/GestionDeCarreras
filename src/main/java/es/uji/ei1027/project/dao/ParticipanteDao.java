package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Participante;
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
public class ParticipanteDao {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipanteDao.class);
    private JdbcTemplate jdbcTemplate;

    // Obtiene el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Añade el participante a la base de datos */
    public void addParticipante(Participante participante) {
        jdbcTemplate.update("INSERT INTO participante VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                participante.getDni(), participante.getNombre(), participante.getApellido(), participante.getFechaNacimiento(), participante.getEmail(), participante.getMetodoDePago(), participante.getClubCif(),participante.getSexo());
    }

    /* Borra el Participante de la base de datos */
    public void deleteParticipante(Participante participante) {
        jdbcTemplate.update("DELETE FROM participante WHERE dni=?", participante.getDni());
    }
    /* Borra el Participante de la base de datos */
    public void deleteParticipante(String dni) {
        jdbcTemplate.update("DELETE FROM participante WHERE dni=?", dni);
    }

    /* Actualiza los atributos del Participante */
    public void updateParticipante(Participante participante) {
        jdbcTemplate.update("UPDATE participante SET nombre=?, apellido=?, fechaNacimiento=?, email=?, metodoDePago=? ,clubCif=?, sexo=? WHERE dni=?",
                participante.getNombre(), participante.getApellido(), participante.getFechaNacimiento(), participante.getEmail(), participante.getMetodoDePago(), participante.getClubCif(), participante.getSexo(), participante.getDni());
    }

    /* Obtiene el Participante con el dni dado. Devuelve null si no existe. */
    public Participante getParticipante(String dni) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM participante WHERE dni=?",
                    new ParticipanteRowMapper(),
                    dni);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obtiene todos los Participantes. Devuelve una lista vacía si no hay ninguno. */
    public List<Participante> getParticipantes() {
        try {
            return jdbcTemplate.query("SELECT * FROM participante",
                    new ParticipanteRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }

    }
    public List<Participante> getParticipantesByClub(String clubCif) {
        try {
            return jdbcTemplate.query("SELECT * FROM participante WHERE clubcif = ?", new ParticipanteRowMapper(), clubCif);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
