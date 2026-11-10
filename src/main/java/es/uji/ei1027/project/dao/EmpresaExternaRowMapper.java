package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.EmpresaExterna;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaExternaRowMapper implements RowMapper<EmpresaExterna> {
    @Override
    public EmpresaExterna mapRow(ResultSet rs, int rowNum) throws SQLException {
        EmpresaExterna empresaExterna = new EmpresaExterna();
        empresaExterna.setCif(rs.getString("cif"));
        empresaExterna.setNombre(rs.getString("nombre"));
        empresaExterna.setDniPromotor(rs.getString("dniPromotor"));
        return empresaExterna;
    }
}
