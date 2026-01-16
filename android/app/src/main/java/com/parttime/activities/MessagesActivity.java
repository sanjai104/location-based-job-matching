package com.parttime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.parttime.R;
import com.parttime.adapters.ConversationAdapter;
import com.parttime.adapters.MessageAdapter;
import com.parttime.api.ApiClient;
import com.parttime.api.ApiService;
import com.parttime.models.ConversationResponse;
import com.parttime.models.MessageRequest;
import com.parttime.models.MessageResponse;
import com.parttime.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewMessages;
    private ConversationAdapter conversationAdapter;
    private MessageAdapter messageAdapter;
    private ProgressBar progressBar;
    private EditText etMessage;
    private ImageButton btnSend;
    private TextView tvConversationTitle;
    private View layoutConversations, layoutMessages;
    
    private ApiService apiService;
    private SharedPreferencesManager prefsManager;
    private Long currentConversationUserId;
    private boolean isShowingConversations = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        
        ApiClient.initialize(this);
        apiService = ApiClient.getApiService();
        prefsManager = SharedPreferencesManager.getInstance(this);
        
        initViews();
        setupRecyclerView();
        loadConversations();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        progressBar = findViewById(R.id.progressBar);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvConversationTitle = findViewById(R.id.tvConversationTitle);
        layoutConversations = findViewById(R.id.layoutConversations);
        layoutMessages = findViewById(R.id.layoutMessages);
        
        btnSend.setOnClickListener(v -> sendMessage());
    }
    
    private void setupRecyclerView() {
        conversationAdapter = new ConversationAdapter(new ArrayList<>(), conversation -> {
            openConversation(conversation);
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(conversationAdapter);
    }
    
    private void loadConversations() {
        showProgress(true);
        Call<List<ConversationResponse>> call = apiService.getConversations();
        call.enqueue(new Callback<List<ConversationResponse>>() {
            @Override
            public void onResponse(Call<List<ConversationResponse>> call, Response<List<ConversationResponse>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    conversationAdapter.updateConversations(response.body());
                } else {
                    Toast.makeText(MessagesActivity.this, "Failed to load conversations", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<ConversationResponse>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(MessagesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void openConversation(ConversationResponse conversation) {
        currentConversationUserId = conversation.getUserId();
        tvConversationTitle.setText(conversation.getUserName());
        isShowingConversations = false;
        layoutConversations.setVisibility(View.GONE);
        layoutMessages.setVisibility(View.VISIBLE);
        
        messageAdapter = new MessageAdapter(new ArrayList<>(), prefsManager.getUserId());
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        
        loadMessages(conversation.getUserId());
    }
    
    private void loadMessages(Long userId) {
        showProgress(true);
        Call<List<MessageResponse>> call = apiService.getMessages(userId);
        call.enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter.updateMessages(response.body());
                    recyclerView.scrollToPosition(response.body().size() - 1);
                } else {
                    Toast.makeText(MessagesActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(MessagesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void sendMessage() {
        if (currentConversationUserId == null) {
            Toast.makeText(this, "Please select a conversation", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }
        
        MessageRequest request = new MessageRequest(currentConversationUserId, null, content);
        Call<MessageResponse> call = apiService.sendMessage(request);
        
        btnSend.setEnabled(false);
        
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                btnSend.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    etMessage.setText("");
                    loadMessages(currentConversationUserId);
                } else {
                    Toast.makeText(MessagesActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                btnSend.setEnabled(true);
                Toast.makeText(MessagesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public void onBackPressed() {
        if (!isShowingConversations) {
            isShowingConversations = true;
            layoutMessages.setVisibility(View.GONE);
            layoutConversations.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(conversationAdapter);
            currentConversationUserId = null;
        } else {
            super.onBackPressed();
        }
    }
}

