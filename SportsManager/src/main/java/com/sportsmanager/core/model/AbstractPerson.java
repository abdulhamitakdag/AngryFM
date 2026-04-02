package com.sportsmanager.core.model;

import com.sportsmanager.core.interfaces.ITrainable;
import java.util.UUID;

/**
 * Oyuncu ve koçların ortak özelliklerini barındıran temel sınıf.
 */
public abstract class AbstractPerson implements ITrainable {
    private final UUID id;
    private final String name;
    private int age;
    private final Gender gender;

    public AbstractPerson(String name, int age, Gender gender) {
        if (age < 15 || age > 50) {
            throw new IllegalArgumentException("Yaş 15 ile 50 arasında olmalıdır.");
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