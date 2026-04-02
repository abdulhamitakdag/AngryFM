package com.sportsmanager.core.interfaces;

/**
 * Antrenman yapılabilen (gelişime açık) varlıkların yeteneklerini tanımlar.
 */
public interface ITrainable {
    /**
     * Varlığa antrenman uygular.
     * @param intensity Antrenman yoğunluğu (0.0 ile 1.0 arası)
     */
    void train(double intensity);

    /**
     * @return Antrenman verimliliği katsayısı
     */
    double getTrainingEffectiveness();
}
