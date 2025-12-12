package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterFormTests {
    @Test
    void testDefaultConstructor() {
        RegisterForm form = new RegisterForm();
        assertNotNull(form);
    }

    @Test
    void testParameterizedConstructor() {
        RegisterForm form = new RegisterForm("John Doe", "john@example.com", "password123");
        assertEquals("John Doe", form.getName());
        assertEquals("john@example.com", form.getEmail());
        assertEquals("password123", form.getPassword());
    }

    @Test
    void testGettersAndSetters() {
        RegisterForm form = new RegisterForm();
        form.setName("Jane Doe");
        form.setEmail("jane@example.com");
        form.setPassword("password456");

        assertEquals("Jane Doe", form.getName());
        assertEquals("jane@example.com", form.getEmail());
        assertEquals("password456", form.getPassword());
    }
}

