package com.example.kwy2868.practice.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwy2868.practice.R;
import com.example.kwy2868.practice.model.BaseResult;
import com.example.kwy2868.practice.model.Emotion;
import com.example.kwy2868.practice.model.GetMusicResponse;
import com.example.kwy2868.practice.model.ListenMusic;
import com.example.kwy2868.practice.network.APIManager;
import com.example.kwy2868.practice.util.DialogUtil;
import com.example.kwy2868.practice.view.MusicAdapter;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.kwy2868.practice.model.Emotion.EMOTION_ANGER;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_ANGER_HA_HM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_ANGER_HA_LM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_ANGER_LA_HM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_ANGER_LA_LM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_CONCENTRATE;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_CONCENTRATE_HA_HM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_CONCENTRATE_HA_LM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_CONCENTRATE_LA_HM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_CONCENTRATE_LA_LM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_HAPPY;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_HAPPY_HA_HM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_HAPPY_HA_LM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_HAPPY_LA_HM;
import static com.example.kwy2868.practice.model.Emotion.EMOTION_HAPPY_LA_LM;


public class MusicListActivity extends BaseActivity {

    private ListView listView;
    private int finalEmotion;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        listView = (ListView) findViewById(R.id.listview);

        init();
    }

    private void init() {
        double stddevMed = getIntent().getDoubleExtra(EEGActivity.KEY_STDDEV_MED, 0);
        double stddevAtt = getIntent().getDoubleExtra(EEGActivity.KEY_STDDEV_ATT, 0);
        double avgMed = getIntent().getDoubleExtra(EEGActivity.KEY_AVG_MED, 0);
        double avgAtt = getIntent().getDoubleExtra(EEGActivity.KEY_AVG_ATT, 0);
        //TODO: emotion test중 나중에 바꾸기
        int emotion =  getIntent().getIntExtra(CheckEmotionActivity.KEY_EMOTION, 0);
        finalEmotion = getEmotion(emotion, avgAtt, avgMed);
        getMusicList(getEmotion(emotion, avgAtt, avgMed));
        setEmotionTitle(getEmotion(emotion, avgAtt, avgMed));
        Log.d("avg_med", "avg_med : " + avgMed);
        Log.d("avg_att", "avg_att : " + avgAtt);
    }

    private void setEmotionTitle(int emotion) {
        String textEmotion = null;
        switch (emotion) {
            case EMOTION_HAPPY_HA_HM:
                textEmotion = "HAPPY, High ATT. High MED.";
                break;
            case EMOTION_HAPPY_LA_LM:
                textEmotion = "HAPPY, Low ATT. Low MED.";
                break;
            case EMOTION_HAPPY_LA_HM:
                textEmotion = "HAPPY, Low ATT. High MED.";
                break;
            case EMOTION_HAPPY_HA_LM:
                textEmotion = "HAPPY, High ATT. Low MED.";
                break;
            case EMOTION_ANGER_HA_HM:
                textEmotion = "STRESS, High Att. High MED.";
                break;
            case EMOTION_ANGER_LA_LM:
                textEmotion = "STRESS, Low Att. Low MED.";
                break;
            case EMOTION_ANGER_LA_HM:
                textEmotion = "STRESS, Low Att. High MED.";
                break;
            case EMOTION_ANGER_HA_LM:
                textEmotion = "STRESS, High Att. Low MED.";
                break;
            case EMOTION_CONCENTRATE_HA_HM:
                textEmotion = "RELAXED, High Att. High MED.";
                break;
            case EMOTION_CONCENTRATE_LA_LM:
                textEmotion = "RELAXED, Low Att. Low MED.";
                break;
            case EMOTION_CONCENTRATE_LA_HM:
                textEmotion = "RELAXED, Low Att. High MED.";
                break;
            case EMOTION_CONCENTRATE_HA_LM:
                textEmotion = "RELAXED, High Att. Low MED.";
                break;
        }

        ((TextView) findViewById(R.id.music_title_text)).setText(textEmotion);
    }

    private void getMusicList(int emotion) {
        APIManager.getMusicService().getMusicList(new Emotion(emotion)).enqueue(new Callback<GetMusicResponse>() {
            @Override
            public void onResponse(Call<GetMusicResponse> call, Response<GetMusicResponse> response) {
                setList(response.body());
            }

            @Override
            public void onFailure(Call<GetMusicResponse> call, Throwable t) {
                DialogUtil.showOKDialog(MusicListActivity.this, "네트워크 오류입니다. 잠시 후에 시도해주세요");
            }
        });
    }

    private void setList(final GetMusicResponse musicResponse) {
        MusicAdapter adapter = new MusicAdapter(this, R.layout.view_music, musicResponse.musicList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(musicResponse.musicList.get(position).url)));
                APIManager.getMusicService().listenMusic(new ListenMusic(MainActivity.user.idx, musicResponse.musicList.get(position).id, finalEmotion))
                        .enqueue(new Callback<BaseResult>() {
                    @Override
                    public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                        Toast.makeText(MusicListActivity.this, "listen", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<BaseResult> call, Throwable t) {
                        DialogUtil.showOKDialog(MusicListActivity.this, "네트워크 오류입니다. 잠시 후에 시도해주세요");
                    }
                });
            }
        });
    }

    public static int getEmotion(int emotion, double att, double med) {
        switch (emotion) {
            case EMOTION_HAPPY:
                if (att > 30) {
                    if (med > 50) {
                        return EMOTION_HAPPY_HA_HM;
                    } else {
                        return EMOTION_HAPPY_HA_LM;
                    }
                } else {
                    if (med > 50) {
                        return EMOTION_HAPPY_LA_HM;
                    } else {
                        return EMOTION_HAPPY_LA_LM;
                    }
                }
            case EMOTION_ANGER:
                if (att > 30) {
                    if (med > 50) {
                        return EMOTION_ANGER_HA_HM;
                    } else {
                        return EMOTION_ANGER_HA_LM;
                    }
                } else {
                    if (med > 50) {
                        return EMOTION_ANGER_LA_HM;
                    } else {
                        return EMOTION_ANGER_LA_LM;
                    }
                }
            case EMOTION_CONCENTRATE:
                if (att > 30) {
                    if (med > 50) {
                        return EMOTION_CONCENTRATE_HA_HM;
                    } else {
                        return EMOTION_CONCENTRATE_HA_LM;
                    }
                } else {
                    if (med > 50) {
                        return EMOTION_CONCENTRATE_LA_HM;
                    } else {
                        return EMOTION_CONCENTRATE_LA_LM;
                    }
                }
        }
        return 0;
    }
}
