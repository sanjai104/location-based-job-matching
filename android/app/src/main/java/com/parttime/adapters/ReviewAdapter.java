package com.parttime.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.parttime.R;
import com.parttime.models.ReviewResponse;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    
    private List<ReviewResponse> reviews;
    
    public ReviewAdapter(List<ReviewResponse> reviews) {
        this.reviews = reviews;
    }
    
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }
    
    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }
    
    public void updateReviews(List<ReviewResponse> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }
    
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReviewerName, tvComment, tvJobTitle;
        private RatingBar ratingBar;
        
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
        
        public void bind(ReviewResponse review) {
            tvReviewerName.setText("By: " + review.getReviewerName());
            ratingBar.setRating(review.getRating());
            if (review.getComment() != null && !review.getComment().isEmpty()) {
                tvComment.setText(review.getComment());
            } else {
                tvComment.setText("No comment");
            }
            if (review.getJobTitle() != null) {
                tvJobTitle.setText("Job: " + review.getJobTitle());
            } else {
                tvJobTitle.setText("");
            }
        }
    }
}


