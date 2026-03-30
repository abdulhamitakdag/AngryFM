package com.sportsmanager.core.model;

public abstract class AbstractPlayerAttributes {

    public abstract double OverallRating();
    public abstract void applyTrainingBoost(double intensity);

    public double getOverallRating() {
        return OverallRating();
    }

    public double limiter(double value, double min, double max) {
        if (value < min) return min; //attributeların alabileceği max ve min değerler var.
        if (value > max) return max; // bu değerleri aşmamasını sağlıyoruz.
        return value;
    }

}