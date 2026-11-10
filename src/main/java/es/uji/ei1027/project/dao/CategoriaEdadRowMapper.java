package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.CategoriaEdad;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CategoriaEdadRowMapper implements RowMapper<CategoriaEdad> {
    @Override
    public CategoriaEdad mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoriaEdad categoriaEdad = new CategoriaEdad();
        categoriaEdad.setCod(rs.getString("cod"));
        categoriaEdad.setNombre(rs.getString("nombre"));
        categoriaEdad.setEdadMinima(rs.getInt("edadMinima"));
        // Comprobar si la columna edadMaxima es NULL antes de obtener el int
        int edadMaxima = rs.getInt("edadMaxima");
        if (rs.wasNull()) {
            categoriaEdad.setEdadMaxima(null);
        } else {
            categoriaEdad.setEdadMaxima(edadMaxima);
        }
        return categoriaEdad;
    }
}