package com.entrelinhas.backend.service;

import com.entrelinhas.backend.domain.Chat;
import com.entrelinhas.backend.domain.Message;
import com.entrelinhas.backend.domain.UserAccount;
import com.entrelinhas.backend.domain.UserType;
import com.entrelinhas.backend.dto.chat.ChatResponse;
import com.entrelinhas.backend.dto.chat.MessageRequest;
import com.entrelinhas.backend.dto.chat.MessageResponse;
import com.entrelinhas.backend.repository.ChatRepository;
import com.entrelinhas.backend.repository.MessageRepository;
import com.entrelinhas.backend.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public ChatResponse openChat(Long otherUserId, UserAccount currentUser) {
        UserAccount other = userAccountRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        validateAllowedPair(currentUser, other);
        Chat chat = chatRepository.findBySenderAndReceiver(currentUser, other)
                .or(() -> chatRepository.findBySenderAndReceiver(other, currentUser))
                .orElseGet(() -> chatRepository.save(Chat.builder()
                        .sender(currentUser)
                        .receiver(other)
                        .build()));
        return toChatResponse(chat, currentUser);
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> list(UserAccount currentUser) {
        List<Chat> chats = chatRepository.findBySenderOrReceiver(currentUser, currentUser);
        return chats.stream()
                .filter(chat -> isAllowedPair(chat.getSender(), chat.getReceiver()))
                .map(chat -> toChatResponse(chat, currentUser))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> messages(Long chatId, UserAccount currentUser) {
        Chat chat = getAuthorizedChat(chatId, currentUser);
        List<Message> messages = messageRepository.findByChatOrderByCreatedAtAsc(chat);
        return messages.stream().map(this::toMessageResponse).toList();
    }

    @Transactional
    public MessageResponse sendMessage(Long chatId, MessageRequest request, UserAccount currentUser) {
        Chat chat = getAuthorizedChat(chatId, currentUser);
        Message message = Message.builder()
                .chat(chat)
                .sender(currentUser)
                .content(request.getContent())
                .read(false)
                .build();
        messageRepository.save(message);
        return toMessageResponse(message);
    }

    private Chat getAuthorizedChat(Long chatId, UserAccount currentUser) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        if (!chat.getSender().getId().equals(currentUser.getId()) && !chat.getReceiver().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Chat não pertence ao usuário");
        }
        validateAllowedPair(chat.getSender(), chat.getReceiver());
        return chat;
    }

    private ChatResponse toChatResponse(Chat chat, UserAccount currentUser) {
        UserAccount other = chat.getSender().getId().equals(currentUser.getId()) ? chat.getReceiver() : chat.getSender();
        return ChatResponse.builder()
                .id(chat.getId())
                .otherUserId(other.getId())
                .otherUserName(other.getFullName())
                .createdAt(chat.getCreatedAt())
                .build();
    }

    private MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .read(message.isRead())
                .build();
    }

    private void validateAllowedPair(UserAccount first, UserAccount second) {
        if (!isAllowedPair(first, second)) {
            throw new IllegalArgumentException("Combinação de usuários não permitida para chat");
        }
    }

    private boolean isAllowedPair(UserAccount first, UserAccount second) {
        UserType firstType = first.getUserType();
        UserType secondType = second.getUserType();
        return (firstType == UserType.COMPANY && secondType == UserType.FACTION)
                || (firstType == UserType.FACTION && secondType == UserType.COMPANY)
                || (firstType == UserType.FACTION && secondType == UserType.FACCIONISTA)
                || (firstType == UserType.FACCIONISTA && secondType == UserType.FACTION);
    }
}
