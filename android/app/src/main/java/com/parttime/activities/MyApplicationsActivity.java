package com.parttime.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.parttime.R;
import com.parttime.adapters.ApplicationAdapter;
import com.parttime.api.ApiClient;
import com.parttime.api.ApiService;
import com.parttime.models.ApplicationResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;
    private ApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);
        
        ApiClient.initialize(this);
        apiService = ApiClient.getApiService();
        
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ApplicationAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        loadApplications();
    }
    
    private void loadApplications() {
        Call<List<ApplicationResponse>> call = apiService.getMyApplications();
        call.enqueue(new Callback<List<ApplicationResponse>>() {
            @Override
            public void onResponse(Call<List<ApplicationResponse>> call, Response<List<ApplicationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateApplications(response.body());
                } else {
                    Toast.makeText(MyApplicationsActivity.this, "Failed to load applications", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<ApplicationResponse>> call, Throwable t) {
                Toast.makeText(MyApplicationsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


