package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class UserTests {
    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testParameterizedConstructor() {
        User user = new User("John Doe", "john@example.com", "password123");
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        user.setId(id);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setPassword("password456");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(id, user.getId());
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("password456", user.getPassword());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testLifecycleCallbacks_onCreate_and_onUpdate() throws Exception {
        User user = new User();

        Method onCreate = User.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(user);

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        LocalDateTime firstUpdatedAt = user.getUpdatedAt();

        Thread.sleep(5); // ensure timestamp difference
        Method onUpdate = User.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(user);

        assertTrue(user.getUpdatedAt().isAfter(firstUpdatedAt) || user.getUpdatedAt().isEqual(firstUpdatedAt));
    }
}

