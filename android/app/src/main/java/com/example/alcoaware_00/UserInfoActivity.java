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
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.drink_frequency_options, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkFrequencySpinner.setAdapter(frequencyAdapter);

        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.drink_location_options, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkLocationSpinner.setAdapter(locationAdapter);

        // Set initial selection to the first item (placeholder)
        setSpinnerToPlaceholder(genderSpinner);
        setSpinnerToPlaceholder(drinkFrequencySpinner);
        setSpinnerToPlaceholder(drinkLocationSpinner);

        // Prevent selection of the first item
        setSpinnerDefaultColor(genderSpinner);
        setSpinnerDefaultColor(drinkFrequencySpinner);
        setSpinnerDefaultColor(drinkLocationSpinner);

        submitButton.setOnClickListener(v -> {
            // 사용자 정보 저장 로직
            if (genderSpinner.getSelectedItemPosition() == 0 || drinkFrequencySpinner.getSelectedItemPosition() == 0 || drinkLocationSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(UserInfoActivity.this, "Please select all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // 다른 유효성 검사 및 데이터 저장 로직
        });
    }

    private void setSpinnerToPlaceholder(Spinner spinner) {
        spinner.setSelection(0); // 첫 번째 항목 선택
    }

    private void setSpinnerDefaultColor(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}

