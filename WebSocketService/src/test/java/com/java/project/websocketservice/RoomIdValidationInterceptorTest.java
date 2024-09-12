package com.java.project.websocketservice;

import com.java.project.websocketservice.client.ChatRoomFeignClient;
import com.java.project.websocketservice.interceptor.RoomIdValidationIntercepter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomIdValidationInterceptorTest {

    @Mock
    private ChatRoomFeignClient chatRoomFeignClient;

    @InjectMocks
    private RoomIdValidationIntercepter roomIdValidationInterceptor;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebSocketHandler wsHandler;

    @Test
    void testBeforeHandshakeValidRoom() throws Exception {
        // Мокаем запрос с корректным roomId
        URI uri = new URI("ws://localhost/ws/chat?roomId=123");
        when(request.getURI()).thenReturn(uri);
        when(chatRoomFeignClient.roomExists("123")).thenReturn(true);

        Map<String, Object> attributes = new HashMap<>();
        boolean result = roomIdValidationInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Проверяем, что комната существует и перехватчик возвращает true
        assertTrue(result);
        assertEquals("123", attributes.get("roomId"));
    }

    @Test
    void testBeforeHandshakeInvalidRoom() throws Exception {
        // Мокаем запрос с некорректным roomId
        URI uri = new URI("ws://localhost/ws/chat?roomId=invalid");
        when(request.getURI()).thenReturn(uri);
        when(chatRoomFeignClient.roomExists("invalid")).thenReturn(false);

        Map<String, Object> attributes = new HashMap<>();
        boolean result = roomIdValidationInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Проверяем, что перехватчик возвращает false и устанавливает статус BAD_REQUEST
        assertFalse(result);
        verify(response).setStatusCode(HttpStatus.BAD_REQUEST);
    }
}
