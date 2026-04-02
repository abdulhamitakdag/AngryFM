package com.sportsmanager.core.model;

import java.util.List;

// takımın maç stratejisi - hücum/savunma ağırlığı ve pressing yoğunluğu burada tutuluyor
// üç değer de 0.0-1.0 arasına clamp'leniyor, maç motoru modifier'ları buradan okuyor
public abstract class AbstractTactic {

    private String name;
    private double attackingWeight;
    private double defensiveWeight;   // her zaman 1.0 - attackingWeight
    private double pressureIntensity;

    public AbstractTactic(String name, double attackingWeight, double pressureIntensity) {
        this.name              = name;
        this.attackingWeight   = clamp(attackingWeight);
        this.defensiveWeight   = clamp(1.0 - attackingWeight);
        this.pressureIntensity = clamp(pressureIntensity);
    }

    public abstract String getFormationString(); // "4-3-3", "5-1" gibi formasyon yazısı

    // kadro bu taktiğe uygun mu kontrol eder, eksik pozisyon varsa hata fırlatır
    public abstract void validateForSquad(List<AbstractPlayer> squad);

    // maç motoru modifier'ları - hücum/savunma/pressing çarpanları

    public double getOffensiveModifier() { // 0.80 - 1.20 arası
        return 0.80 + attackingWeight * 0.40;
    }

    public double getDefensiveModifier() { // 0.80 - 1.20 arası
        return 0.80 + defensiveWeight * 0.40;
    }

    // yüksek press = daha çok top kapma ama kontra riski artar
    public double getPressureModifier() { // 0.90 - 1.10 arası
        return 0.90 + pressureIntensity * 0.20;
    }

    // getter'lar

    public String getName()              { return name; }
    public double getAttackingWeight()   { return attackingWeight; }
    public double getDefensiveWeight()   { return defensiveWeight; }
    public double getPressureIntensity() { return pressureIntensity; }

    // setter'lar

    public void setName(String name) {
        this.name = name;
    }

    // defensiveWeight otomatik hesaplanır (1.0 - attackingWeight)
    public void setAttackingWeight(double attackingWeight) {
        this.attackingWeight = clamp(attackingWeight);
        this.defensiveWeight = clamp(1.0 - attackingWeight);
    }

    public void setPressureIntensity(double pressureIntensity) {
        this.pressureIntensity = clamp(pressureIntensity);
    }

    // değeri 0.0 - 1.0 arasına sınırlar
    protected static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (hücum=%.2f, savunma=%.2f, press=%.2f)",
                name, getFormationString(), attackingWeight, defensiveWeight, pressureIntensity);
    }
}
