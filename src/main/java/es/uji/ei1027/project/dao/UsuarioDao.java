package es.uji.ei1027.project.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.jasypt.util.password.BasicPasswordEncryptor;
import es.uji.ei1027.project.model.UserDetails;

import java.util.Collection;

@Repository
public class UsuarioDao{

    private JdbcTemplate jdbcTemplate;
    private final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public UserDetails loadUserByUsername(String username, String password) {
        try {
            UserDetails user = jdbcTemplate.queryForObject(
                    "SELECT username, password, tipo_usuario FROM Usuario WHERE username=?",
                    new Object[]{username},
                    new UserDetailsRowMapper()
            );
            if (user != null && passwordEncryptor.checkPassword(password, user.getPassword())) {
                return user;
            } else {
                return null;
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public Collection<UserDetails> listAllUsers() {
        return jdbcTemplate.query("SELECT username, password, tipo_usuario FROM Usuario", new UserDetailsRowMapper());
    }


    public void addUser(UserDetails user) {
        String sql = "INSERT INTO Usuario(username, password, tipo_usuario) VALUES(?, ?, ?)";
        String encryptedPassword = passwordEncryptor.encryptPassword(user.getPassword());
        jdbcTemplate.update(sql, user.getUsername(), encryptedPassword, user.getTipoUsuario());
    }

    public void deleteUser(String username) {
        jdbcTemplate.update("DELETE FROM Usuario WHERE username=?", username);

    }

    public String getPromotorDniByUsername(String username) {
        String sql = "SELECT username FROM Usuario WHERE username = ? AND tipo_usuario = 'promotor'";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getParticipanteDniByUsername(String username) {
        String sql = "SELECT username FROM Usuario WHERE username = ? AND tipo_usuario = 'participante'";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}