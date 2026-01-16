package com.parttime.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.parttime.R;
import com.parttime.api.ApiClient;
import com.parttime.api.ApiService;
import com.parttime.models.ApplicationRequest;
import com.parttime.models.ApplicationResponse;
import com.parttime.models.JobResponse;
import com.parttime.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobDetailActivity extends AppCompatActivity {
    
    private JobResponse job;
    private TextView tvTitle, tvDescription, tvProvider, tvSalary, tvAddress, tvJobType, tvRequirements;
    private EditText etApplicationMessage;
    private Button btnApply;
    private ApiService apiService;
    private SharedPreferencesManager prefsManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        
        ApiClient.initialize(this);
        apiService = ApiClient.getApiService();
        prefsManager = SharedPreferencesManager.getInstance(this);
        
        job = (JobResponse) getIntent().getSerializableExtra("job");
        if (job == null) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        displayJobDetails();
        
        // Hide apply button if user is provider or already applied
        String role = prefsManager.getUserRole();
        if (role != null && role.equals("PROVIDER")) {
            btnApply.setVisibility(View.GONE);
            etApplicationMessage.setVisibility(View.GONE);
        }
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvProvider = findViewById(R.id.tvProvider);
        tvSalary = findViewById(R.id.tvSalary);
        tvAddress = findViewById(R.id.tvAddress);
        tvJobType = findViewById(R.id.tvJobType);
        tvRequirements = findViewById(R.id.tvRequirements);
        etApplicationMessage = findViewById(R.id.etApplicationMessage);
        btnApply = findViewById(R.id.btnApply);
        
        btnApply.setOnClickListener(v -> submitApplication());
    }
    
    private void displayJobDetails() {
        tvTitle.setText(job.getTitle());
        tvDescription.setText(job.getDescription() != null ? job.getDescription() : "No description");
        tvProvider.setText("Provider: " + job.getProviderName());
        
        if (job.getSalaryMin() != null && job.getSalaryMax() != null) {
            tvSalary.setText(String.format("Salary: $%.2f - $%.2f", job.getSalaryMin(), job.getSalaryMax()));
        } else if (job.getSalaryMin() != null) {
            tvSalary.setText("Salary: From $" + job.getSalaryMin());
        } else {
            tvSalary.setText("Salary: Not specified");
        }
        
        tvAddress.setText("Location: " + (job.getAddress() != null ? job.getAddress() : "Not specified"));
        tvJobType.setText("Job Type: " + (job.getJobType() != null ? job.getJobType() : "Not specified"));
        tvRequirements.setText("Requirements: " + (job.getRequirements() != null ? job.getRequirements() : "None"));
    }
    
    private void submitApplication() {
        String message = etApplicationMessage.getText().toString().trim();
        
        ApplicationRequest request = new ApplicationRequest(job.getId(), message);
        Call<ApplicationResponse> call = apiService.createApplication(request);
        
        btnApply.setEnabled(false);
        btnApply.setText("Applying...");
        
        call.enqueue(new Callback<ApplicationResponse>() {
            @Override
            public void onResponse(Call<ApplicationResponse> call, Response<ApplicationResponse> response) {
                btnApply.setEnabled(true);
                btnApply.setText("Apply");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(JobDetailActivity.this, "Application submitted successfully!", Toast.LENGTH_SHORT).show();
                    btnApply.setVisibility(View.GONE);
                    etApplicationMessage.setVisibility(View.GONE);
                } else {
                    Toast.makeText(JobDetailActivity.this, "Failed to submit application: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApplicationResponse> call, Throwable t) {
                btnApply.setEnabled(true);
                btnApply.setText("Apply");
                Toast.makeText(JobDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


