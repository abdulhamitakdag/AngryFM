package com.sportsmanager.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractCoach extends BaseTest {

    static class DummyCoach extends AbstractCoach {

        public DummyCoach(String name, int age, Gender gender,
                          CoachSpecialty specialty, int coachingLevel) {
            super(name, age, gender, specialty, coachingLevel);
        }

        @Override
        public double specialtyMultiplier() {
            return 1.5;
        }
    }

    @Test
    void constructorStoresSpecialtyCorrectly_Male() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 3);

        assertEquals(CoachSpecialty.ATTACKING, coach.getSpecialty());
    }

    @Test
    void constructorStoresSpecialtyCorrectly_Female() {
        AbstractCoach coach = new DummyCoach("Ayşe", 40, Gender.FEMALE,
                CoachSpecialty.DEFENDING, 3);

        assertEquals(CoachSpecialty.DEFENDING, coach.getSpecialty());
    }

    @Test
    void constructorStoresCoachingLevelCorrectly_Male() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 4);

        assertEquals(4, coach.getCoachingLevel());
    }

    @Test
    void constructorStoresCoachingLevelCorrectly_Female() {
        AbstractCoach coach = new DummyCoach("Ayşe", 40, Gender.FEMALE,
                CoachSpecialty.DEFENDING, 2);

        assertEquals(2, coach.getCoachingLevel());
    }

    @Test
    void constructorThrowsExceptionWhenSpecialtyIsNull_Male() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyCoach("Ahmet", 40, Gender.MALE, null, 3));
    }

    @Test
    void constructorThrowsExceptionWhenSpecialtyIsNull_Female() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyCoach("Ayşe", 40, Gender.FEMALE, null, 3));
    }

    @Test
    void constructorThrowsExceptionWhenCoachingLevelBelow1_Male() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyCoach("Ahmet", 40, Gender.MALE,
                        CoachSpecialty.ATTACKING, 0));
    }

    @Test
    void constructorThrowsExceptionWhenCoachingLevelAbove5_Female() {
        assertThrows(IllegalArgumentException.class, () ->
                new DummyCoach("Ayşe", 40, Gender.FEMALE,
                        CoachSpecialty.DEFENDING, 6));
    }

    @Test
    void setSpecialtyUpdatesSpecialty_Male() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 3);

        coach.setSpecialty(CoachSpecialty.DEFENDING);

        assertEquals(CoachSpecialty.DEFENDING, coach.getSpecialty());
    }

    @Test
    void setSpecialtyUpdatesSpecialty_Female() {
        AbstractCoach coach = new DummyCoach("Ayşe", 40, Gender.FEMALE,
                CoachSpecialty.DEFENDING, 3);

        coach.setSpecialty(CoachSpecialty.ATTACKING);

        assertEquals(CoachSpecialty.ATTACKING, coach.getSpecialty());
    }

    @Test
    void setCoachingLevelUpdatesLevelWhenValid_Male() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 2);

        coach.setCoachingLevel(5);

        assertEquals(5, coach.getCoachingLevel());
    }

    @Test
    void setCoachingLevelUpdatesLevelWhenValid_Female() {
        AbstractCoach coach = new DummyCoach("Ayşe", 40, Gender.FEMALE,
                CoachSpecialty.DEFENDING, 2);

        coach.setCoachingLevel(1);

        assertEquals(1, coach.getCoachingLevel());
    }

    @Test
    void setCoachingLevelThrowsExceptionWhenBelow1_Male() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 3);

        assertThrows(IllegalArgumentException.class, () ->
                coach.setCoachingLevel(0));
    }

    @Test
    void setCoachingLevelThrowsExceptionWhenAbove5_Female() {
        AbstractCoach coach = new DummyCoach("Ayşe", 40, Gender.FEMALE,
                CoachSpecialty.DEFENDING, 3);

        assertThrows(IllegalArgumentException.class, () ->
                coach.setCoachingLevel(6));
    }

    @Test
    void getTrainingEffectivenessReturnsCorrectValueLevel1() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 1);

        assertEquals(1.1, coach.getTrainingEffectiveness());
    }

    @Test
    void getTrainingEffectivenessReturnsCorrectValueLevel5() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 5);

        assertEquals(1.5, coach.getTrainingEffectiveness());
    }

    @Test
    void specialtyMultiplierReturnsDummyValue() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 3);

        assertEquals(1.5, coach.specialtyMultiplier());
    }

    @Test
    void trainDoesNotThrowException() {
        AbstractCoach coach = new DummyCoach("Ahmet", 40, Gender.MALE,
                CoachSpecialty.ATTACKING, 3);

        assertDoesNotThrow(() -> coach.train(2.0));
    }
}