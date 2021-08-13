package learn.pets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.pets.data.PetRepository;
import learn.pets.models.Pet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest       // 1. Without an argument, @SpringBootTest creates a mock MVC environment.
@AutoConfigureMockMvc // 2. Configure the mock MVC environment.
class PetControllerTest {

    // 3. Mock PetRepository with Mockito.
    // This ensure we don't need to worry about true data access.
    @MockBean
    PetRepository repository;

    // 4. Create a field for mock MVC and let Spring Boot inject it.
    @Autowired
    MockMvc mvc;

    @Test
    void shouldGetAll() throws Exception {

        List<Pet> pets = List.of(
                new Pet(1, "Name #1", "Type #1"),
                new Pet(2, "Name #2", "Type #2"),
                new Pet(3, "Name #3", "Type #3")
        );

        // 5. ObjectMapper is the default JSON serializer for Spring MVC.
        // We use it to generate the expected HTTP response body.
        ObjectMapper jsonMapper = new ObjectMapper();
        String expectedJson = jsonMapper.writeValueAsString(pets);

        // 6. Configure the per-test behavior for mock PetRepository.
        when(repository.findAll()).thenReturn(pets);

        // 7. Send a mock HTTP request and assert facts about the response.
        mvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }


    @Test
    void shouldAdd() throws Exception {
        // 1. Configure per-test mock repository behavior.
        Pet petIn = new Pet(0, "Ule", "Mantis");
        Pet expected = new Pet(1, "Ule", "Mantis");

        when(repository.add(any())).thenReturn(expected);

        // 2. Generate both input and expected JSON.
        ObjectMapper jsonMapper = new ObjectMapper();
        String jsonIn = jsonMapper.writeValueAsString(petIn);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        // 3. Build the request.
        var request = post("/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        // 4. Send the request and assert.
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldNotAddEmptyName() throws Exception {
        // 1. No mock behavior required because
        // it never makes it to the repository.
        Pet petIn = new Pet(0, " ", "Mantis");

        // 2. Generate input JSON.
        ObjectMapper jsonMapper = new ObjectMapper();
        String jsonIn = jsonMapper.writeValueAsString(petIn);

        var request = post("/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        // 3. Send the request and assert.
        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }
}