package org.delcom.app.services;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.AuthTokenRepository;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.utils.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        RegisterForm form = new RegisterForm("John Doe", "john@example.com", "password123");
        User savedUser = new User("John Doe", "john@example.com", "encoded");
        savedUser.setId(UUID.randomUUID());

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.register(form);
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testRegisterEmailExists() {
        RegisterForm form = new RegisterForm("John Doe", "john@example.com", "password123");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.register(form));
    }

    @Test
    void testLogin() {
        LoginForm form = new LoginForm("john@example.com", "password123");
        User user = new User("John Doe", "john@example.com", "encoded");
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtHelper.generateToken(user.getId())).thenReturn("token123");
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(new AuthToken());

        String token = authService.login(form);
        assertNotNull(token);
        assertEquals("token123", token);
    }

    @Test
    void testLoginInvalidCredentials() {
        LoginForm form = new LoginForm("john@example.com", "wrong");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(form));
    }

    @Test
    void testLogout() {
        AuthToken token = new AuthToken();
        when(authTokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        doNothing().when(authTokenRepository).delete(token);

        assertDoesNotThrow(() -> authService.logout("token123"));
    }

    @Test
    void testGetUserByToken() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        AuthToken authToken = new AuthToken();
        authToken.setUserId(userId);

        when(jwtHelper.validateToken("token123")).thenReturn(true);
        when(authTokenRepository.findByToken("token123")).thenReturn(Optional.of(authToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = authService.getUserByToken("token123");
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void testGetUserByToken_InvalidToken() {
        when(jwtHelper.validateToken("invalid-token")).thenReturn(false);

        User result = authService.getUserByToken("invalid-token");
        assertNull(result);
        verify(authTokenRepository, never()).findByToken(any());
    }

    @Test
    void testGetUserByToken_TokenNotFound() {
        when(jwtHelper.validateToken("token123")).thenReturn(true);
        when(authTokenRepository.findByToken("token123")).thenReturn(Optional.empty());

        User result = authService.getUserByToken("token123");
        assertNull(result);
    }

    @Test
    void testGetUserByToken_UserNotFound() {
        UUID userId = UUID.randomUUID();
        AuthToken authToken = new AuthToken();
        authToken.setUserId(userId);

        when(jwtHelper.validateToken("token123")).thenReturn(true);
        when(authTokenRepository.findByToken("token123")).thenReturn(Optional.of(authToken));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = authService.getUserByToken("token123");
        assertNull(result);
    }

    @Test
    void testLogin_WrongPassword() {
        LoginForm form = new LoginForm("john@example.com", "wrongpassword");
        User user = new User("John Doe", "john@example.com", "encoded");
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encoded")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(form));
    }

    @Test
    void testLogout_TokenNotFound() {
        when(authTokenRepository.findByToken("token123")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> authService.logout("token123"));
        verify(authTokenRepository, never()).delete(any());
    }
}

