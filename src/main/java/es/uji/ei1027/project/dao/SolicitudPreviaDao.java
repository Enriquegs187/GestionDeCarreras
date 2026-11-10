package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.SolicitudPrevia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SolicitudPreviaDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Añadir una solicitud previa
    public void addSolicitudPrevia(SolicitudPrevia solicitud) {
        jdbcTemplate.update("INSERT INTO SolicitudPrevia (id, nombre, estadoSolicitud, fechaAprobacion, motivo, personaContacto, mailEmpresaContacto, dniPromotor) VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                solicitud.getId(), solicitud.getNombre(), solicitud.getEstadoSolicitud(), solicitud.getFechaAprobacion(),
                solicitud.getMotivo(), solicitud.getPersonaContacto(), solicitud.getMailEmpresaContacto(), solicitud.getDniPromotor());
    }

    // Eliminar una solicitud previa
    public void deleteSolicitudPrevia(String id) {
        jdbcTemplate.update("DELETE FROM SolicitudPrevia WHERE id=?", id);
    }

    // Actualizar una solicitud previa
    public void updateSolicitudPrevia(SolicitudPrevia solicitud) {
        jdbcTemplate.update("UPDATE SolicitudPrevia SET nombre=?, estadoSolicitud=?, fechaAprobacion=?, motivo=?, personaContacto=?, mailEmpresaContacto=?, dniPromotor=? WHERE id=?",
                solicitud.getNombre(), solicitud.getEstadoSolicitud(), solicitud.getFechaAprobacion(),
                solicitud.getMotivo(), solicitud.getPersonaContacto(), solicitud.getMailEmpresaContacto(), solicitud.getDniPromotor(),
                solicitud.getId());
    }

    // Obtener una solicitud previa por ID
    public SolicitudPrevia getSolicitudPrevia(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM SolicitudPrevia WHERE id=?",
                    new SolicitudPreviaRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Obtener todas las solicitudes previas
    public List<SolicitudPrevia> getSolicitudesPrevias() {
        try {
            return jdbcTemplate.query("SELECT * FROM SolicitudPrevia",
                    new SolicitudPreviaRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}

