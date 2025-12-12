package org.delcom.app.configs;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTests {
    @Test
    void testPasswordEncoderBean() {
        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
    }
}

