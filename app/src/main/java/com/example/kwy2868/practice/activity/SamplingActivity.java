package com.example.kwy2868.practice.activity;

import android.graphics.Color;
import android.widget.TextView;



public abstract class SamplingActivity extends BaseActivity {
    public abstract void generateNextPractice();

    // 소리내고 있는 음에 대한 피드백 메세지를 보여준다.
    public void displayMessage(String message, boolean isPositive) {
        getMessageView().setText(message);
        getMessageView().setTextColor(isPositive ? Color.rgb(34, 139, 34) : Color.rgb(255, 36, 0));
    }

    protected abstract TextView getMessageView();
}
