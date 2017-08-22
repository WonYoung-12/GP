package com.example.kwy2868.practice.util;

import android.util.Log;

import com.example.kwy2868.practice.activity.EEGActivity;
import com.example.kwy2868.practice.activity.SamplingActivity;
import com.example.kwy2868.practice.model.AnalyzedWave;
import com.example.kwy2868.practice.model.Error;
import com.example.kwy2868.practice.model.MessageClass;

import java.util.Observable;
import java.util.Observer;

public class SinglePracticeUIObserver implements Observer {
    private static final double SUCCEED_MATCHING_PERCENT = 75.0;    // System의 음과 몇 % 일치해야 성공할것인가

	private final SamplingActivity activity;

	private double frequency;
	private final NormalizedSounds normalizedSounds = new NormalizedSounds();
	
	private MessageClass message;
	private MessageClass previouslyProposedMessage;
	private MessageClass proposedMessage;   // needs to get X consecutive votes.
	private int numberOfVotes;
    private final int minNumberOfVotes = 3; // X.

    public SinglePracticeUIObserver(SamplingActivity activity) {
        this.activity = activity;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (!(observable instanceof SoundAnalyzer) || !(data instanceof AnalyzedWave)) {
            return;
        }

        AnalyzedWave analyzedWave = (AnalyzedWave) data;
        frequency = FrequencySmoother.getSmoothFrequency(analyzedWave);
        if (analyzedWave.error == Error.BIG_FREQUENCY
                || analyzedWave.error == Error.BIG_VARIANCE
                || analyzedWave.error == Error.ZERO_SAMPLES) {
            proposedMessage = MessageClass.TOO_NOISY;
        } else if (analyzedWave.error == Error.TOO_QUIET) {
            proposedMessage = MessageClass.TOO_QUIET;
        } else if (analyzedWave.error == Error.NO_PROBLEMS) {
            proposedMessage = MessageClass.TUNING_IN_PROGRESS;
        } else {
            Log.e("PracticeUIHelper", "알 수 없는 메세지 클래스입니다.");
            proposedMessage = null;
        }

        // 사용자의 입력이 시스템의 Sound와 75% 이상 일치하면 다음문제 생성
        if (updateUI() >= SUCCEED_MATCHING_PERCENT) {
            activity.generateNextPractice();
        }
    }

    public double updateUI() {
        double match;

        NormalizedSounds.Sound sound = normalizedSounds.getSound(frequency);
        if (sound == null) {
            match = 0;
        } else {
            if (frequency < sound.frequency) {
                match = (frequency - sound.minFrequency) / (sound.frequency - sound.minFrequency);
            } else {
                match = (sound.maxFrequency - frequency) / (sound.maxFrequency - sound.frequency);
            }
            match *= 1.2;
        }

        if (proposedMessage == MessageClass.TUNING_IN_PROGRESS && sound == null) {
            proposedMessage = MessageClass.WEIRD_FREQUENCY;
        }

        if (message == null) {
            message = previouslyProposedMessage = proposedMessage;
        } else if (message != proposedMessage) {
            if (previouslyProposedMessage != proposedMessage) {
                previouslyProposedMessage = proposedMessage;
                numberOfVotes = 1;
            } else {
                numberOfVotes++;
            }

            if (numberOfVotes >= minNumberOfVotes) {
                message = proposedMessage;
            }
        }

        if (message != null) {
            switch (message) {
                case TUNING_IN_PROGRESS:
                    if (sound == null) {
//                        activity.displayMessage("분석 할 수 없는 파형입니다.", false);
                    } else {
                        activity.displayMessage("현재 입력된 파형 : " + sound.name
                                + " : " + Math.min(Math.round(100.0 * match), 100) + " / 100", true);
                    }
                    break;
                case TOO_NOISY:
                    activity.displayMessage("노이즈가 심합니다.", false);
                    break;
                case TOO_QUIET:
                    activity.displayMessage("전극을 다시 접촉해주세요.", false);
                    break;
                case WEIRD_FREQUENCY:
                    activity.displayMessage("분석할 수 없는 파형입니다.", false);
                default:
                    Log.d("PracticeUIHelper", "메세지가 없습니다.");
            }
        }

        if (sound != null) {
            int currentFrequencyIndex = ((EEGActivity) activity).currentFrequencyIndex;
            if (NormalizedSounds.SOUND_NAME[currentFrequencyIndex].equals(sound.name)) {
                return Math.round(match * 100.0);
            }
        }

        return 0;
    }
}
