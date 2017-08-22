package com.example.kwy2868.practice.model;


public class AnalyzedWave {
    public double frequency;
    public final boolean frequencyAvailable;
    public final Error error;

    public AnalyzedWave(Error error) {
        this.frequencyAvailable = false;
        this.error = error;
    }

    public AnalyzedWave(double frequency) {
        this.frequencyAvailable = true;
        this.frequency = frequency;
        this.error = Error.NO_PROBLEMS;
    }
}
