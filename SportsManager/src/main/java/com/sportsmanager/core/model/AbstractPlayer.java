package com.sportsmanager.core.model;

// sporcuların temel yapısı
public abstract class AbstractPlayer extends AbstractPerson {
    private String position;
    private int shirtNumber;
    private AbstractPlayerAttributes attributes;
    private Injury injury;

    public AbstractPlayer(String name, int age, Gender gender, String position, int shirtNumber, AbstractPlayerAttributes attributes) {
        super(name, age, gender);
        this.position = position;
        /* this.position = Objects.requireNonNull(position);
        abstraction için ekleyebiliriz*/
        this.shirtNumber = shirtNumber;
        this.attributes = attributes;
        /*this.attributes = Objects.requireNonNull(attributes);
        abstraction için ekleyebiliriz*/
        this.injury = null; // Başlangıçta sakatlık yok
    }

    public String getPosition() {
        return position;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public AbstractPlayerAttributes getAttributes() {
        return attributes;
    }

    public void setInjury(Injury newInjury) {
        this.injury = newInjury;
    }
    public Injury getInjury() {
        return injury;
    }

    public boolean isInjured() {
        return this.injury != null && this.injury.getGamesRemaining() > 0;
    }

    // sakatlık süresini bir maç azaltır
    public void decrementInjury() {
        if (injury != null) {
            injury.decrementGamesRemaining();
            if (injury.getGamesRemaining() == 0) {
                injury= null;
            }
        }
    }

    @Override
    public void train(double intensity) {
        if (injury != null) {
            // sakat oyuncu antrenman yapamaz
            return;
        }
        if (attributes != null) {
            attributes.applyTrainingBoost(intensity * getTrainingEffectiveness());
        }
    }

    // Getter ve Setter'lar...
}