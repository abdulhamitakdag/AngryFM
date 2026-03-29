package com.sportsmanager.core.model;

/**
 * Sporcuların temel yapısını tanımlar.
 */
public abstract class AbstractPlayer extends AbstractPerson {
    private String position;
    private int shirtNumber;
    private AbstractPlayerAttributes attributes;
    private Object injury; // B kişisinin yazacağı Injury sınıfı gelecek

    public AbstractPlayer(String name, int age, Gender gender, String position, int shirtNumber, AbstractPlayerAttributes attributes) {
        super(name, age, gender);
        this.position = position;
        this.shirtNumber = shirtNumber;
        this.attributes = attributes;
        this.injury = null; // Başlangıçta sakatlık yok
    }

    /**
     * Sakatlık süresini bir maç azaltır.
     */
    public void decrementInjury() {
        if (injury != null) {
            // injury.decrementGamesRemaining() çağrılacak (B kişisinin sınıfı gelince)
        }
    }

    @Override
    public void train(double intensity) {
        if (injury != null) {
            // Sakat oyuncu antrenman yapamaz
            return;
        }
        if (attributes != null) {
            attributes.applyTrainingBoost(intensity * getTrainingEffectiveness());
        }
    }

    // Getter ve Setter'lar...
}