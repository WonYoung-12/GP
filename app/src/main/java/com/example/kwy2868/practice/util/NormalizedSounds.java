package com.example.kwy2868.practice.util;

/**
 * 각 음들의 최소 주파수와 최대 주파수를 설정해줌으로써 음 객체를 정규화시킨다.
 */
public class NormalizedSounds {
    private static final double[] SOUND_FREQUENCY
            = new double[] {4, 7, 12, 30, 100};
    public static final String[] SOUND_NAME
            = new String[] {"Delta", "Theta", "Alpha", "Beta", "Gamma"};

    public class Sound {
        public final double frequency;
		public final double minFrequency;
		public final double maxFrequency;
		public final String name;

		public Sound(double frequency, double minFrequency, double maxFrequency, String name) {
            this.frequency = frequency;
            this.minFrequency = minFrequency;
            this.maxFrequency = maxFrequency;
            this.name = name;
		}
	}

    private final Sound[] sounds;

    public NormalizedSounds() {
        sounds = new Sound[SOUND_FREQUENCY.length];
        // 각 음 별로 최소, 최대 주파수 설정
        for (int i = 0; i < SOUND_FREQUENCY.length; i++) {
            double frequency = SOUND_FREQUENCY[i];

            double minFrequency = (i == 0)
                    ? 0.75 * (frequency * 2 - (frequency + SOUND_FREQUENCY[i + 1]) / 2)
                    : (frequency + SOUND_FREQUENCY[i - 1]) / 2;
            double maxFrequency = (i == SOUND_FREQUENCY.length - 1)
                    ? 1.5 * (frequency * 2 - (frequency + SOUND_FREQUENCY[i - 1]) / 2)
                    : (frequency + SOUND_FREQUENCY[i + 1]) / 2;

            sounds[i] = new Sound(frequency, minFrequency, maxFrequency, SOUND_NAME[i]);
        }
    }

    public Sound getSound(double frequency) {
        for (Sound sound : sounds) {
            if (sound.minFrequency <= frequency && frequency <= sound.maxFrequency) {
                return sound;
            }
        }
        return null;
    }
}
