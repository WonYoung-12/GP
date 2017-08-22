package com.example.kwy2868.practice.model;


public enum MessageClass {
    TUNING_IN_PROGRESS, // 소리 분석중
    WEIRD_FREQUENCY,    // 분석 불가능한 소리
    TOO_QUIET,          // 소리가 너무작음
    TOO_NOISY           // 소음이 너무 심함
}
