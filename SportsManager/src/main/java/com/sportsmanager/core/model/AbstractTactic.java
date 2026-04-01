package com.sportsmanager.core.model;

import java.util.List;

/**
 * Takımın maç stratejisini tanımlar.
 *
 * Üç evrensel eksen tüm sporlar için geçerlidir:
 *   - attackingWeight  : hücum ağırlığı        (0.0 = tam savunma, 1.0 = tam hücum)
 *   - defensiveWeight  : savunma ağırlığı       (her zaman 1.0 - attackingWeight)
 *   - pressureIntensity: pressing yoğunluğu     (0.0 = pasif, 1.0 = maksimum press)
 *
 * Üç değer de [0.0, 1.0] aralığına clamp'lenir.
 * Maç motoru (IMatchEngine) modifier metodlarını okuyarak simülasyonu ölçekler.
 */
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

    // ── Abstract hook'lar ─────────────────────────────────────────────────────

    /**
     * UI'da ve maç raporlarında gösterilen formasyon yazısı.
     * Futbol için "4-3-3", voleybol için "5-1" gibi.
     */
    public abstract String getFormationString();

    /**
     * Verilen kadronun bu taktiği uygulayıp uygulayamayacağını doğrular.
     * Eksik pozisyon varsa IllegalArgumentException fırlatır.
     */
    public abstract void validateForSquad(List<AbstractPlayer> squad);

    // ── Maç motoru modifier'ları ──────────────────────────────────────────────

    /**
     * Hücum çıktısına uygulanan çarpan.
     * Formül: 0.80 + attackingWeight * 0.40  →  aralık: [0.80, 1.20]
     * Subclass'lar spor-spesifik mantık için override edebilir.
     */
    public double getOffensiveModifier() {
        return 0.80 + attackingWeight * 0.40;
    }

    /**
     * Savunma direncine uygulanan çarpan.
     * Formül: 0.80 + defensiveWeight * 0.40  →  aralık: [0.80, 1.20]
     */
    public double getDefensiveModifier() {
        return 0.80 + defensiveWeight * 0.40;
    }

    /**
     * Pressing yoğunluğu çarpanı.
     * Formül: 0.90 + pressureIntensity * 0.20  →  aralık: [0.90, 1.10]
     * Yüksek press daha fazla top kapma şansı yaratır ama kontra riskini artırır.
     */
    public double getPressureModifier() {
        return 0.90 + pressureIntensity * 0.20;
    }

    // ── Getter'lar ────────────────────────────────────────────────────────────

    public String getName()              { return name; }
    public double getAttackingWeight()   { return attackingWeight; }
    public double getDefensiveWeight()   { return defensiveWeight; }
    public double getPressureIntensity() { return pressureIntensity; }

    // ── Setter'lar ────────────────────────────────────────────────────────────

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Hücum ağırlığını günceller.
     * defensiveWeight otomatik olarak 1.0 - attackingWeight olarak hesaplanır.
     */
    public void setAttackingWeight(double attackingWeight) {
        this.attackingWeight = clamp(attackingWeight);
        this.defensiveWeight = clamp(1.0 - attackingWeight);
    }

    public void setPressureIntensity(double pressureIntensity) {
        this.pressureIntensity = clamp(pressureIntensity);
    }

    // ── Yardımcı ─────────────────────────────────────────────────────────────

    /**
     * Değeri [0.0, 1.0] aralığına sınırlar.
     * Subclass'ların kendi ekstra numeric alanları için de kullanabilir.
     */
    protected static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (hücum=%.2f, savunma=%.2f, press=%.2f)",
                name, getFormationString(), attackingWeight, defensiveWeight, pressureIntensity);
    }
}
