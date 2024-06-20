package com.example.alcoaware_00;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
            // Validate user input
            if (!validateInput()) {
                return;
            }

            // Save user data (dummy logic)

            // Proceed to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInput() {
        if (isEmpty(nameEditText) || isEmpty(ageEditText) || isEmpty(regionEditText)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (genderSpinner.getSelectedItemPosition() == 0 || drinkFrequencySpinner.getSelectedItemPosition() == 0 || drinkLocationSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void setSpinnerToPlaceholder(Spinner spinner) {
        spinner.setSelection(0); // Select the first item
    }

    private void setSpinnerDefaultColor(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    TextView textView = (TextView) view;
                    if (textView != null) {
                        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
