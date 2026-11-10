package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.UserDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDetailsRowMapper implements RowMapper<UserDetails> {
    @Override
    public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDetails user = new UserDetails();
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setTipoUsuario(rs.getString("tipo_usuario"));

        return user;
    }
}