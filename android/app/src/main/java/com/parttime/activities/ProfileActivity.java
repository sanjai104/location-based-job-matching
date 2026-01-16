package com.parttime.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.parttime.R;
import com.parttime.adapters.ReviewAdapter;
import com.parttime.api.ApiClient;
import com.parttime.api.ApiService;
import com.parttime.models.ReviewRequest;
import com.parttime.models.ReviewResponse;
import com.parttime.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    
    private TextView tvUserName, tvUserEmail, tvAverageRating;
    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private RatingBar ratingBar;
    private EditText etReviewComment;
    private Button btnSubmitReview;
    private ApiService apiService;
    private SharedPreferencesManager prefsManager;
    private Long profileUserId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        ApiClient.initialize(this);
        apiService = ApiClient.getApiService();
        prefsManager = SharedPreferencesManager.getInstance(this);
        
        profileUserId = getIntent().getLongExtra("userId", -1);
        if (profileUserId == -1) {
            profileUserId = prefsManager.getUserId();
        }
        
        initViews();
        setupRecyclerView();
        loadProfile();
        loadReviews();
        
        // Hide review submission if viewing own profile
        if (profileUserId.equals(prefsManager.getUserId())) {
            ratingBar.setVisibility(View.GONE);
            etReviewComment.setVisibility(View.GONE);
            btnSubmitReview.setVisibility(View.GONE);
        }
    }
    
    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        recyclerView = findViewById(R.id.recyclerView);
        ratingBar = findViewById(R.id.ratingBar);
        etReviewComment = findViewById(R.id.etReviewComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        
        btnSubmitReview.setOnClickListener(v -> submitReview());
    }
    
    private void setupRecyclerView() {
        adapter = new ReviewAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void loadProfile() {
        // For now, just show current user info
        // In a full implementation, you'd fetch user details from API
        tvUserName.setText(prefsManager.getUserName());
        tvUserEmail.setText(prefsManager.getUserEmail());
        
        loadAverageRating();
    }
    
    private void loadAverageRating() {
        Call<Double> call = apiService.getAverageRating(profileUserId);
        call.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double average = response.body();
                    tvAverageRating.setText(String.format("Average Rating: %.2f / 5.0", average));
                }
            }
            
            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                // Silently fail
            }
        });
    }
    
    private void loadReviews() {
        Call<List<ReviewResponse>> call = apiService.getUserReviews(profileUserId);
        call.enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call, Response<List<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateReviews(response.body());
                }
            }
            
            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = etReviewComment.getText().toString().trim();
        
        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ReviewRequest request = new ReviewRequest(profileUserId, null, rating, comment);
        Call<ReviewResponse> call = apiService.createReview(request);
        
        btnSubmitReview.setEnabled(false);
        btnSubmitReview.setText("Submitting...");
        
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                btnSubmitReview.setEnabled(true);
                btnSubmitReview.setText("Submit Review");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProfileActivity.this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    ratingBar.setRating(0);
                    etReviewComment.setText("");
                    loadReviews();
                    loadAverageRating();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                btnSubmitReview.setEnabled(true);
                btnSubmitReview.setText("Submit Review");
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


