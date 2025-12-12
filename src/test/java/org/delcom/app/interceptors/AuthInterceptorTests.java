package org.delcom.app.interceptors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthInterceptorTests {
    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthInterceptor authInterceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPreHandleWithValidToken() throws Exception {
        Cookie cookie = new Cookie("token", "valid-token");
        Cookie[] cookies = {cookie};
        User user = new User();
        user.setId(UUID.randomUUID());

        when(request.getCookies()).thenReturn(cookies);
        when(authService.getUserByToken("valid-token")).thenReturn(user);

        boolean result = authInterceptor.preHandle(request, response, null);
        assertTrue(result);
    }

    @Test
    void testPreHandleWithoutToken() throws Exception {
        when(request.getCookies()).thenReturn(null);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = authInterceptor.preHandle(request, response, null);
        assertFalse(result);
        verify(response).sendRedirect("/auth/login");
    }

    @Test
    void testAfterCompletion() throws Exception {
        authInterceptor.afterCompletion(request, response, null, null);
        // Should not throw exception
        assertTrue(true);
    }

    @Test
    void testPreHandleWithInvalidToken() throws Exception {
        Cookie cookie = new Cookie("token", "invalid-token");
        Cookie[] cookies = {cookie};

        when(request.getCookies()).thenReturn(cookies);
        when(authService.getUserByToken("invalid-token")).thenReturn(null);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = authInterceptor.preHandle(request, response, null);
        assertFalse(result);
        verify(response).sendRedirect("/auth/login");
    }

    @Test
    void testPreHandleWithCookiesButNoToken() throws Exception {
        Cookie cookie1 = new Cookie("other", "value1");
        Cookie cookie2 = new Cookie("session", "value2");
        Cookie[] cookies = {cookie1, cookie2};

        when(request.getCookies()).thenReturn(cookies);
        doNothing().when(response).sendRedirect(anyString());

        boolean result = authInterceptor.preHandle(request, response, null);
        assertFalse(result);
        verify(response).sendRedirect("/auth/login");
        verify(authService, never()).getUserByToken(anyString());
    }
}

