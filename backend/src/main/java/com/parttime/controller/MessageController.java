package com.parttime.controller;

import com.parttime.dto.ConversationResponse;
import com.parttime.dto.MessageRequest;
import com.parttime.dto.MessageResponse;
import com.parttime.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        MessageResponse response = messageService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations() {
        List<ConversationResponse> conversations = messageService.getConversations();
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long userId) {
        List<MessageResponse> messages = messageService.getMessages(userId);
        return ResponseEntity.ok(messages);
    }
}


