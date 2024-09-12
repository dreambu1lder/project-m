package com.java.project.websocketservice;

import com.java.project.websocketservice.config.WebSocketConfig;
import com.java.project.websocketservice.handler.ChatWebSocketHandler;
import com.java.project.websocketservice.interceptor.RoomIdValidationIntercepter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = WebSocketConfig.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class WebSocketConfigTest {

    @MockBean
    private RoomIdValidationIntercepter roomIdValidationIntercepter;

    @MockBean
    private ChatWebSocketHandler chatWebSocketHandler;

    @Mock
    private WebSocketHandlerRegistry registry;

    @Mock
    private WebSocketHandlerRegistration registration; // Мок для WebSocketHandlerRegistration

    @Autowired
    private WebSocketConfig webSocketConfig;

    @BeforeEach
    void setUp() {
        // Настраиваем мок, чтобы метод addHandler возвращал registration
        when(registry.addHandler(any(WebSocketHandler.class), any(String[].class)))
                .thenReturn(registration);

        // Настраиваем мок, чтобы цепочки вызовов работали корректно
        when(registration.addInterceptors(any(HandshakeInterceptor.class)))
                .thenReturn(registration);
    }

    @Test
    void testWebSocketHandlerRegistration() {
        // Вызываем метод registerWebSocketHandlers вручную
        webSocketConfig.registerWebSocketHandlers(registry);

        // Проверяем, что обработчик чата зарегистрирован с корректным URL
        verify(registry).addHandler(eq(chatWebSocketHandler), eq("/ws/chat"));
    }

    @Test
    void testInterceptorRegistration() {
        // Вызываем метод registerWebSocketHandlers вручную
        webSocketConfig.registerWebSocketHandlers(registry);

        // Проверяем, что интерсептор зарегистрирован для обработчика
        verify(registration).addInterceptors(eq(roomIdValidationIntercepter));
    }

    @Test
    void testAllowedOrigins() {
        // Вызываем метод registerWebSocketHandlers вручную
        webSocketConfig.registerWebSocketHandlers(registry);

        // Проверяем, что указаны разрешенные источники (CORS)
        verify(registration).setAllowedOrigins(eq("*"));
    }
}