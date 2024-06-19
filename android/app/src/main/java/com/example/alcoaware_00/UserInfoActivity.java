package com.example.alcoaware_00;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;
import android.graphics.Color;

public class UserInfoActivity extends AppCompatActivity {
    private EditText nameEditText, ageEditText, regionEditText;
    private Spinner genderSpinner, drinkFrequencySpinner, drinkLocationSpinner;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        nameEditText = findViewById(R.id.name);
        ageEditText = findViewById(R.id.age);
        regionEditText = findViewById(R.id.region);
        genderSpinner = findViewById(R.id.gender);
        drinkFrequencySpinner = findViewById(R.id.drink_frequency);
        drinkLocationSpinner = findViewById(R.id.drink_location);
        submitButton = findViewById(R.id.submit_button);

        // Setting up adapters
        setupSpinner(genderSpinner, R.array.gender_options);
        setupSpinner(drinkFrequencySpinner, R.array.drink_frequency_options);
        setupSpinner(drinkLocationSpinner, R.array.drink_location_options);

        submitButton.setOnClickListener(v -> {
            // 사용자 정보 저장 로직
            if (genderSpinner.getSelectedItemPosition() == 0 || drinkFrequencySpinner.getSelectedItemPosition() == 0 || drinkLocationSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(UserInfoActivity.this, "Please select all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // 다른 유효성 검사 및 데이터 저장 로직
        });
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResId, android.R.layout.simple_spinner_item) {
            @Override
            public boolean isEnabled(int position) {
                // 첫 번째 항목 (placeholder)은 선택 불가
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Placeholder의 텍스트 색상 변경
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set initial selection to the first item (placeholder)
        spinner.setSelection(0);
    }
}
