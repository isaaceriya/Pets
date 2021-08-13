package learn.pets.data;

import learn.pets.models.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 1. SpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PetJdbcTemplateRepositoryTest {

    // 2. Let Spring inject auto-configured dependencies.
    @Autowired
    PetJdbcTemplateRepository repository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    // 3. @BeforeAll work-around.
    static boolean hasSetUp = false;

    @BeforeEach
    void setup() {
        if (!hasSetUp) {
            hasSetUp = true;
            jdbcTemplate.update("call set_known_good_state();");
        }
    }

    // 4. Constructor and oneTimeSetup removed.

    /* <snip>
    All @Tests remain unchanged.
    They are omitted for clarity.
    </snip>
    */


    @Test
    void ShouldfindAll() {
        List<Pet> all = repository.findAll();

        assertNotNull(all);

        assertTrue(all.size() >=2);

        Pet expected = new Pet();
        expected.setPetId(1);
        expected.setName("Meep");
        expected.setType("Mouse");

        assertTrue((all.contains(expected) && all.stream().anyMatch(i -> i.getPetId() == 2)));

    }

    @Test
    void findById() {
        Pet expected = new Pet();
        expected.setPetId(1);
        expected.setName("Meep");
        expected.setType("Mouse");

        Pet actual = repository.findById(1);

        assertEquals(expected,actual);

    }

    @Test
    void shouldNotFindByID() {
        Pet actual = repository.findById(15000);
        assertNull(actual);
    }

    @Test
    void add() {
        Pet pet = new Pet();
        pet.setName("Ada");
        pet.setType("Dog");

        Pet actual = repository.add(pet);
        pet.setPetId(4);

        assertNotNull(actual);
        assertEquals(pet, actual);
    }

    @Test
    void shouldUpdateExisting() {
        Pet pet = new Pet();
        pet.setPetId(2);
        pet.setName("Singe");
        pet.setType("Snake");

        assertTrue(repository.update(pet));
        assertEquals(pet, repository.findById(2));
    }

    @Test
    void shouldNotUpdateMissing() {
        Pet pet = new Pet();
        pet.setPetId(20000);
        pet.setName("Singe");
        pet.setType("Snake");

        assertFalse(repository.update(pet));

    }

    @Test
    void deleteById() {
        assertTrue(repository.deleteById(3));
    }

    @Test
    void deleteByIdMissing() {
        assertFalse(repository.deleteById(150000));
    }
}