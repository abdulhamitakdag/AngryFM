package com.sportsmanager.core.model;

/**
 * Sporcuların temel yapısını tanımlar.
 */
public abstract class AbstractPlayer extends AbstractPerson {
    private String position;
    private int shirtNumber;
    private AbstractPlayerAttributes attributes;
    private Injury injury;

    /*ben selin, burayı placeholder objeden Injury class objesine değiştiriyorum hakkınızı helal edin*/
    /*ben yine selin, oyuncuya nasıl injury veriyoruz?*/

    public AbstractPlayer(String name, int age, Gender gender, String position, int shirtNumber, AbstractPlayerAttributes attributes) {
        super(name, age, gender);
        this.position = position;
        this.shirtNumber = shirtNumber;
        this.attributes = attributes;
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

    /**
     * Sakatlık süresini bir maç azaltır.
     */
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
            // Sakat oyuncu antrenman yapamaz
            return;
        }
        if (attributes != null) {
            attributes.applyTrainingBoost(intensity * getTrainingEffectiveness());
        }
    }

    // Getter ve Setter'lar...
}