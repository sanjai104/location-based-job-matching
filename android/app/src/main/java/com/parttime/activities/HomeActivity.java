package com.parttime.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.parttime.R;
import com.parttime.adapters.JobAdapter;
import com.parttime.api.ApiClient;
import com.parttime.api.ApiService;
import com.parttime.models.JobResponse;
import com.parttime.services.LocationService;
import com.parttime.utils.LocationHelper;
import com.parttime.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private JobAdapter adapter;
    private ProgressBar progressBar;
    private EditText etMinSalary, etMaxSalary, etSearchKeyword;
    private Button btnSearch, btnUseLocation;
    private ApiService apiService;
    private SharedPreferencesManager prefsManager;
    private LocationService locationService;
    private Double currentLat, currentLng;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        ApiClient.initialize(this);
        apiService = ApiClient.getApiService();
        prefsManager = SharedPreferencesManager.getInstance(this);
        locationService = new LocationService(this);
        
        if (!prefsManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
        
        initViews();
        setupRecyclerView();
        loadJobs();
        
        if (LocationHelper.hasLocationPermission(this)) {
            getCurrentLocation();
        }
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        etMinSalary = findViewById(R.id.etMinSalary);
        etMaxSalary = findViewById(R.id.etMaxSalary);
        etSearchKeyword = findViewById(R.id.etSearchKeyword);
        btnSearch = findViewById(R.id.btnSearch);
        btnUseLocation = findViewById(R.id.btnUseLocation);
        
        btnSearch.setOnClickListener(v -> performSearch());
        btnUseLocation.setOnClickListener(v -> {
            if (LocationHelper.hasLocationPermission(this)) {
                getCurrentLocation();
            } else {
                LocationHelper.requestLocationPermission(this);
            }
        });
    }
    
    private void setupRecyclerView() {
        adapter = new JobAdapter(new ArrayList<>(), job -> {
            Intent intent = new Intent(HomeActivity.this, JobDetailActivity.class);
            intent.putExtra("job", job);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void loadJobs() {
        showProgress(true);
        Call<List<JobResponse>> call = apiService.getAllJobs();
        call.enqueue(new Callback<List<JobResponse>>() {
            @Override
            public void onResponse(Call<List<JobResponse>> call, Response<List<JobResponse>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateJobs(response.body());
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to load jobs", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<JobResponse>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void performSearch() {
        showProgress(true);
        
        String minSalaryStr = etMinSalary.getText().toString().trim();
        String maxSalaryStr = etMaxSalary.getText().toString().trim();
        String keyword = etSearchKeyword.getText().toString().trim();
        
        Double minSalary = minSalaryStr.isEmpty() ? null : Double.parseDouble(minSalaryStr);
        Double maxSalary = maxSalaryStr.isEmpty() ? null : Double.parseDouble(maxSalaryStr);
        String keywordParam = keyword.isEmpty() ? null : keyword;
        
        Call<List<JobResponse>> call = apiService.searchJobs(
                currentLat, currentLng, 10.0, // 10km radius
                minSalary, maxSalary,
                null, // jobType
                keywordParam
        );
        
        call.enqueue(new Callback<List<JobResponse>>() {
            @Override
            public void onResponse(Call<List<JobResponse>> call, Response<List<JobResponse>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateJobs(response.body());
                    if (response.body().isEmpty()) {
                        Toast.makeText(HomeActivity.this, "No jobs found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<JobResponse>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onLocationError(String error) {
                Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        String role = prefsManager.getUserRole();
        if (role != null && role.equals("PROVIDER")) {
            menu.findItem(R.id.menu_post_job).setVisible(true);
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.menu_messages) {
            startActivity(new Intent(this, MessagesActivity.class));
            return true;
        } else if (id == R.id.menu_my_applications) {
            startActivity(new Intent(this, MyApplicationsActivity.class));
            return true;
        } else if (id == R.id.menu_post_job) {
            startActivity(new Intent(this, PostJobActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            prefsManager.clear();
            navigateToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


