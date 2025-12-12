package org.delcom.app.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

class HomeControllerTests {
    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIndex_NotLoggedIn() {
        org.springframework.ui.Model model = mock(org.springframework.ui.Model.class);
        when(request.getCookies()).thenReturn(null);
        
        String view = homeController.index(request, model);
        assertEquals("index", view);
        verify(model).addAttribute("isLoggedIn", false);
    }

    @Test
    void testIndex_LoggedIn() {
        org.springframework.ui.Model model = mock(org.springframework.ui.Model.class);
        Cookie cookie = new Cookie("token", "valid-token");
        Cookie[] cookies = {cookie};
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");

        when(request.getCookies()).thenReturn(cookies);
        when(authService.getUserByToken("valid-token")).thenReturn(user);

        String view = homeController.index(request, model);
        assertEquals("index", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("isLoggedIn", true);
    }

    @Test
    void testIndex_InvalidToken() {
        org.springframework.ui.Model model = mock(org.springframework.ui.Model.class);
        Cookie cookie = new Cookie("token", "invalid-token");
        Cookie[] cookies = {cookie};

        when(request.getCookies()).thenReturn(cookies);
        when(authService.getUserByToken("invalid-token")).thenReturn(null);

        String view = homeController.index(request, model);
        assertEquals("index", view);
        verify(model).addAttribute("isLoggedIn", false);
    }

    @Test
    void testIndex_WithCookiesButNoToken() {
        org.springframework.ui.Model model = mock(org.springframework.ui.Model.class);
        Cookie cookie1 = new Cookie("other", "value1");
        Cookie cookie2 = new Cookie("session", "value2");
        Cookie[] cookies = {cookie1, cookie2};

        when(request.getCookies()).thenReturn(cookies);

        String view = homeController.index(request, model);
        assertEquals("index", view);
        verify(model).addAttribute("isLoggedIn", false);
        verify(authService, never()).getUserByToken(anyString());
    }
}
