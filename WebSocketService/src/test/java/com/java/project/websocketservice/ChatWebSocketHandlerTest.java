package com.java.project.websocketservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.project.websocketservice.dto.ChatMessageDto;
import com.java.project.websocketservice.handler.ChatWebSocketHandler;
import com.java.project.websocketservice.redis.RedisWebSocketSessionStore;
import com.java.project.websocketservice.service.ChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatWebSocketHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisWebSocketSessionStore redisWebSocketSessionStore;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatWebSocketHandler chatWebSocketHandler;

    @Mock
    private WebSocketSession session;

    @Test
    void testAfterConnectionEstablished() throws Exception {
        // Устанавливаем mock для получения chatRoomId
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws/chat?roomId=123"));

        chatWebSocketHandler.afterConnectionEstablished(session);

        // Проверяем, что сессия добавлена в Redis хранилище
        verify(redisWebSocketSessionStore).addSession(eq("123"), eq(session));

        // Проверяем, что сообщение о присоединении отправлено в ChatService
        verify(chatService).processAndSendMessage(any(ChatMessageDto.class));
    }

    @Test
    void testHandleTextMessage() throws Exception {
        // Устанавливаем mock для получения chatRoomId и чтения сообщения
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws/chat?roomId=123"));
        String messagePayload = "{\"type\": \"CHAT\", \"content\": \"Hello\"}";
        when(objectMapper.readValue(anyString(), eq(ChatMessageDto.class)))
                .thenReturn(ChatMessageDto.builder().type(ChatMessageDto.MessageType.CHAT).content("Hello").build());

        TextMessage message = new TextMessage(messagePayload);

        chatWebSocketHandler.handleTextMessage(session, message);

        // Проверяем, что сообщение было отправлено в ChatService
        verify(chatService).processAndSendMessage(any(ChatMessageDto.class));

        // Проверяем, что клиент получил подтверждение
        verify(session).sendMessage(new TextMessage("Message sent to Kafka: Hello"));
    }

    @Test
    void testAfterConnectionClosed() throws Exception {
        // Устанавливаем mock для получения chatRoomId
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws/chat?roomId=123"));

        chatWebSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Проверяем, что сессия удалена из Redis хранилища
        verify(redisWebSocketSessionStore).removeSession(eq("123"), eq(session));

        // Проверяем, что сообщение о выходе отправлено в ChatService
        verify(chatService).processAndSendMessage(any(ChatMessageDto.class));
    }
}
