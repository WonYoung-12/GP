package com.example.kwy2868.practice.util;

public class KMEvents {
    private KMEvents() {}

    public static class PlayingStatusChanged {
        public final boolean start;

        public PlayingStatusChanged(boolean start) {
            this.start = start;
        }
    }
}
