package learn.pets.controllers;

import learn.pets.domain.PetService;
import learn.pets.domain.Result;
import learn.pets.domain.ResultType;
import learn.pets.models.Pet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController          // 1. Spring DI and MVC
@CrossOrigin(origins = {"http://localhost:3000", "http://initial-domain.com"})    // NEW CODE
@RequestMapping("/pets") // 2. Base URL
public class PetController {

    private final PetService service;

    // 3. Auto-inject PetService
    public PetController(PetService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pet> findAll() {
        return service.findAll();
    }

    @GetMapping("/{petId}")
    public ResponseEntity<Pet> findById(@PathVariable int petId) {
        Pet pet = service.findById(petId);
        if (pet == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pet, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Pet> add(@RequestBody Pet pet) {
        Result<Pet> result = service.add(pet);
        if (result.getType() == ResultType.INVALID) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @PutMapping("/{petId}")
    public ResponseEntity<Void> update(@PathVariable int petId, @RequestBody Pet pet) {

        // id conflict. stop immediately.
        if (petId != pet.getPetId()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // NEW CODE: try/catch
        try {
            Result<Pet> result = service.update(pet);
            if (result.getType() == ResultType.INVALID) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (result.getType() == ResultType.NOT_FOUND) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            // 500 Internal Server Error
            // With no shared details.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> delete(@PathVariable int petId) {
        if (service.deleteById(petId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}