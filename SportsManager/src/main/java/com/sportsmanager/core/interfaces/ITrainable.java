package com.sportsmanager.core.interfaces;

// antrenman yapılabilen varlıklar için arayüz
public interface ITrainable {
    void train(double intensity); // intensity 0.0 ile 1.0 arası
    double getTrainingEffectiveness(); // antrenman verimlilik katsayısı
}
