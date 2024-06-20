package com.example.alcoaware_00;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CalendarView;
import android.os.Bundle;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private Button recordPageButton;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendar_view);
        recordPageButton = findViewById(R.id.record_page_button);

        recordPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });
    }
}
