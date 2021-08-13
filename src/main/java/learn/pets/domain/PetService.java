package learn.pets.domain;

import learn.pets.data.PetRepository;
import learn.pets.models.Pet;
import org.springframework.stereotype.Service;

import java.util.List;
// static import to skip the Mockito type name.
import static org.mockito.Mockito.mock;


@Service // Register the service with the Spring DI context.
public class PetService {

    // org.mockito.Mockito.mock() creates a dynamic class that
// satisfies PetRepository contracts.
    PetRepository repository = mock(PetRepository.class);


    // A concrete PetRepository will be injected here.
    public PetService(PetRepository repository) {
        this.repository = repository;
    }

    public List<Pet> findAll() {
        return repository.findAll();
    }

    public Pet findById(int petId) {
        return repository.findById(petId);
    }

    public Result<Pet> add(Pet pet) {
        Result<Pet> result = validate(pet);
        if (result.getType() != ResultType.SUCCESS) {
            return result;
        }

        Pet p = repository.add(pet);
        result.setPayload(p);
        return result;
    }

    public Result<Pet> update(Pet pet) {
        Result<Pet> result = validate(pet);
        if (result.getType() != ResultType.SUCCESS) {
            return result;
        }

        if (!repository.update(pet)) {
            result.addMessage("pet id " + pet.getPetId() + " not found", ResultType.NOT_FOUND);
        }
        return result;
    }

    public boolean deleteById(int petId) {
        return repository.deleteById(petId);
    }

    private Result<Pet> validate(Pet pet) {
        Result<Pet> result = new Result<>();

        if (pet == null) {
            result.addMessage("pet cannot be null", ResultType.INVALID);
            return result;
        }

        if (pet.getName() == null || pet.getName().trim().length() == 0) {
            result.addMessage("name is required", ResultType.INVALID);
        }

        if (pet.getType() == null || pet.getType().trim().length() == 0) {
            result.addMessage("type is required", ResultType.INVALID);
        }

        return result;
    }
}