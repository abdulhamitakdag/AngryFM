package com.sportsmanager.core.model;

/**
 * Takım koçlarının temel yapısını tanımlar.
 */
public abstract class AbstractCoach extends AbstractPerson {
    private CoachSpecialty specialty;
    private int coachingLevel; // 1 ile 5 arası

    public AbstractCoach(String name, int age, Gender gender, CoachSpecialty specialty, int coachingLevel) {
        super(name, age, gender);
        if (coachingLevel < 1 || coachingLevel > 5) {
            throw new IllegalArgumentException("Koçluk seviyesi 1 ile 5 arasında olmalıdır.");
        }
        this.specialty = specialty;
        this.coachingLevel = coachingLevel;
    }

    /**
     * Takıma antrenman yaptırır.
     */
    public void conductTraining(AbstractTeam team) {
        // Takımdaki her oyuncu için train() metodunu çağırır
        // Bu detay AbstractTeam ile entegre çalışacak
    }

    /**
     * Koçun uzmanlık alanına göre sağlayacağı çarpan (hook metodu).
     */
    public abstract double specialtyMultiplier();

    @Override
    public void train(double intensity) {
        // Koçların antrenman mantığı (varsa) buraya eklenebilir
    }

    @Override
    public double getTrainingEffectiveness() {
        return 1.0 + (coachingLevel * 0.1); // Örnek bir verimlilik hesabı
    }
}