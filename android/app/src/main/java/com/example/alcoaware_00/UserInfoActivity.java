package com.example.alcoaware_00;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

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

        // Move to Address Search Activity
        regionEditText.setFocusable(false);
        regionEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소 검색 웹뷰로 이동
                Intent intent=  new Intent(UserInfoActivity.this, AddressSearchActivity.class);
                getSearchResult.launch(intent);
            }
        });

        submitButton.setOnClickListener(v -> {
            // Validate user input
            if (!validateInput()) {
                return;
            }

            // Save user data to Firebase Database
            saveUserDataToDatabase();

            // Proceed to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                // AddressSearchActivity로부터 결과값을 전달받는다
                if(result.getResultCode() == RESULT_OK){
                    Intent data = result.getData();
                    if(data != null){
                        String region = data.getStringExtra("data");
                        regionEditText.setText(region);
                    }
                }
            }
    );

    private boolean validateInput() {
        if (isEmpty(nameEditText) || isEmpty(ageEditText) || isEmpty(regionEditText)) {
            Toast.makeText(this, "모든 질문에 응답하지 않았습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (genderSpinner.getSelectedItemPosition() == 0 || drinkFrequencySpinner.getSelectedItemPosition() == 0 || drinkLocationSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "모든 질문에 응답하지 않았습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        String ageStr = ageEditText.getText().toString().trim();
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "올바른 출생년도 형식을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidAge(age)) {
            Toast.makeText(this, "올바른 출생년도 형식을 입력해 주세요.", Toast.LENGTH_SHORT).show();
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

    private boolean isValidAge(int year) {
        // 출생년도가 4자리 숫자인지 확인
        return (year >= 1000 && year <= 9999);
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

    private void saveUserDataToDatabase() {
        // Get user input
        String name = nameEditText.getText().toString().trim();
        int age = Integer.parseInt(ageEditText.getText().toString().trim());
        String region = regionEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        String drinkFrequency = drinkFrequencySpinner.getSelectedItem().toString();
        String drinkLocation = drinkLocationSpinner.getSelectedItem().toString();

        // Firebase Database reference
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Create a map to hold the updated user info
            Map<String, Object> userInfoUpdates = new HashMap<>();
            userInfoUpdates.put("name", name);
            userInfoUpdates.put("age", age);
            userInfoUpdates.put("region", region);
            userInfoUpdates.put("gender", gender);
            userInfoUpdates.put("drinkFrequency", drinkFrequency);
            userInfoUpdates.put("drinkLocation", drinkLocation);

            // Save updates to 'UserAccount' node under current user's UID
            databaseRef.child("UserAccount").child(currentUser.getUid()).updateChildren(userInfoUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "사용자 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "사용자 정보 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
