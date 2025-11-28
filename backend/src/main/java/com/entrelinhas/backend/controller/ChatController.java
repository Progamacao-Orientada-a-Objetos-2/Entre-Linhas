package com.entrelinhas.backend.controller;

import com.entrelinhas.backend.dto.chat.ChatResponse;
import com.entrelinhas.backend.dto.chat.MessageRequest;
import com.entrelinhas.backend.dto.chat.MessageResponse;
import com.entrelinhas.backend.repository.UserAccountRepository;
import com.entrelinhas.backend.security.UserPrincipal;
import com.entrelinhas.backend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserAccountRepository userAccountRepository;

    @PostMapping
    public ResponseEntity<ChatResponse> open(@AuthenticationPrincipal UserPrincipal principal,
                                             @RequestParam Long otherUserId) {
        return ResponseEntity.ok(chatService.openChat(otherUserId, userAccountRepository.findById(principal.getId()).orElseThrow()));
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(chatService.list(userAccountRepository.findById(principal.getId()).orElseThrow()));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageResponse>> messages(@PathVariable Long chatId,
                                                          @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(chatService.messages(chatId, userAccountRepository.findById(principal.getId()).orElseThrow()));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageResponse> send(@PathVariable Long chatId,
                                                @AuthenticationPrincipal UserPrincipal principal,
                                                @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(chatId, request, userAccountRepository.findById(principal.getId()).orElseThrow()));
    }
}
