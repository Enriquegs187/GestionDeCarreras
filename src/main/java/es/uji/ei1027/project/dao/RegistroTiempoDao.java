package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.RegistroTiempo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RegistroTiempoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addRegistroTiempo(RegistroTiempo registro) {
        jdbcTemplate.update("INSERT INTO RegistroTiempo (cod, tiempo, comentarios, codPuntoControl, codInscripcion) VALUES(?, ?, ?, ?, ?)",
                registro.getCod(), registro.getTiempo(), registro.getComentarios(),
                registro.getCodPuntoControl(), registro.getCodInscripcion());
    }

    public void deleteRegistroTiempo(RegistroTiempo registro) {
        jdbcTemplate.update("DELETE FROM RegistroTiempo WHERE cod=?", registro.getCod());
    }

    public void deleteRegistroTiempo(String cod) {
        jdbcTemplate.update("DELETE FROM RegistroTiempo WHERE cod=?", cod);
    }

    public void updateRegistroTiempo(RegistroTiempo registro) {
        jdbcTemplate.update("UPDATE RegistroTiempo SET tiempo=?, comentarios=?, codPuntoControl=?, codInscripcion=? WHERE cod=?",
                registro.getTiempo(), registro.getComentarios(), registro.getCodPuntoControl(), registro.getCodInscripcion(), registro.getCod());
    }

    public RegistroTiempo getRegistroTiempo(String cod) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM RegistroTiempo WHERE cod=?",
                    new RegistroTiempoRowMapper(), cod);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<RegistroTiempo> getRegistrosTiempo() {
        try {
            return jdbcTemplate.query("SELECT * FROM RegistroTiempo",
                    new RegistroTiempoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<RegistroTiempo> getRegistrosPorCarrera(String codCarrera) {
        String sql = "SELECT rt.* " + "FROM RegistroTiempo rt " + "JOIN Inscripcion i ON rt.codInscripcion = i.cod " + "WHERE i.codCarrera = ?";
        try {
            return jdbcTemplate.query(sql, new RegistroTiempoRowMapper(), codCarrera);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<RegistroTiempo> getRegistrosFiltradosYOrdenados(String codCarrera, String nombreParticipante, String codPuntoControl, String orden) {
        StringBuilder sql = new StringBuilder(
                "SELECT rt.* FROM RegistroTiempo rt " +
                        "JOIN Inscripcion i ON rt.codInscripcion = i.cod " +
                        "JOIN Participante p ON i.dniParticipante = p.dni " +
                        "WHERE i.codCarrera = ?");

        List<Object> params = new ArrayList<>();
        params.add(codCarrera);

        if (nombreParticipante != null && !nombreParticipante.trim().isEmpty()) {
            sql.append(" AND (p.nombre LIKE ? OR p.apellido LIKE ?)");
            String searchTerm = "%" + nombreParticipante.trim() + "%";
            params.add(searchTerm);
            params.add(searchTerm);
        }

        if (codPuntoControl != null && !codPuntoControl.trim().isEmpty()) {
            sql.append(" AND rt.codPuntoControl = ?");
            params.add(codPuntoControl);
        }

        if ("desc".equalsIgnoreCase(orden)) {
            sql.append(" ORDER BY rt.tiempo DESC");
        } else {
            sql.append(" ORDER BY rt.tiempo ASC");
        }

        try {
            return jdbcTemplate.query(sql.toString(), new RegistroTiempoRowMapper(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<RegistroTiempo> getRegistrosPorInscripcion(String codInscripcion) {
        try {
            return jdbcTemplate.query("SELECT * FROM RegistroTiempo WHERE codInscripcion = ? ORDER BY tiempo ASC",
                    new RegistroTiempoRowMapper(), codInscripcion);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public boolean existeRegistroParaInscripcionYPunto(String codInscripcion, String codPuntoControl) {
        String sql = "SELECT COUNT(*) FROM RegistroTiempo WHERE codInscripcion = ? AND codPuntoControl = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{codInscripcion, codPuntoControl}, Integer.class);
        return count != null && count > 0;
    }


}
