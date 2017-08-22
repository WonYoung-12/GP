package com.example.kwy2868.practice.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.kwy2868.practice.R;
import com.example.kwy2868.practice.model.SignUpResult;
import com.example.kwy2868.practice.model.User;
import com.example.kwy2868.practice.network.APIManager;
import com.example.kwy2868.practice.util.DialogUtil;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class LoginActivity extends BaseActivity {

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        findViewById(R.id.normal_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        //Log.d("hash", "key : " + getKeyHash(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestAccessTokenInfo();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }
    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("key", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    private void requestAccessTokenInfo() {
        AuthService.requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onNotSignedUp() {
                // not happened
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
                DialogUtil.showOKDialog(LoginActivity.this, "네트워크 오류입니다. 잠시 후에 시도해주세요");
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                long userId = accessTokenInfoResponse.getUserId();
                Log.d("up","this access token is for userId=" + userId);

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Log.d("up","this access token expires after " + expiresInMilis + " milliseconds.");
                requestMe(userId);
            }
        });
    }

    private void requestMe(final long userId) {
        List<String> propertyKeys = new ArrayList<>();
        propertyKeys.add("nickname");
        propertyKeys.add("thumbnail_image");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                final User user = new User();
                user.nickname = userProfile.getNickname();
                user.thumbnailImagePath = userProfile.getThumbnailImagePath();
                user.userId = userId;

                APIManager.getUserService().signUp(user).enqueue(new Callback<SignUpResult>() {
                    @Override
                    public void onResponse(Call<SignUpResult> call, Response<SignUpResult> response) {
                        if (response.isSuccessful()) {
                            if (response.body().code == 200) {
                                user.idx = response.body().idx;
                                redirectMain(user);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpResult> call, Throwable t) {
                        DialogUtil.showOKDialog(LoginActivity.this, "네트워크 오류입니다. 잠시 후에 시도해주세요");
                    }
                });
            }

            @Override
            public void onNotSignedUp() {

            }
        }, propertyKeys, false);
    }

    protected void redirectMain(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        this.finish();
    }
}
