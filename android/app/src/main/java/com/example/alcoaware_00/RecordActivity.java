package com.example.alcoaware_00;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RecordActivity extends AppCompatActivity {
    private Button recordButton;
    private TextView statusTextView;
    private boolean isRecording = false;
    private Handler handler = new Handler();
    private Runnable recordTask;

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    private SensorEventListener sensorEventListener;

    private float[] accelerometerData = new float[3];
    private float[] gyroscopeData = new float[3];

    private Button mainPageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        recordButton = findViewById(R.id.record_button);
        statusTextView = findViewById(R.id.status_text);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, accelerometerData, 0, event.values.length);
                } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    System.arraycopy(event.values, 0, gyroscopeData, 0, event.values.length);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        recordTask = new Runnable() {
            @Override
            public void run() {
                recordSensorData();
                handler.postDelayed(this, 5 * 60 * 1000); // 5분마다 실행
            }
        };

        recordButton.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });


        mainPageButton = findViewById(R.id.main_page_button);

        mainPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void startRecording() {
        isRecording = true;
        statusTextView.setText("기록중");
        recordButton.setText("Stop");
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        handler.post(recordTask);
    }

    private void stopRecording() {
        isRecording = false;
        statusTextView.setText("기록되고 있지 않습니다");
        recordButton.setText("Start");
        sensorManager.unregisterListener(sensorEventListener);
        handler.removeCallbacks(recordTask);
    }

    private void recordSensorData() {
        long timestamp = System.currentTimeMillis();
        try (FileWriter fileWriter = new FileWriter(getFilesDir() + "/sensor_data.csv", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.printf("%d,%f,%f,%f,%f,%f,%f\n", timestamp,
                    accelerometerData[0], accelerometerData[1], accelerometerData[2],
                    gyroscopeData[0], gyroscopeData[1], gyroscopeData[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
