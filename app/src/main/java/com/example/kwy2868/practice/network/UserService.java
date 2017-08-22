package com.example.kwy2868.practice.network;

import com.example.kwy2868.practice.model.BaseResult;
import com.example.kwy2868.practice.model.SignUpResult;
import com.example.kwy2868.practice.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService  {

    @POST("/signup.php")
    Call<SignUpResult> signUp(@Body User user);
}
