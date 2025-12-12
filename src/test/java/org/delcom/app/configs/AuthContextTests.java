package org.delcom.app.configs;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AuthContextTests {

    @Test
    void setAndGetUserId_ShouldReturnSameValue() {
        UUID userId = UUID.randomUUID();
        try {
            AuthContext.setUserId(userId);
            assertEquals(userId, AuthContext.getUserId());
        } finally {
            AuthContext.clear();
        }
    }

    @Test
    void clear_ShouldRemoveValue() {
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
        AuthContext.clear();
        assertNull(AuthContext.getUserId());
    }

    @Test
    void threadIsolation_ShouldNotLeakBetweenThreads() throws InterruptedException {
        UUID mainUser = UUID.randomUUID();
        UUID otherUser = UUID.randomUUID();
        AuthContext.setUserId(mainUser);

        AtomicReference<UUID> threadValue = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            AuthContext.setUserId(otherUser);
            threadValue.set(AuthContext.getUserId());
            AuthContext.clear();
        });
        thread.start();
        thread.join();

        // main thread still has its own value
        assertEquals(mainUser, AuthContext.getUserId());
        // other thread saw its own value
        assertEquals(otherUser, threadValue.get());
        AuthContext.clear();
    }

    @Test
    void getUserId_ShouldReturnNull_WhenNotSet() {
        AuthContext.clear();
        assertNull(AuthContext.getUserId());
    }

    @Test
    void constructor_ShouldInstantiate() {
        // Cover default constructor for utility-style class
        assertNotNull(new AuthContext());
    }
}
