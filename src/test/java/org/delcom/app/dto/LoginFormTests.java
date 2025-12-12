package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginFormTests {
    @Test
    void testDefaultConstructor() {
        LoginForm form = new LoginForm();
        assertNotNull(form);
    }

    @Test
    void testParameterizedConstructor() {
        LoginForm form = new LoginForm("john@example.com", "password123");
        assertEquals("john@example.com", form.getEmail());
        assertEquals("password123", form.getPassword());
    }

    @Test
    void testGettersAndSetters() {
        LoginForm form = new LoginForm();
        form.setEmail("jane@example.com");
        form.setPassword("password456");

        assertEquals("jane@example.com", form.getEmail());
        assertEquals("password456", form.getPassword());
    }
}

