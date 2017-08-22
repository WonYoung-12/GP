package com.example.kwy2868.practice.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.kwy2868.practice.R;
import com.example.kwy2868.practice.model.Emotion;


public class CheckEmotionForPreferActivity extends BaseActivity implements View.OnClickListener {
    public static final String KEY_EMOTION = "key_emotion";

    private TextView highAtt;
    private TextView highMed;
    private TextView lowAtt;
    private TextView lowMed;

    private boolean isHighAtt = true;
    private boolean isHighMed = true;

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

        highAtt = (TextView) findViewById(R.id.high_att_text);
        highMed = (TextView) findViewById(R.id.high_med_text);
        lowAtt = (TextView) findViewById(R.id.low_att_text);
        lowMed = (TextView) findViewById(R.id.low_med_text);

        highAtt.setOnClickListener(this);
        highMed.setOnClickListener(this);
        lowAtt.setOnClickListener(this);
        lowMed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PreferMusicListActivity.class);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.emotion_bad_button:
                if (isHighAtt) {
                    if (isHighMed) {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_ANGER_HA_HM);
                    } else {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_ANGER_HA_LM);
                    }
                } else {
                    if (isHighMed) {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_ANGER_LA_HM);
                    } else {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_ANGER_LA_LM);
                    }
                }

                startActivity(intent);
                finish();
                break;
            case R.id.emotion_normal_button:
                if (isHighAtt) {
                    if (isHighMed) {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_CONCENTRATE_HA_HM);
                    } else {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_CONCENTRATE_HA_LM);
                    }
                } else {
                    if (isHighMed) {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_CONCENTRATE_LA_HM);
                    } else {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_CONCENTRATE_LA_LM);
                    }
                }
                startActivity(intent);
                finish();
                break;
            case R.id.emotion_good_button:
                if (isHighAtt) {
                    if (isHighMed) {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_HAPPY_HA_HM);
                    } else {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_HAPPY_HA_LM);
                    }
                } else {
                    if (isHighMed) {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_HAPPY_LA_HM);
                    } else {
                        intent.putExtra(KEY_EMOTION, Emotion.EMOTION_HAPPY_LA_LM);
                    }
                }
                startActivity(intent);
                finish();
                break;

            case R.id.high_att_text:
                isHighAtt = true;
                highAtt.setTextColor(Color.WHITE);
                highAtt.setBackgroundColor(getResources().getColor(R.color.main_4));
                lowAtt.setTextColor(getResources().getColor(R.color.main_4));
                lowAtt.setBackgroundColor(Color.WHITE);

                break;
            case R.id.low_att_text:
                isHighAtt = false;
                highAtt.setTextColor(getResources().getColor(R.color.main_4));
                highAtt.setBackgroundColor(Color.WHITE);
                lowAtt.setTextColor(Color.WHITE);
                lowAtt.setBackgroundColor(getResources().getColor(R.color.main_4));
                break;
            case R.id.high_med_text:
                isHighMed = true;
                highMed.setTextColor(Color.WHITE);
                highMed.setBackgroundColor(getResources().getColor(R.color.main_4));
                lowMed.setTextColor(getResources().getColor(R.color.main_4));
                lowMed.setBackgroundColor(Color.WHITE);

                break;
            case R.id.low_med_text:
                isHighMed = false;
                highMed.setTextColor(getResources().getColor(R.color.main_4));
                highMed.setBackgroundColor(Color.WHITE);
                lowMed.setTextColor(Color.WHITE);
                lowMed.setBackgroundColor(getResources().getColor(R.color.main_4));
                break;
        }
    }
}
