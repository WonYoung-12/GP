package com.example.kwy2868.practice.model;


public class Emotion {

    public static final int EMOTION_HAPPY = 123;
    public static final int EMOTION_ANGER = 234;
    public static final int EMOTION_CONCENTRATE = 345;


    //high-high
    public static final int EMOTION_HAPPY_HA_HM = 1;
    public static final int EMOTION_ANGER_HA_HM = 2;
    public static final int EMOTION_CONCENTRATE_HA_HM  = 3;
    //low-low
    public static final int EMOTION_HAPPY_LA_LM = 4;
    public static final int EMOTION_ANGER_LA_LM = 5;
    public static final int EMOTION_CONCENTRATE_LA_LM  = 6;
    //high-low
    public static final int EMOTION_HAPPY_HA_LM = 7;
    public static final int EMOTION_ANGER_HA_LM = 8;
    public static final int EMOTION_CONCENTRATE_HA_LM  = 9;
    //low-high
    public static final int EMOTION_HAPPY_LA_HM = 10;
    public static final int EMOTION_ANGER_LA_HM = 11;
    public static final int EMOTION_CONCENTRATE_LA_HM  = 12;

    int emotion;

    public Emotion(int emotion) {
        this.emotion = emotion;
    }
}
