package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractPerson extends BaseTest {

    static class DummyPerson extends AbstractPerson {

        public DummyPerson(String name, int age, Gender gender) {
            super(name, age, gender);
        }

        @Override
        public void train(double intensity) {
        }

        @Override
        public double getTrainingEffectiveness() {
            return 1.0;
        }
    }

    @Test
    void constructorStoresNameCorrectly_Male() {
        AbstractPerson person = new DummyPerson("Ahmet", 20, Gender.MALE);
        assertEquals("Ahmet", person.getName());
    }

    @Test
    void constructorStoresNameCorrectly_Female() {
        AbstractPerson person = new DummyPerson("Ayşe", 20, Gender.FEMALE);
        assertEquals("Ayşe", person.getName());
    }

    @Test
    void constructorStoresAgeCorrectly_Male() {
        AbstractPerson person = new DummyPerson("Ahmet", 20, Gender.MALE);
        assertEquals(20, person.getAge());
    }

    @Test
    void constructorStoresAgeCorrectly_Female() {
        AbstractPerson person = new DummyPerson("Ayşe", 20, Gender.FEMALE);
        assertEquals(20, person.getAge());
    }

    @Test
    void constructorStoresGenderCorrectly_Male() {
        AbstractPerson person = new DummyPerson("Ahmet", 20, Gender.MALE);
        assertEquals(Gender.MALE, person.getGender());
    }

    @Test
    void constructorStoresGenderCorrectly_Female() {
        AbstractPerson person = new DummyPerson("Ayşe", 20, Gender.FEMALE);
        assertEquals(Gender.FEMALE, person.getGender());
    }

    @Test
    void constructorAcceptsMinimumAge_Male() {
        AbstractPerson person = new DummyPerson("Mehmet", 16, Gender.MALE);
        assertEquals(16, person.getAge());
    }

    @Test
    void constructorAcceptsMinimumAge_Female() {
        AbstractPerson person = new DummyPerson("Zeynep", 16, Gender.FEMALE);
        assertEquals(16, person.getAge());
    }

    @Test
    void constructorAcceptsMaximumAge_Male() {
        AbstractPerson person = new DummyPerson("Mehmet", 50, Gender.MALE);
        assertEquals(50, person.getAge());
    }

    @Test
    void constructorAcceptsMaximumAge_Female() {
        AbstractPerson person = new DummyPerson("Zeynep", 50, Gender.FEMALE);
        assertEquals(50, person.getAge());
    }

    @Test
    void constructorThrowsExceptionWhenAgeIsBelow15_Male() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyPerson("Mehmet", 14, Gender.MALE));
    }

    @Test
    void constructorThrowsExceptionWhenAgeIsBelow15_Female() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyPerson("Zeynep", 14, Gender.FEMALE));
    }

    @Test
    void constructorThrowsExceptionWhenAgeIsAbove50_Male() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyPerson("Mehmet", 51, Gender.MALE));
    }

    @Test
    void constructorThrowsExceptionWhenAgeIsAbove50_Female() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyPerson("Zeynep", 51, Gender.FEMALE));
    }

    @Test
    void getIdReturnsNonNullId_Male() {
        AbstractPerson person = new DummyPerson("Ahmet", 20, Gender.MALE);
        assertNotNull(person.getId());
    }

    @Test
    void getIdReturnsNonNullId_Female() {
        AbstractPerson person = new DummyPerson("Ayşe", 20, Gender.FEMALE);
        assertNotNull(person.getId());
    }

    @Test
    void differentPeopleHaveDifferentIds() {
        AbstractPerson person1 = new DummyPerson("Ahmet", 20, Gender.MALE);
        AbstractPerson person2 = new DummyPerson("Ahmet", 20, Gender.MALE);

        assertNotEquals(person1.getId(), person2.getId());
    }
}