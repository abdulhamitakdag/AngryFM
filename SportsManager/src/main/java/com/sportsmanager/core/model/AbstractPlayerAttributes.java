package com.sportsmanager.core.model;

public abstract class AbstractPlayerAttributes {

    public abstract int computeOverallRating();
    public abstract void applyTrainingBoost(double intensity);

    public int getOverallRating() {
        return computeOverallRating();
    }

    public double limiter(double value, double min, double max) {
        if (value < min) return min; //attributeların alabileceği max ve min değerler var.
        if (value > max) return max; // bu değerleri aşmamasını sağlıyoruz.
        return value;
    }

}