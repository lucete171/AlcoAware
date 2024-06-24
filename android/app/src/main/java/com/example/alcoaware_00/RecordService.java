package com.example.alcoaware_00;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordService extends Service {

    private SensorManager sensorManager;
    private Sensor gyroSensor, accelerometerSensor;
    private SensorEventListener sensorListener;
    private ApiService apiService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-api-endpoint.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    Log.d("자이로스코프 데이터", "X: " + x + ", Y: " + y + ", Z: " + z);

                    // 서버로 데이터 전송
                    sendDataToServer(x, y, z);
                } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    Log.d("가속도계 데이터", "X: " + x + ", Y: " + y + ", Z: " + z);

                    // 서버로 데이터 전송
                    sendDataToServer(x, y, z);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // 정확도 변경 처리 (필요 시)
            }
        };

        sensorManager.registerListener(sensorListener, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;  // 서비스가 종료되어도 자동으로 재시작
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendDataToServer(float x, float y, float z) {
        // 서버로 데이터 전송
        Call<Void> call = apiService.sendSensorData(x, y, z);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("데이터 전송", "데이터 전송 성공");
                } else {
                    Log.e("데이터 전송", "데이터 전송 실패. 오류: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("데이터 전송", "데이터 전송 실패. 오류: " + t.getMessage());
            }
        });
    }
}
