package com.pori.chat.controller;

import com.pori.chat.dto.ConversationResponse;
import com.pori.chat.dto.MessageCreateRequest;
import com.pori.chat.dto.MessageResponse;
import com.pori.chat.service.ChatService;
import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getConversations(principal.userId())));
    }

    @GetMapping("/{peerHandle}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String peerHandle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(principal.userId(), peerHandle, page, size)));
    }

    @PostMapping("/{peerHandle}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String peerHandle,
            @Valid @RequestBody MessageCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                chatService.sendMessage(principal.userId(), peerHandle, request)));
    }

    @PostMapping("/{peerHandle}/read")
    public ResponseEntity<ApiResponse<Object>> markRead(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String peerHandle) {
        chatService.markRead(principal.userId(), peerHandle);
        return ResponseEntity.ok(ApiResponse.empty());
    }
}
