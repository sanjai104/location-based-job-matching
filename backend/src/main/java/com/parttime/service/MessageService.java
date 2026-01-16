package com.parttime.service;

import com.parttime.dto.ConversationResponse;
import com.parttime.dto.MessageRequest;
import com.parttime.dto.MessageResponse;
import com.parttime.model.Job;
import com.parttime.model.Message;
import com.parttime.model.User;
import com.parttime.repository.JobRepository;
import com.parttime.repository.MessageRepository;
import com.parttime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    public MessageResponse sendMessage(MessageRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User sender = (User) auth.getPrincipal();
        
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        Job job = null;
        if (request.getJobId() != null) {
            job = jobRepository.findById(request.getJobId())
                    .orElse(null);
        }
        
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setJob(job);
        message.setContent(request.getContent());
        
        message = messageRepository.save(message);
        return convertToResponse(message);
    }
    
    public List<MessageResponse> getMessages(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        List<Message> messages = messageRepository.findBySenderIdAndReceiverId(currentUser.getId(), userId);
        messages.addAll(messageRepository.findByReceiverIdAndSenderId(currentUser.getId(), userId));
        
        return messages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ConversationResponse> getConversations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        List<Message> allMessages = messageRepository.findAllUserMessages(currentUser.getId());
        
        Map<Long, Message> lastMessagesByUser = new HashMap<>();
        Map<Long, User> usersById = new HashMap<>();
        
        for (Message message : allMessages) {
            Long otherUserId;
            if (message.getSender().getId().equals(currentUser.getId())) {
                otherUserId = message.getReceiver().getId();
            } else {
                otherUserId = message.getSender().getId();
            }
            
            if (!lastMessagesByUser.containsKey(otherUserId) || 
                message.getTimestamp().isAfter(lastMessagesByUser.get(otherUserId).getTimestamp())) {
                lastMessagesByUser.put(otherUserId, message);
            }
            
            if (!usersById.containsKey(otherUserId)) {
                usersById.put(otherUserId, 
                    message.getSender().getId().equals(currentUser.getId()) ? 
                    message.getReceiver() : message.getSender());
            }
        }
        
        return lastMessagesByUser.entrySet().stream()
                .map(entry -> {
                    Message lastMessage = entry.getValue();
                    User otherUser = usersById.get(entry.getKey());
                    ConversationResponse conv = new ConversationResponse();
                    conv.setUserId(otherUser.getId());
                    conv.setUserName(otherUser.getName());
                    conv.setLastMessage(lastMessage.getContent());
                    conv.setLastMessageTime(lastMessage.getTimestamp());
                    if (lastMessage.getJob() != null) {
                        conv.setJobId(lastMessage.getJob().getId());
                        conv.setJobTitle(lastMessage.getJob().getTitle());
                    }
                    return conv;
                })
                .sorted(Comparator.comparing(ConversationResponse::getLastMessageTime).reversed())
                .collect(Collectors.toList());
    }
    
    private MessageResponse convertToResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(message.getSender().getName());
        response.setReceiverId(message.getReceiver().getId());
        response.setReceiverName(message.getReceiver().getName());
        if (message.getJob() != null) {
            response.setJobId(message.getJob().getId());
            response.setJobTitle(message.getJob().getTitle());
        }
        response.setContent(message.getContent());
        response.setTimestamp(message.getTimestamp());
        return response;
    }
}


