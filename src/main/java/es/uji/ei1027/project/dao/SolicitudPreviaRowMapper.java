package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.SolicitudPrevia;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class SolicitudPreviaRowMapper implements RowMapper<SolicitudPrevia> {
    @Override
    public SolicitudPrevia mapRow(ResultSet rs, int rowNum) throws SQLException {
        SolicitudPrevia solicitud = new SolicitudPrevia();
        solicitud.setId(rs.getString("id"));
        solicitud.setNombre(rs.getString("nombre"));
        solicitud.setEstadoSolicitud(rs.getString("estadoSolicitud"));
        solicitud.setFechaAprobacion(rs.getObject("fechaAprobacion") != null ? rs.getObject("fechaAprobacion", Date.class) : null);
        solicitud.setMotivo(rs.getString("motivo"));
        solicitud.setPersonaContacto(rs.getString("personaContacto"));
        solicitud.setMailEmpresaContacto(rs.getString("mailEmpresaContacto"));
        solicitud.setDniPromotor(rs.getString("dniPromotor"));
        return solicitud;
    }
}
