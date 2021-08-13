package learn.pets.data;

import learn.pets.models.Pet;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;



import org.springframework.context.annotation.Profile;

@Repository
@Profile("jdbc-template") // NEW ANNOTATION
public class PetJdbcTemplateRepository implements PetRepository {



    private final JdbcTemplate jdbcTemplate;

    public PetJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Pet> findAll() {
        final String sql = "select pet_id, `name`, `type` from pet;";
        /* Lambda version of mapping commented out here

        return jdbcTemplate.query(sql, (resultSet, rowNum) ->
        {
            Pet pet = new Pet();
            pet.setName(resultSet.getString("name"));
            pet.setType(resultSet.getString("type"));
            pet.setPetId(resultSet.getInt("pet_id"));
            return pet;
        });*/

        return jdbcTemplate.query(sql, new PetMapper());

    }

    @Override
    public Pet findByName(String petName) {
        return null;
    }

    @Override
    public Pet findById(int petId) {
        final String sql = "SELECT pet_id, `name`, `type` FROM pet where pet_id = ?;";
        try{
            return jdbcTemplate.queryForObject(sql, new PetMapper() ,petId);

        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Pet add(Pet pet) {
        final String sql = "insert into pet(`name`, `type`) values (?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,pet.getName());
            ps.setString(2,pet.getType());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        pet.setPetId(keyHolder.getKey().intValue());

        return pet;

    }

    @Override
    public boolean update(Pet pet) {
        final String sql = "update pet set "
                + "`name` = ? "
                + "`type` = ? "
                + "where pet_id = ?;";

        int rowsUpdated = jdbcTemplate.update(sql,
                pet.getName(), pet.getType(), pet.getPetId());

        return rowsUpdated > 0;
    }

    @Override
    public boolean deleteById(int petId) {
        final String sql = "delete from pet where pet_id = ?;";
        return jdbcTemplate.update(sql, petId) > 0;
    }
}
