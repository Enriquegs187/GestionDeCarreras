package es.uji.ei1027.project.dao;

import es.uji.ei1027.project.model.Promotor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PromotorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Añade promotor a la base de datos
    public void addPromotor(Promotor promotor){
        jdbcTemplate.update("INSERT INTO Promotor VALUES(?, ?, ?, ?, ?)",
                promotor.getDni(), promotor.getNombre(), promotor.getNombreOrganizacion(),
                       promotor.getEmail(), promotor.getTelefono());
    }

    // Borra promotor de la base de datos
    public void deletePromotor(Promotor promotor){
        jdbcTemplate.update("DELETE FROM Promotor WHERE dni=?", promotor.getDni());
    }

    public void deletePromotor(String dni){
        jdbcTemplate.update("DELETE FROM Promotor WHERE dni=?", dni);
    }

    // Actualiza los atributos del Promotor
    public void updatePromotor(Promotor promotor){
        jdbcTemplate.update("UPDATE Promotor SET dni=?, nombre=?, nombreOrganizacion=?, email=?, telefono=? WHERE dni=?",
                promotor.getDni(), promotor.getNombre(), promotor.getNombreOrganizacion(),
                promotor.getEmail(), promotor.getTelefono(), promotor.getDni());
    }

    //Obtiene el Promotor con el DNI dado. Devuelve null si no existe
    public Promotor getPromotor(String dniPromotor){
        try{
            return jdbcTemplate.queryForObject("SELECT * FROM Promotor WHERE dni=?",
                    new PromotorRowMapper(),
                    dniPromotor);
        }
        catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //Obtiene todos los Promotores. Devuelve una lista vacía si no hay ninguno
    public List<Promotor> getPromotores(){
        try{
            return jdbcTemplate.query("SELECT * FROM Promotor",
                    new PromotorRowMapper());

        }
        catch(EmptyResultDataAccessException e){
            return new ArrayList<>();
        }
    }


}
