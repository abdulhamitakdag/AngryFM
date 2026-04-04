package com.sportsmanager.core.model;
import org.junit.jupiter.api.Test;
import java.util.List;
import static com.sportsmanager.util.RandomGenerator.generateFemaleCoach;
import static com.sportsmanager.util.RandomGenerator.generateMaleCoach;
import static com.sportsmanager.util.ResourceLoader.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestPerson extends BaseTest{
    @Test
    void isFemalePersonValid() {
        AbstractPerson person = generateFemaleCoach();
        assertNotNull(person.getId());
        assertEquals(Gender.FEMALE, person.getGender());
        outOfBoundsMessage("Age", person.getAge(), 15, 50);
        List<String> names = loadLinesFromTxt("femalenames.txt");
        if (names.contains(person.getName())) {
            System.out.println("Name valid.");
        } else {
            System.out.println("Name invalid!");
        }
    }

    @Test
    void isMalePersonValid() {
        AbstractPerson person = generateMaleCoach();
        assertNotNull(person.getId());
        assertEquals(Gender.MALE, person.getGender());
        outOfBoundsMessage("Age", person.getAge(), 15, 50);
        List<String> names = loadLinesFromTxt("malenames.txt");
        if (names.contains(person.getName())) {
            System.out.println("Name valid.");
        } else {
            System.out.println("Name invalid!");
        }

    }

}
