package com.parttime.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.parttime.R;
import com.parttime.models.MessageResponse;
import com.parttime.utils.SharedPreferencesManager;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    
    private List<MessageResponse> messages;
    private Long currentUserId;
    
    public MessageAdapter(List<MessageResponse> messages, Long currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }
    
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 0 ? R.layout.item_message_sent : R.layout.item_message_received, parent, false);
        return new MessageViewHolder(view, viewType == 0);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }
    
    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }
    
    @Override
    public int getItemViewType(int position) {
        MessageResponse message = messages.get(position);
        return message.getSenderId().equals(currentUserId) ? 0 : 1;
    }
    
    public void updateMessages(List<MessageResponse> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }
    
    class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContent, tvTime;
        private boolean isSent;
        
        public MessageViewHolder(@NonNull View itemView, boolean isSent) {
            super(itemView);
            this.isSent = isSent;
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
        
        public void bind(MessageResponse message) {
            tvContent.setText(message.getContent());
            if (message.getTimestamp() != null) {
                tvTime.setText(message.getTimestamp());
            }
        }
    }
}


