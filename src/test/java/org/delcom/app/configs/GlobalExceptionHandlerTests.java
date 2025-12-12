package org.delcom.app.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTests {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleException_WithNullMessage() {
        Exception e = new Exception();
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertEquals("Error", result.getModel().get("error"));
        assertEquals("Terjadi kesalahan. Silakan coba lagi.", result.getModel().get("message"));
    }

    @Test
    void testHandleException_WithEmptyMessage() {
        Exception e = new Exception("");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertEquals("Error", result.getModel().get("error"));
        assertEquals("Terjadi kesalahan. Silakan coba lagi.", result.getModel().get("message"));
    }

    @Test
    void testHandleException_WithDatabaseError() {
        Exception e = new Exception("could not connect to database");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertEquals("Error", result.getModel().get("error"));
        assertTrue(result.getModel().get("message").toString().contains("database"));
    }

    @Test
    void testHandleException_WithConnectionError() {
        Exception e = new Exception("connection failed");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertEquals("Error", result.getModel().get("error"));
        assertTrue(result.getModel().get("message").toString().contains("database"));
    }

    @Test
    void testHandleException_WithDuplicateEmail() {
        Exception e = new Exception("email duplicate");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertEquals("Error", result.getModel().get("error"));
        assertTrue(result.getModel().get("message").toString().contains("Email"));
    }

    @Test
    void testHandleException_WithNormalMessage() {
        Exception e = new Exception("Test error message");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertEquals("Error", result.getModel().get("error"));
        assertEquals("Test error message", result.getModel().get("message"));
    }

    @Test
    void testHandleException_WithDatabaseKeyword() {
        Exception e = new Exception("database error occurred");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertTrue(result.getModel().get("message").toString().contains("database"));
    }

    @Test
    void testHandleException_WithConnectionKeyword() {
        Exception e = new Exception("connection problem");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertTrue(result.getModel().get("message").toString().contains("database"));
    }

    @Test
    void testHandleException_WithCouldNotConnectKeyword() {
        Exception e = new Exception("could not connect to remote service");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertTrue(result.getModel().get("message").toString().contains("Tidak dapat terhubung"));
    }

    @Test
    void testHandleException_WithEmailButNotDuplicate() {
        Exception e = new Exception("email is invalid");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertEquals("email is invalid", result.getModel().get("message"));
    }

    @Test
    void testHandleException_WithDuplicateButNotEmail() {
        Exception e = new Exception("duplicate entry found");
        ModelAndView result = globalExceptionHandler.handleException(e);
        assertNotNull(result);
        assertEquals("error", result.getViewName());
        assertEquals("duplicate entry found", result.getModel().get("message"));
    }
}

