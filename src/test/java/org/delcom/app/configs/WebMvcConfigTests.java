package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebMvcConfigTests {
    @Mock
    private AuthInterceptor authInterceptor;

    private WebMvcConfig webMvcConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webMvcConfig = new WebMvcConfig();
        // Set authInterceptor using reflection or create new instance
        try {
            java.lang.reflect.Field field = WebMvcConfig.class.getDeclaredField("authInterceptor");
            field.setAccessible(true);
            field.set(webMvcConfig, authInterceptor);
        } catch (Exception e) {
            // If reflection fails, just test without interceptor
        }
    }

    @Test
    void testAddInterceptors() {
        InterceptorRegistry registry = new InterceptorRegistry();
        assertDoesNotThrow(() -> webMvcConfig.addInterceptors(registry));
    }

    @Test
    void testAddResourceHandlers() {
        GenericWebApplicationContext applicationContext = new GenericWebApplicationContext();
        applicationContext.refresh();
        
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(
            applicationContext, 
            null
        );
        
        assertDoesNotThrow(() -> webMvcConfig.addResourceHandlers(registry));
    }
}

