package com.example.kwy2868.practice.util;


import com.example.kwy2868.practice.model.AnalyzedWave;

/**
 * 단일 음에 대해 이론적으로 주파수는 항상 같은 크기가 들어와야함
 * 하지만 일부 노이즈가 섞여있을 수 있으므로 invalid한 값들에 대해
 * 허용 가능한 범위의 횟수까지는 정상값과 연산하여 사용가능한 값으로 보정해준다.
 */

public class FrequencySmoother {
    private static final double frequencyForgetting = 0.9; // 얼마나 빨리 주파수를 잃어버리는지
    private static final int invalidDataAllowed = 6;       // invalid한 값이 몇번까지 허용 가능한가
    static private double smoothFrequency = 0.0;           // 스무딩된 결과값 저장하는 변수
    static private int invalidDataCounter;				   // invalid한 값이 몇번이나 들어왔나 check

    public static double getSmoothFrequency(AnalyzedWave result) {
        if (!result.frequencyAvailable) {	// 입력결과가 허용가능한 주파수가 아니라면
            invalidDataCounter = Math.min(invalidDataCounter + 1, invalidDataAllowed * 2); // 몇번째인지 count
        } else {
            if (smoothFrequency == 0.0) {		// 지금 읽어들인 주파수는 사용가능한 값일 때 이전에 invalid한 값이 없었다면
                smoothFrequency = result.frequency;	// 지금 읽어들인 주파수 그대로 사용
            } else {
                smoothFrequency = (1 - frequencyForgetting) * smoothFrequency
                        + frequencyForgetting * result.frequency;	// invalid한 주파수와 지금 주파수값을 일정 비율로 섞어서 스무딩된 주파수를 만들어냄
            }
            invalidDataCounter = Math.max(invalidDataCounter - invalidDataAllowed, 0);	// 카운터가 0이하로 내려가지 않게 하기 위해서 사용
        }

        if (invalidDataCounter <= invalidDataAllowed) {
            return smoothFrequency;
        } else {
            return smoothFrequency;
        }
    }
}

