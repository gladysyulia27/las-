package org.delcom.app.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtHelperTests {
    private JwtHelper jwtHelper;

    @BeforeEach
    void setUp() {
        jwtHelper = new JwtHelper();
        ReflectionTestUtils.setField(jwtHelper, "secret", "herbal-medicine-collection-secret-key-very-long-and-secure-key-for-jwt-token-generation");
        ReflectionTestUtils.setField(jwtHelper, "expiration", 86400000L);
    }

    @Test
    void testGenerateToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtHelper.generateToken(userId);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetUserIdFromToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtHelper.generateToken(userId);
        UUID extractedUserId = jwtHelper.getUserIdFromToken(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void testValidateToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtHelper.generateToken(userId);
        assertTrue(jwtHelper.validateToken(token));
        assertFalse(jwtHelper.validateToken("invalid-token"));
    }

    @Test
    void testValidateToken_WithNull() {
        assertFalse(jwtHelper.validateToken(null));
    }

    @Test
    void testValidateToken_WithEmptyString() {
        assertFalse(jwtHelper.validateToken(""));
    }

    @Test
    void testValidateToken_WithMalformedToken() {
        assertFalse(jwtHelper.validateToken("not.a.valid.token"));
    }
}

