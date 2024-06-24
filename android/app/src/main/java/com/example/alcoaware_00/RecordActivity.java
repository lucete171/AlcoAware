package com.example.alcoaware_00;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RecordActivity extends AppCompatActivity {

    private ToggleButton toggleButton;
    private TextView drinkingText;
    private Button recordButton;
    private TextView statusText;
    private boolean isRecording = false;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        toggleButton = findViewById(R.id.toggle_button);
        drinkingText = findViewById(R.id.drinking_text);
        recordButton = findViewById(R.id.record_button);
        statusText = findViewById(R.id.status_text);

        // 토글 버튼 상태 변경 리스너
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    drinkingText.setText(R.string.drinking_on);
                } else {
                    drinkingText.setText(R.string.drinking_off);
                }
            }
        });

        // 초기 상태 설정
        boolean isDrinking = toggleButton.isChecked();
        if (isDrinking) {
            drinkingText.setText(R.string.drinking_on);
        } else {
            drinkingText.setText(R.string.drinking_off);
        }

        // 레코드 버튼 클릭 리스너
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    // 서비스 시작
                    serviceIntent = new Intent(RecordActivity.this, RecordService.class);
                    startService(serviceIntent);
                    isRecording = true;
                    recordButton.setText(R.string.btn_stop_text);
                    statusText.setText(R.string.status_on);
                } else {
                    // 서비스 종료
                    stopService(serviceIntent);
                    isRecording = false;
                    recordButton.setText(R.string.btn_start_text);
                    statusText.setText(R.string.status_off);
                }
            }
        });

        // 초기 상태 설정
        if (isRecording) {
            statusText.setText(R.string.status_on);
        } else {
            statusText.setText(R.string.status_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 서비스도 함께 종료
        if (isRecording) {
            stopService(serviceIntent);
        }
    }
}
