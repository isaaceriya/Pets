package learn.pets.data;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;

import learn.pets.models.Pet;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

// NEW ANNOTATIONs
@Repository
@Profile("jdbc")
public class PetJdbcRepository implements PetRepository {


    // 1. Dangerous initialization during construction
    private DataSource dataSource = initDataSource();

    private DataSource initDataSource() {
        MysqlDataSource result = new MysqlDataSource();
        // 2. connection string is:
        // [db-tech]:[db-vendor]://[host]:[port]/[database-name]
        result.setUrl("jdbc:mysql://localhost:3306/pets");
        // 3. username
        result.setUser("root");
        // 4. password
        result.setPassword("top-secret-password");
        return result;
    }


    @Override
    public List<Pet> findAll() {
        ArrayList<Pet> result = new ArrayList<>();

        final String sql = "select pet_id, `name`, `type` from pet;";

        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Pet pet = new Pet();
                pet.setName(rs.getString("name"));
                pet.setType(rs.getString("type"));
                pet.setPetId(rs.getInt("pet_id"));


                result.add(pet);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @Override
    public Pet findByName(String petName) {
        // SQL Injection is possible here!
        String sql = "select * from pet where name = '" + petName + "';";

        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            if (rs.next()) {
                Pet pet = new Pet();
                pet.setPetId(rs.getInt("pet_id"));
                pet.setName(rs.getString("name"));
                pet.setType(rs.getString("type"));
                return pet;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Pet findById(int petId) {
        // The '?' placeholder requires a value.
        final String sql = "select pet_id, `name`, `type` from pet where pet_id = ?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            // Replace the first '?' with petId.
            statement.setInt(1, petId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Pet pet = new Pet();
                    pet.setPetId(rs.getInt("pet_id"));
                    pet.setName(rs.getString("name"));
                    pet.setType(rs.getString("type"));
                    return pet;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Pet add(Pet pet) {
        final String sql = "insert into pet (`name`, `type`) value (?,?);";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, pet.getName());
            statement.setString(2, pet.getType());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted <= 0) {
                return null;
            }

            try(ResultSet keys = statement.getGeneratedKeys()) {
                if(keys.next()) {
                    pet.setPetId(keys.getInt(1));
                }
                else {
                    return null;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return pet;
    }

    @Override
    public boolean update(Pet pet) {

        final String sql = "update pet set "
                + "`name` = ?, "
                + "`type` = ? "
                + "where pet_id = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, pet.getName());
            statement.setString(2, pet.getType());
            statement.setInt(3, pet.getPetId());

            return statement.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteById(int petId) {
        final String sql = "delete from pet where pet_id = ?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, petId);
            return statement.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}