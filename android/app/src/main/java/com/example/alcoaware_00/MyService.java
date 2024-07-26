package com.example.alcoaware_00;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyService extends Service {

    private static final String CHANNEL_ID = "AlcoAwareChannel";
    private Handler handler;
    private Runnable dataSender;

    // Firebase 관련 변수
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String uid;

    // 위치 관련 변수
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase 초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
        }

        // 위치 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 핸들러 초기화
        handler = new Handler(Looper.getMainLooper());

        // 데이터 전송을 위한 Runnable 설정
        dataSender = new Runnable() {
            @Override
            public void run() {
                sendDeviceData();
                handler.postDelayed(this, 30000); // 30초마다 데이터 전송
            }
        };
        handler.post(dataSender); // 핸들러에 Runnable 추가
    }

    private void sendDeviceData() {
        // 위치 권한 확인 (이전과 동일한 방법)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            return;
        }

        // 마지막으로 알려진 위치 가져오기
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            Map<String, Object> deviceData = new HashMap<>();
            deviceData.put("timestamp", System.currentTimeMillis());
            deviceData.put("location", location != null ? location.toString() : "unknown");
            deviceData.put("acc_x", RecordActivity.accX);
            deviceData.put("acc_y", RecordActivity.accY);
            deviceData.put("acc_z", RecordActivity.accZ);
            deviceData.put("gyr_x", RecordActivity.gyrX);
            deviceData.put("gyr_y", RecordActivity.gyrY);
            deviceData.put("gyr_z", RecordActivity.gyrZ);
            deviceData.put("drinking", RecordActivity.isDrinking);

            db.collection("UserAccount").document(uid)
                    .collection("DeviceData").add(deviceData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(MyService.this, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(MyService.this, "데이터 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, RecordActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("AlcoAware")
                .setContentText("Recording in progress")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "AlcoAware Recording Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(dataSender);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
