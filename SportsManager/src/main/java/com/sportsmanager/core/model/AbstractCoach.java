package com.sportsmanager.core.model;

// koçların temel yapısı
public abstract class AbstractCoach extends AbstractPerson {
    private CoachSpecialty specialty;
    private int coachingLevel; // 1 ile 5 arası

    public AbstractCoach(String name, int age, Gender gender, CoachSpecialty specialty, int coachingLevel) {
        super(name, age, gender);
        if (specialty == null) {
            throw new IllegalArgumentException("Specialty cannot be null.");
        }
        if (coachingLevel < 1 || coachingLevel > 5) {
            throw new IllegalArgumentException("Coaching level must be between 1 and 5.");
        }
        this.specialty = specialty;
        this.coachingLevel = coachingLevel;
    }

    public CoachSpecialty getSpecialty() {
        return specialty;
    }

    public int getCoachingLevel() {
        return coachingLevel;
    }

    public void setSpecialty(CoachSpecialty specialty) {
        this.specialty = specialty;
    }

    public void setCoachingLevel(int coachingLevel) {
        if (coachingLevel < 1 || coachingLevel > 5) {
            throw new IllegalArgumentException("Coaching level must be between 1 and 5.");
        }
        this.coachingLevel = coachingLevel;
    }

    // takıma antrenman yaptırır
    public void conductTraining(AbstractTeam team) {
        // her oyuncu icin train() cagirilacak, AbstractTeam ile entegre calışacak
    }

    public abstract double specialtyMultiplier(); // koçun uzmanlık alanına göre çarpan

    @Override
    public void train(double intensity) {
        // koçların antrenman mantığı buraya eklenebilir
    }

    @Override
    public double getTrainingEffectiveness() {
        return 1.0 + (coachingLevel * 0.1); // basit verimlilik hesabı
    }
}