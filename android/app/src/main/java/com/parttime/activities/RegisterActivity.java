package com.parttime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.parttime.R;
import com.parttime.api.ApiClient;
import com.parttime.api.ApiService;
import com.parttime.models.AuthResponse;
import com.parttime.models.RegisterRequest;
import com.parttime.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText etName, etEmail, etPassword, etPhone;
    private RadioGroup rgRole;
    private RadioButton rbStudent, rbProvider;
    private Button btnRegister;
    private ApiService apiService;
    private SharedPreferencesManager prefsManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        ApiClient.initialize(this);
        apiService = ApiClient.getApiService();
        prefsManager = SharedPreferencesManager.getInstance(this);
        
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        rgRole = findViewById(R.id.rgRole);
        rbStudent = findViewById(R.id.rbStudent);
        rbProvider = findViewById(R.id.rbProvider);
        btnRegister = findViewById(R.id.btnRegister);
        
        rbStudent.setChecked(true);
        
        btnRegister.setOnClickListener(v -> performRegister());
    }
    
    private void performRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        String role = "STUDENT";
        if (selectedRoleId == R.id.rbProvider) {
            role = "PROVIDER";
        }
        
        btnRegister.setEnabled(false);
        btnRegister.setText("Registering...");
        
        // For now, location will be null. It can be updated later from profile
        RegisterRequest request = new RegisterRequest(name, email, password, phone, role, null, null);
        Call<AuthResponse> call = apiService.register(request);
        
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Register");
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    prefsManager.saveToken(authResponse.getToken());
                    prefsManager.saveUserInfo(
                            authResponse.getUserId(),
                            authResponse.getName(),
                            authResponse.getEmail(),
                            authResponse.getRole()
                    );
                    navigateToHome();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Register");
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}


