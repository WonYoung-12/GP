package com.example.kwy2868.practice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.kwy2868.practice.R;
import com.example.kwy2868.practice.model.Emotion;


public class CheckEmotionActivity extends BaseActivity implements View.OnClickListener {
    public static final String KEY_EMOTION = "key_emotion";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_emotion);
        setView();
    }

    private void setView() {
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.emotion_bad_button).setOnClickListener(this);
        findViewById(R.id.emotion_normal_button).setOnClickListener(this);
        findViewById(R.id.emotion_good_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, EEGActivity.class);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.emotion_bad_button:
                intent.putExtra(KEY_EMOTION, Emotion.EMOTION_ANGER);
                startActivity(intent);
                break;
            case R.id.emotion_normal_button:
                intent.putExtra(KEY_EMOTION, Emotion.EMOTION_CONCENTRATE);
                startActivity(intent);
                break;
            case R.id.emotion_good_button:
                intent.putExtra(KEY_EMOTION, Emotion.EMOTION_HAPPY);
                startActivity(intent);
                break;
        }
    }
}
