package com.sportsmanager.core.model;
import java.util.*;

public class Injury {

    public enum Severity { MINOR, MODERATE, SERIOUS }
    private final UUID id;
    private final Severity severity;
    private int gamesRemaining;

/*Hi, selin here, should we name injuries? We only have severity for now*/
    public Injury(Severity severity, int gamesRemaining) {
        if (severity == null) {
            throw new IllegalArgumentException("Severity cannot be null!");
        }
        if (gamesRemaining < 1) {
            throw new IllegalArgumentException("gamesRemaining must be at least 1!");
        }
        this.id = UUID.randomUUID();
        this.severity = severity;
        this.gamesRemaining = gamesRemaining;
    }

    public UUID getId() {
        return id;
    }
    public Severity getSeverity() {
        return severity;
    }
    public int getGamesRemaining() {
        return gamesRemaining;
    }


    public void decrementGamesRemaining() {
        if (this.gamesRemaining > 0) {
            this.gamesRemaining--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Injury injury = (Injury) o;
        return id.equals(injury.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
