import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.location.LocationService;
import com.kakao.sdk.location.model.KakaoLocation;
import com.kakao.sdk.location.model.LocationResponse;
import com.kakao.sdk.location.model.LocationRequest;

import java.util.HashMap;
import java.util.Map;

public class RecordWorker extends Worker implements SensorEventListener {

    private static final String TAG = "RecordWorker";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private FirebaseFirestore db;

    // 센서 데이터
    private float accX, accY, accZ;
    private float gyrX, gyrY, gyrZ;

    public RecordWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        // Firebase Firestore 초기화
        db = FirebaseFirestore.getInstance();

        // 카카오 SDK 초기화
        KakaoSdk.init(context, "<YOUR_APP_KEY>");

        // 센서 매니저 및 센서 초기화
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "WorkManager started");

        // 카카오 위치 API를 통해 위치 요청
        LocationService.getCurrentLocation(new LocationRequest.Builder().build(), new LocationResponse.Callback() {
            @Override
            public void onSuccess(KakaoLocation kakaoLocation) {
                double latitude = kakaoLocation.getLatitude();
                double longitude = kakaoLocation.getLongitude();
                uploadData(latitude, longitude);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Location request failed", t);
            }
        });

        // 센서 리스너 등록
        if (sensorManager != null) {
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        // 센서 데이터를 수집할 시간이 필요하므로 잠시 대기
        try {
            Thread.sleep(2000);  // 2초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }

        // 센서 리스너 등록 해제
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        return Result.success();
    }

    // Firebase Firestore에 데이터를 업로드하는 메서드
    private void uploadData(double latitude, double longitude) {
        Map<String, Object> data = new HashMap<>();
        data.put("accX", accX);
        data.put("accY", accY);
        data.put("accZ", accZ);
        data.put("gyrX", gyrX);
        data.put("gyrY", gyrY);
        data.put("gyrZ", gyrZ);
        data.put("latitude", latitude);
        data.put("longitude", longitude);
        data.put("timestamp", System.currentTimeMillis());

        // Firestore에 데이터 업로드
        db.collection("recordData")
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accX = event.values[0];
            accY = event.values[1];
            accZ = event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyrX = event.values[0];
            gyrY = event.values[1];
            gyrZ = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정확도 변경 시 필요한 경우 구현
    }
}
