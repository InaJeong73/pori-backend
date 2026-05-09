package com.pori.chat.service;

import com.pori.chat.domain.Conversation;
import com.pori.chat.domain.Message;
import com.pori.chat.dto.ConversationResponse;
import com.pori.chat.dto.MessageCreateRequest;
import com.pori.chat.dto.MessageResponse;
import com.pori.chat.repository.ConversationRepository;
import com.pori.chat.repository.MessageRepository;
import com.pori.global.exception.BusinessException;
import com.pori.global.exception.errorcode.GlobalErrorCode;
import com.pori.portfolio.repository.PortfolioRepository;
import com.pori.user.domain.User;
import com.pori.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final PortfolioRepository portfolioRepository;

    public ChatService(ConversationRepository conversationRepository,
                        MessageRepository messageRepository,
                        UserService userService,
                        PortfolioRepository portfolioRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.portfolioRepository = portfolioRepository;
    }

    public List<ConversationResponse> getConversations(UUID myId) {
        checkGate(myId);
        return conversationRepository.findByUserId(myId).stream()
                .map(c -> {
                    UUID peerId = c.getOtherUserId(myId);
                    User peer = userService.findById(peerId);
                    return ConversationResponse.from(c, myId, peer.getHandle());
                }).toList();
    }

    public Page<MessageResponse> getMessages(UUID myId, String peerHandle, int page, int size) {
        checkGate(myId);
        User peer = userService.findByHandle(peerHandle);
        Conversation conv = conversationRepository.findBetween(myId, peer.getId())
                .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conv.getId(), PageRequest.of(page, size))
                .map(MessageResponse::from);
    }

    @Transactional
    public MessageResponse sendMessage(UUID senderId, String peerHandle, MessageCreateRequest request) {
        checkGate(senderId);
        User peer = userService.findByHandle(peerHandle);
        Conversation conv = conversationRepository.findBetween(senderId, peer.getId())
                .orElseGet(() -> conversationRepository.save(Conversation.between(senderId, peer.getId())));
        Message message = Message.builder()
                .conversationId(conv.getId())
                .senderId(senderId)
                .body(request.body())
                .attachedPortfolioId(request.attachedPortfolioId())
                .build();
        messageRepository.save(message);
        conv.onNewMessage(senderId);
        conversationRepository.save(conv);
        return MessageResponse.from(message);
    }

    @Transactional
    public void markRead(UUID myId, String peerHandle) {
        User peer = userService.findByHandle(peerHandle);
        conversationRepository.findBetween(myId, peer.getId()).ifPresent(conv -> {
            conv.markRead(myId);
            conversationRepository.save(conv);
        });
    }

    private void checkGate(UUID userId) {
        long published = portfolioRepository.countPublishedByUserId(userId);
        int maxScore = portfolioRepository.findMaxScoreByUserId(userId).orElse(0);
        if (published == 0 || maxScore < 60) {
            throw new BusinessException(GlobalErrorCode.FORBIDDEN);
        }
    }
}
