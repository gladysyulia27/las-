package org.delcom.app.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTests {
    @Mock
    private AuthService authService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowLoginForm() {
        String view = authController.showLoginForm(model);
        assertEquals("auth/login", view);
        verify(model).addAttribute(eq("loginForm"), any(LoginForm.class));
    }

    @Test
    void testShowRegisterForm() {
        String view = authController.showRegisterForm(model);
        assertEquals("auth/register", view);
        verify(model).addAttribute(eq("registerForm"), any(RegisterForm.class));
    }

    @Test
    void testLoginSuccess() {
        LoginForm form = new LoginForm("test@example.com", "password");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.login(any(LoginForm.class))).thenReturn("token123");

        String view = authController.login(form, bindingResult, response, model);
        assertEquals("redirect:/herbal-medicines", view);
    }

    @Test
    void testLoginWithErrors() {
        LoginForm form = new LoginForm();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = authController.login(form, bindingResult, response, model);
        assertEquals("auth/login", view);
    }

    @Test
    void testRegisterSuccess() {
        RegisterForm form = new RegisterForm("John", "john@example.com", "password");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.register(any(RegisterForm.class))).thenReturn(null);

        String view = authController.register(form, bindingResult, model);
        assertEquals("auth/login", view);
    }

    @Test
    void testRegisterWithValidationErrors() {
        RegisterForm form = new RegisterForm();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = authController.register(form, bindingResult, model);

        assertEquals("auth/register", view);
        verify(authService, never()).register(any());
    }

    @Test
    void testLogout() {
        String view = authController.logout(response);
        assertEquals("redirect:/", view);
    }

    @Test
    void testLoginWithException() {
        LoginForm form = new LoginForm("test@example.com", "password");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.login(any(LoginForm.class))).thenThrow(new RuntimeException("Invalid credentials"));

        String view = authController.login(form, bindingResult, response, model);
        assertEquals("auth/login", view);
        verify(model).addAttribute(eq("error"), any());
    }

    @Test
    void testRegisterWithException() {
        RegisterForm form = new RegisterForm("John", "john@example.com", "password");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.register(any(RegisterForm.class))).thenThrow(new RuntimeException("Email already exists"));

        String view = authController.register(form, bindingResult, model);
        assertEquals("auth/register", view);
        verify(model).addAttribute(eq("error"), eq("Email already exists"));
    }

    @Test
    void testRegisterWithNullExceptionMessage() {
        RegisterForm form = new RegisterForm("John", "john@example.com", "password");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.register(any(RegisterForm.class))).thenThrow(new RuntimeException((String) null));

        String view = authController.register(form, bindingResult, model);
        assertEquals("auth/register", view);
        verify(model).addAttribute(eq("error"), eq("Terjadi kesalahan saat registrasi. Silakan coba lagi."));
    }

    @Test
    void testRegisterWithEmptyExceptionMessage() {
        RegisterForm form = new RegisterForm("John", "john@example.com", "password");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.register(any(RegisterForm.class))).thenThrow(new RuntimeException(""));

        String view = authController.register(form, bindingResult, model);
        assertEquals("auth/register", view);
        verify(model).addAttribute(eq("error"), eq("Terjadi kesalahan saat registrasi. Silakan coba lagi."));
    }

    @Test
    void testShowRegisterForm_WithExistingAttribute() {
        when(model.containsAttribute("registerForm")).thenReturn(true);
        String view = authController.showRegisterForm(model);
        assertEquals("auth/register", view);
        verify(model, never()).addAttribute(eq("registerForm"), any());
    }
}

