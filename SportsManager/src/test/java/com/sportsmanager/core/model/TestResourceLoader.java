package com.sportsmanager.core.model;

import com.sportsmanager.core.model.BaseTest;
import com.sportsmanager.util.ResourceLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestResourceLoader extends BaseTest {

    @Test
    void loadLinesThrowsMeaningfulExceptionForMissingFile() {
        assertThrows(RuntimeException.class, () -> ResourceLoader.loadLinesFromTxt("no-such-file.txt"));
    }

    @Test
    void loadLinesReturnsNonEmptyListForExistingMaleNamesFile() {
        assertFalse(ResourceLoader.loadLinesFromTxt("malenames.txt").isEmpty());
    }

    @Test
    void loadLinesReturnsNonEmptyListForExistingFemaleNamesFile() {
        assertFalse(ResourceLoader.loadLinesFromTxt("femalenames.txt").isEmpty());
    }

    @Test
    void loadLinesReturnsNonEmptyListForExistingSurnamesFile() {
        assertFalse(ResourceLoader.loadLinesFromTxt("surnames.txt").isEmpty());
    }
}