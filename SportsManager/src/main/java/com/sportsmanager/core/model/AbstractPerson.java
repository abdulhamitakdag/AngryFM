package com.sportsmanager.core.model;

import com.sportsmanager.core.interfaces.ITrainable;
import java.util.UUID;

// oyuncu ve koçların ortak özellikleri
public abstract class AbstractPerson implements ITrainable {
    private final UUID id;
    private final String name;
    private int age;
    private final Gender gender;

    public AbstractPerson(String name, int age, Gender gender) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null.");
        }
        if (age < 16 || age > 75) {
            throw new IllegalArgumentException("Age must be between 16 and 75.");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public Gender getGender() { return gender; }
}