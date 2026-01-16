package com.parttime.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.parttime.R;
import com.parttime.api.ApiClient;
import com.parttime.api.ApiService;
import com.parttime.models.JobRequest;
import com.parttime.models.JobResponse;
import com.parttime.services.LocationService;
import com.parttime.utils.LocationHelper;
import com.parttime.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostJobActivity extends AppCompatActivity {
    
    private EditText etTitle, etDescription, etAddress, etSalaryMin, etSalaryMax, etRequirements;
    private Button btnPostJob, btnUseLocation;
    private ApiService apiService;
    private SharedPreferencesManager prefsManager;
    private LocationService locationService;
    private Double currentLat, currentLng;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        
        ApiClient.initialize(this);
        apiService = ApiClient.getApiService();
        prefsManager = SharedPreferencesManager.getInstance(this);
        locationService = new LocationService(this);
        
        initViews();
        
        if (LocationHelper.hasLocationPermission(this)) {
            getCurrentLocation();
        }
    }
    
    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etAddress = findViewById(R.id.etAddress);
        etSalaryMin = findViewById(R.id.etSalaryMin);
        etSalaryMax = findViewById(R.id.etSalaryMax);
        etRequirements = findViewById(R.id.etRequirements);
        btnPostJob = findViewById(R.id.btnPostJob);
        btnUseLocation = findViewById(R.id.btnUseLocation);
        
        btnPostJob.setOnClickListener(v -> postJob());
        btnUseLocation.setOnClickListener(v -> {
            if (LocationHelper.hasLocationPermission(this)) {
                getCurrentLocation();
            } else {
                LocationHelper.requestLocationPermission(this);
            }
        });
    }
    
    private void getCurrentLocation() {
        locationService.getCurrentLocation(new LocationService.LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                currentLat = latitude;
                currentLng = longitude;
                btnUseLocation.setText("Location: " + String.format("%.4f, %.4f", latitude, longitude));
            }
            
            @Override
            public void onLocationError(String error) {
                Toast.makeText(PostJobActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void postJob() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String salaryMinStr = etSalaryMin.getText().toString().trim();
        String salaryMaxStr = etSalaryMax.getText().toString().trim();
        String requirements = etRequirements.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (currentLat == null || currentLng == null) {
            Toast.makeText(this, "Please set location", Toast.LENGTH_SHORT).show();
            return;
        }
        
        JobRequest request = new JobRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setAddress(address);
        request.setLatitude(currentLat);
        request.setLongitude(currentLng);
        request.setJobType("PART_TIME");
        
        if (!salaryMinStr.isEmpty()) {
            try {
                request.setSalaryMin(Double.parseDouble(salaryMinStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid minimum salary", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        if (!salaryMaxStr.isEmpty()) {
            try {
                request.setSalaryMax(Double.parseDouble(salaryMaxStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid maximum salary", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        request.setRequirements(requirements);
        
        btnPostJob.setEnabled(false);
        btnPostJob.setText("Posting...");
        
        Call<JobResponse> call = apiService.createJob(request);
        call.enqueue(new Callback<JobResponse>() {
            @Override
            public void onResponse(Call<JobResponse> call, Response<JobResponse> response) {
                btnPostJob.setEnabled(true);
                btnPostJob.setText("Post Job");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PostJobActivity.this, "Job posted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PostJobActivity.this, "Failed to post job: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<JobResponse> call, Throwable t) {
                btnPostJob.setEnabled(true);
                btnPostJob.setText("Post Job");
                Toast.makeText(PostJobActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


