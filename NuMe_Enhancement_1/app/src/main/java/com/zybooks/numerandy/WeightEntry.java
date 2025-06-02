package com.zybooks.numerandy;

public class WeightEntry {

    private String mDate;
    private double mWeight;

    // Constructor
    public WeightEntry(String date, double weight) {
        this.mDate = date;
        this.mWeight = weight;
    }

    // Getters
    public  String getDate() {
        return mDate;
    }

    public double getWeight() {
        return mWeight;
    }
}
