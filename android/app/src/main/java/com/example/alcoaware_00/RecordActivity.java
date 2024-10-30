package com.example.alcoaware_00;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class RecordActivity extends AppCompatActivity implements SensorEventListener {

    // UI 요소 선언
    private ToggleButton toggleButton; // 음주 상태 토글 버튼
    private TextView drinkingText; // 음주 상태 텍스트 뷰
    private Button recordButton; // 녹화 버튼
    private TextView statusText; // 녹화 상태 텍스트 뷰
    private Button mainButton; // 메인 페이지로 이동하는 버튼
    private Button detectButton; // 음주 여부를 확인하는 버튼

    // 녹화 상태 변수
    private boolean isRecording = false; // 녹화 상태를 나타내는 변수

    // Firebase 관련 변수
    private FirebaseAuth mAuth; // Firebase 인증 객체
    private FirebaseFirestore db; // Firestore 객체
    private FirebaseUser currentUser; // 현재 사용자 FirebaseUser 객체
    private String uid; // 현재 사용자 UID

    // 센서 관련 변수
    private SensorManager sensorManager; // 센서 매니저 객체
    private Sensor accelerometer; // 가속도 센서
    private Sensor gyroscope; // 자이로스코프 센서
    private float accX, accY, accZ; // 가속도 센서 값
    private float gyrX, gyrY, gyrZ; // 자이로스코프 센서 값

    // 위치 관련 변수
    private FusedLocationProviderClient fusedLocationClient; // 위치 제공자 클라이언트
    private Location currentLocation; // 현재 위치 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record); // 레이아웃 설정

        // Firebase 초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
        }

        // 센서 초기화
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // 위치 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // UI 요소 초기화
        toggleButton = findViewById(R.id.toggle_button);
        drinkingText = findViewById(R.id.drinking_text);
        recordButton = findViewById(R.id.record_button);
        statusText = findViewById(R.id.status_text);
        mainButton = findViewById(R.id.main_page_button);

        // 토글 버튼 리스너 설정
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                drinkingText.setText(R.string.drinking_on);
                toggleButton.setBackgroundResource(R.drawable.rounded_button);
            } else {
                drinkingText.setText(R.string.drinking_off);
                toggleButton.setBackgroundResource(R.drawable.rounded_button_gray);
            }
        });

        // 녹화 버튼 클릭 리스너 설정
        recordButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        // 메인 페이지 버튼 클릭 리스너 설정
        mainButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // WorkManager 설정
        PeriodicWorkRequest recordWorkRequest = new PeriodicWorkRequest.Builder(RecordWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder()
                        .setRequiresBatteryNotLow(true)   // 배터리가 부족하지 않은 경우에만 작업 실행
                        .setRequiresCharging(false)       // 충전 중이 아닐 때도 작업 허용
                        .build())
                .build();

        WorkManager.getInstance(this).enqueue(recordWorkRequest);
    }

    // 녹화 시작 메소드
    private void startRecording() {
        isRecording = true;
        recordButton.setBackgroundResource(R.drawable.rounded_button_red);
        recordButton.setTextColor(ContextCompat.getColor(this, R.color.red));
        recordButton.setText(R.string.btn_stop_text); // 녹화 중지 버튼으로 텍스트 변경
        statusText.setText(R.string.status_on); // 상태 텍스트 변경

        // WorkManager를 통해 작업 시작
        WorkManager.getInstance(this).enqueue(new PeriodicWorkRequest.Builder(RecordWorker.class, 1, TimeUnit.MINUTES).build());
    }

    // 녹화 중지 메소드
    private void stopRecording() {
        isRecording = false;
        recordButton.setBackgroundResource(R.drawable.rounded_button);
        recordButton.setTextColor(ContextCompat.getColor(this, R.color.button_text_color_green));
        recordButton.setText(R.string.btn_start_text); // 녹화 시작 버튼으로 텍스트 변경
        statusText.setText(R.string.status_off); // 상태 텍스트 변경

        // WorkManager 작업 중지
        WorkManager.getInstance(this).cancelAllWork();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 센서 데이터 변경 이벤트 처리
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accX = event.values[0]; // 가속도 x 값 설정
            accY = event.values[1]; // 가속도 y 값 설정
            accZ = event.values[2]; // 가속도 z 값 설정
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyrX = event.values[0]; // 자이로스코프 x 값 설정
            gyrY = event.values[1]; // 자이로스코프 y 값 설정
            gyrZ = event.values[2]; // 자이로스코프 z 값 설정
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정확도 변경 이벤트 처리 (필요시 구현)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 위치 권한 요청 결과 처리
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되었을 때 처리할 작업
            }
        }
    }
}
