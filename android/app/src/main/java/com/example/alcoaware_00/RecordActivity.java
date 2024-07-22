package com.example.alcoaware_00;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RecordActivity extends AppCompatActivity implements SensorEventListener {

    // UI 요소 선언
    private ToggleButton toggleButton; // 음주 상태 토글 버튼
    private TextView drinkingText; // 음주 상태 텍스트 뷰
    private Button recordButton; // 녹화 버튼
    private TextView statusText; // 녹화 상태 텍스트 뷰
    private Button mainButton;

    // 녹화 상태 변수
    private boolean isRecording = false;

    // 핸들러와 러너블 선언
    private Handler handler; // 데이터 전송을 위한 핸들러
    private Runnable dataSender; // 데이터 전송을 위한 러너블

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
        setContentView(R.layout.activity_record);

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

        // 메인 스레드의 핸들러 생성
        handler = new Handler(Looper.getMainLooper());

        mainButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    // 데이터 녹화 시작 메소드
    private void startRecording() {
        isRecording = true;
        recordButton.setBackgroundResource(R.drawable.rounded_button_red);
        recordButton.setTextColor(ContextCompat.getColor(this, R.color.red));
        recordButton.setText(R.string.btn_stop_text); // 녹화 중지 버튼으로 텍스트 변경
        statusText.setText(R.string.status_on); // 상태 텍스트 변경

        // 센서 등록
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        // 5분마다 데이터 전송을 위한 러너블 설정
        dataSender = new Runnable() {
            @Override
            public void run() {
                sendDeviceData(); // 기기 데이터 전송 메소드 호출
                handler.postDelayed(this, 30000); // 30초마다 데이터 전송
            }
        };
        handler.post(dataSender); // 핸들러에 러너블 추가
    }

    // 데이터 녹화 중지 메소드
    private void stopRecording() {
        isRecording = false;
        recordButton.setBackgroundResource(R.drawable.rounded_button);
        recordButton.setTextColor(ContextCompat.getColor(this, R.color.button_text_color_green));
        recordButton.setText(R.string.btn_start_text); // 녹화 시작 버튼으로 텍스트 변경
        statusText.setText(R.string.status_off); // 상태 텍스트 변경

        // 센서 등록 해제
        sensorManager.unregisterListener(this);

        // 핸들러의 러너블 제거
        handler.removeCallbacks(dataSender);
    }

    // 기기 데이터 전송 메소드
    private void sendDeviceData() {
        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // 마지막으로 알려진 위치 가져오기
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location; // 현재 위치 설정

                // 전송할 기기 데이터 맵 설정
                Map<String, Object> deviceData = new HashMap<>();
                deviceData.put("timestamp", System.currentTimeMillis()); // 타임스탬프 추가
                deviceData.put("location", currentLocation != null ? currentLocation.toString() : "unknown"); // 위치 정보 추가
                deviceData.put("acc_x", accX); // 가속도 x 값 추가
                deviceData.put("acc_y", accY); // 가속도 y 값 추가
                deviceData.put("acc_z", accZ); // 가속도 z 값 추가
                deviceData.put("gyr_x", gyrX); // 자이로스코프 x 값 추가
                deviceData.put("gyr_y", gyrY); // 자이로스코프 y 값 추가
                deviceData.put("gyr_z", gyrZ); // 자이로스코프 z 값 추가
                deviceData.put("drinking", toggleButton.isChecked()); // 음주 여부 추가

                // Firestore reference
                db.collection("UserAccount").document(uid)
                        .collection("DeviceData").add(deviceData)
                        .addOnSuccessListener(aVoid -> {
                            // 이후 문구 수정이나 삭제
                            Toast.makeText(RecordActivity.this, "사용자 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(RecordActivity.this, "사용자 정보 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
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
                sendDeviceData(); // 위치 권한이 승인되면 데이터 전송
            }
        }
    }
}
