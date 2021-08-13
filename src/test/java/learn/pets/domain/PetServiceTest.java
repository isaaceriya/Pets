package learn.pets.domain;

import learn.pets.data.PetRepository;
import learn.pets.models.Pet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

// 1. No mocked HTTP server
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PetServiceTest {

    // 2. Mock the interface. Behavior is added per test.
    @MockBean
    PetRepository repository;

    // 3. The mock PetRepository is injected into PetService
    // and PetService is injected into PetServiceTest.
    @Autowired
    PetService service;

    @Test
    void shouldAdd() {
        // Arrange
        Pet petIn = new Pet(0, "Ule", "Mantis");
        Pet petOut = new Pet(1, "Ule", "Mantis");

        // 4. Stub a specific behavior.
        when(repository.add(petIn)).thenReturn(petOut);

        // Act
        Result<Pet> result = service.add(petIn);

        // Assert
        assertEquals(ResultType.SUCCESS, result.getType());
        assertEquals(petOut, result.getPayload());
    }


    /* Add Tests */
    @Test
    void shouldNotAddNull() {
        Result<Pet> result = service.add(null);
        assertEquals(ResultType.INVALID, result.getType());
        assertNull(result.getPayload());
    }

    @Test
    void shouldNotAddEmptyName() {
        Pet petIn = new Pet(0, " ", "Mantis");
        Result<Pet> result = service.add(petIn);
        assertEquals(ResultType.INVALID, result.getType());
        assertNull(result.getPayload());
    }

    /* Delete Tests */
    @Test
    void shouldNotDeleteMissingPet() {
        when(repository.deleteById(10)).thenReturn(false);
        assertFalse(service.deleteById(10));
    }

    @Test
    void shouldDelete() {
        when(repository.deleteById(2)).thenReturn(true);
        assertTrue(service.deleteById(2));
    }

}