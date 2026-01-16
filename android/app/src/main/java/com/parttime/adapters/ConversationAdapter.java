package com.parttime.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.parttime.R;
import com.parttime.models.ConversationResponse;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    
    private List<ConversationResponse> conversations;
    private OnConversationClickListener listener;
    
    public interface OnConversationClickListener {
        void onConversationClick(ConversationResponse conversation);
    }
    
    public ConversationAdapter(List<ConversationResponse> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.bind(conversations.get(position));
    }
    
    @Override
    public int getItemCount() {
        return conversations != null ? conversations.size() : 0;
    }
    
    public void updateConversations(List<ConversationResponse> newConversations) {
        this.conversations = newConversations;
        notifyDataSetChanged();
    }
    
    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName, tvLastMessage, tvJobTitle;
        
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConversationClick(conversations.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(ConversationResponse conversation) {
            tvUserName.setText(conversation.getUserName());
            tvLastMessage.setText(conversation.getLastMessage());
            if (conversation.getJobTitle() != null) {
                tvJobTitle.setText("Job: " + conversation.getJobTitle());
            } else {
                tvJobTitle.setText("");
            }
        }
    }
}


