package com.example.alcoaware_00;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 카카오 SDK 초기화
        KakaoSdk.init(this, "361278f601917dbef8832dc866d5ccf4");
    }
}
