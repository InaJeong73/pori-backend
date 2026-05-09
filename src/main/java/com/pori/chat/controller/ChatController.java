package com.pori.chat.controller;

import com.pori.chat.dto.ConversationResponse;
import com.pori.chat.dto.MessageCreateRequest;
import com.pori.chat.dto.MessageResponse;
import com.pori.chat.service.ChatService;
import com.pori.global.response.ApiResponse;
import com.pori.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "채팅방 목록 · 메시지 조회 · 메시지 전송 · 읽음 처리")
@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "채팅방 목록 조회",
            description = "내가 참여 중인 채팅방 목록과 상대 핸들, 안 읽은 메시지 수, 마지막 메시지 시각을 조회합니다. Give-to-Get 조건을 충족해야 합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getConversations(principal.userId())));
    }

    @Operation(
            summary = "상대와의 메시지 목록 조회",
            description = "상대 핸들 기준으로 대화 메시지를 최신순 페이지로 조회합니다. Give-to-Get 조건을 충족해야 합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{peerHandle}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "대화 상대 핸들", example = "alice_fe")
            @PathVariable String peerHandle,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(principal.userId(), peerHandle, page, size)));
    }

    @Operation(
            summary = "메시지 전송",
            description = "상대 핸들로 1:1 메시지를 전송합니다. 필요하면 포트폴리오 UUID를 함께 첨부할 수 있습니다. Give-to-Get 조건을 충족해야 합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{peerHandle}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "메시지를 받을 상대 핸들", example = "bob_backend")
            @PathVariable String peerHandle,
            @Valid @RequestBody MessageCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                chatService.sendMessage(principal.userId(), peerHandle, request)));
    }

    @Operation(
            summary = "메시지 읽음 처리",
            description = "상대와의 대화방에서 내 기준 안 읽은 메시지를 읽음 처리합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{peerHandle}/read")
    public ResponseEntity<ApiResponse<Object>> markRead(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "읽음 처리할 대화 상대 핸들", example = "bob_backend")
            @PathVariable String peerHandle) {
        chatService.markRead(principal.userId(), peerHandle);
        return ResponseEntity.ok(ApiResponse.empty());
    }
}
