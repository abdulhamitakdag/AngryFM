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
        //burada bizim teknik direktör, sporcularla aynı yaş kısıtlamasına sahip oluyor
        //buradaki kısıtlamayı AbstractPlayer'a taşıyabiliriz, ne dersiniz
        //değiştirirsek testleri de ona göre değiştirmem gerekecek so
        //burayı değiştirirseniz pls ping me in the project gc

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null.");
        }
        if (age < 16 || age > 50) {
            throw new IllegalArgumentException("Age must be between 16 and 50.");
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