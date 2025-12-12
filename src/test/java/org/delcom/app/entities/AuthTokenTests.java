package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthTokenTests {
    @Test
    void testDefaultConstructor() {
        AuthToken token = new AuthToken();
        assertNotNull(token);
    }

    @Test
    void testParameterizedConstructor() {
        UUID userId = UUID.randomUUID();
        AuthToken token = new AuthToken("test-token", userId);
        assertEquals("test-token", token.getToken());
        assertEquals(userId, token.getUserId());
    }

    @Test
    void testGettersAndSetters() {
        AuthToken token = new AuthToken();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        token.setId(id);
        token.setToken("new-token");
        token.setUserId(userId);
        token.setCreatedAt(now);

        assertEquals(id, token.getId());
        assertEquals("new-token", token.getToken());
        assertEquals(userId, token.getUserId());
        assertEquals(now, token.getCreatedAt());
    }

    @Test
    void testOnCreateSetsCreatedAt() {
        AuthToken token = new AuthToken();

        token.onCreate();

        assertNotNull(token.getCreatedAt());
        assertTrue(!token.getCreatedAt().isAfter(LocalDateTime.now()));
    }
}

