package com.parttime.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.parttime.R;
import com.parttime.models.JobResponse;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    
    private List<JobResponse> jobs;
    private OnJobClickListener listener;
    
    public interface OnJobClickListener {
        void onJobClick(JobResponse job);
    }
    
    public JobAdapter(List<JobResponse> jobs, OnJobClickListener listener) {
        this.jobs = jobs;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobResponse job = jobs.get(position);
        holder.bind(job);
    }
    
    @Override
    public int getItemCount() {
        return jobs != null ? jobs.size() : 0;
    }
    
    public void updateJobs(List<JobResponse> newJobs) {
        this.jobs = newJobs;
        notifyDataSetChanged();
    }
    
    class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvProvider, tvSalary, tvDistance, tvAddress;
        
        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvProvider = itemView.findViewById(R.id.tvProvider);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJobClick(jobs.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(JobResponse job) {
            tvTitle.setText(job.getTitle());
            tvProvider.setText("By: " + job.getProviderName());
            
            if (job.getSalaryMin() != null && job.getSalaryMax() != null) {
                tvSalary.setText(String.format("$%.2f - $%.2f", job.getSalaryMin(), job.getSalaryMax()));
            } else if (job.getSalaryMin() != null) {
                tvSalary.setText("From: $" + job.getSalaryMin());
            } else {
                tvSalary.setText("Salary not specified");
            }
            
            if (job.getDistance() != null) {
                tvDistance.setText(String.format("%.2f km away", job.getDistance()));
            } else {
                tvDistance.setText("");
            }
            
            if (job.getAddress() != null && !job.getAddress().isEmpty()) {
                tvAddress.setText(job.getAddress());
            } else {
                tvAddress.setText("Location not specified");
            }
        }
    }
}


