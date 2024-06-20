package com.example.alcoaware_00;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.os.Bundle;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // ----------------------------------임시 설정 코드----------------------------------
            // 로그인 검증 로직
            String admin_e = "id@gmail.com";

            if(email.equals(admin_e)){
                if(password.equals(confirmPassword)){
                    Intent intent = new Intent(this, UserInfoActivity.class);
                    startActivity(intent);
                    // 계정 정보 등록하기
                }
                else{
                    Toast.makeText(this, "The password for verification does not match the password.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "This email is already a registered account.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLoginClicked(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
