package learn.pets.models;

import java.util.Objects;

public class Pet {

    // NEW: Pet constructors
    public Pet() {
    }

    public Pet(int petId, String name, String type) {
        this.petId = petId;
        this.name = name;
        this.type = type;
    }

    private int petId;
    private String name;
    private String type;

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petId=" + petId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return petId == pet.petId &&
                Objects.equals(name, pet.name) &&
                Objects.equals(type, pet.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(petId, name, type);
    }
}