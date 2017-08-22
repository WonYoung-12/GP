package com.example.kwy2868.practice;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by kwy2868 on 2017-03-27.
 */
public class PApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        PApplication.context = getApplicationContext();
        KakaoSDK.init(new KakaoSDKAdapter());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/koverwatch.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static Context getAppContext() {
        return PApplication.context;
    }

    private static class KakaoSDKAdapter extends KakaoAdapter{
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return PApplication.getAppContext();
                }
            };
        }
    }
}
