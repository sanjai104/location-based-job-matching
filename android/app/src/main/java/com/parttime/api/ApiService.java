package com.parttime.api;

import com.parttime.models.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {
    
    // Authentication
    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
    
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);
    
    // Jobs
    @GET("api/jobs")
    Call<List<JobResponse>> getAllJobs();
    
    @GET("api/jobs/{id}")
    Call<JobResponse> getJobById(@Path("id") Long id);
    
    @GET("api/jobs/search")
    Call<List<JobResponse>> searchJobs(
            @Query("lat") Double lat,
            @Query("lng") Double lng,
            @Query("radius") Double radius,
            @Query("minSalary") Double minSalary,
            @Query("maxSalary") Double maxSalary,
            @Query("jobType") String jobType,
            @Query("keyword") String keyword
    );
    
    @POST("api/jobs")
    Call<JobResponse> createJob(@Body JobRequest request);
    
    @GET("api/jobs/my-jobs")
    Call<List<JobResponse>> getMyJobs();
    
    // Applications
    @POST("api/applications")
    Call<ApplicationResponse> createApplication(@Body ApplicationRequest request);
    
    @GET("api/applications/my-applications")
    Call<List<ApplicationResponse>> getMyApplications();
    
    // Messages
    @GET("api/messages/conversations")
    Call<List<ConversationResponse>> getConversations();
    
    @GET("api/messages/{userId}")
    Call<List<MessageResponse>> getMessages(@Path("userId") Long userId);
    
    @POST("api/messages")
    Call<MessageResponse> sendMessage(@Body MessageRequest request);
    
    // Reviews
    @POST("api/reviews")
    Call<ReviewResponse> createReview(@Body ReviewRequest request);
    
    @GET("api/reviews/user/{userId}")
    Call<List<ReviewResponse>> getUserReviews(@Path("userId") Long userId);
    
    @GET("api/reviews/user/{userId}/average")
    Call<Double> getAverageRating(@Path("userId") Long userId);
}

