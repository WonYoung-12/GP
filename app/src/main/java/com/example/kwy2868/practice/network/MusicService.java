package com.example.kwy2868.practice.network;

import com.example.kwy2868.practice.model.BaseResult;
import com.example.kwy2868.practice.model.Emotion;
import com.example.kwy2868.practice.model.GetMusicResponse;
import com.example.kwy2868.practice.model.ListenMusic;
import com.example.kwy2868.practice.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface MusicService {
    @POST("/getMusicList.php")
    Call<GetMusicResponse> getMusicList(@Body Emotion emotion);

    @POST("/listen_music.php")
    Call<BaseResult> listenMusic(@Body ListenMusic listenMusic);

    @POST("/getPreferMusicList.php")
    Call<GetMusicResponse> getPreferMusicList(@Body User user);


}
