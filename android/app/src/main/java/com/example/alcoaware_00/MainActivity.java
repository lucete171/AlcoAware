package com.example.alcoaware_00;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CalendarView;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button recordPageButton;
    private CalendarView calendarView;
    private Button logoutButton;
    private FirebaseAuth mFirebaseAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendar_view);
        recordPageButton = findViewById(R.id.record_page_button);
        logoutButton = findViewById(R.id.button_Logout); // 로그아웃 버튼의 ID를 XML에 맞게 설정하세요

        mFirebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        recordPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            mFirebaseAuth.signOut();
            clearLoginState(); // 로그인 상태 초기화
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void clearLoginState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }
}
