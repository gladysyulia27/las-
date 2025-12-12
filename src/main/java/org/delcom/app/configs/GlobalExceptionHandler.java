package org.delcom.app.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        logger.error("Unhandled exception: {}", e.getMessage(), e);
        
        ModelAndView modelAndView = new ModelAndView("error");
        String errorMessage = e.getMessage();
        
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Terjadi kesalahan. Silakan coba lagi.";
        } else if (errorMessage.contains("database") || errorMessage.contains("connection") || 
                   errorMessage.contains("could not connect")) {
            errorMessage = "Tidak dapat terhubung ke database. Pastikan database sudah dibuat dan PostgreSQL berjalan.";
        } else if (errorMessage.contains("email") && errorMessage.contains("duplicate")) {
            errorMessage = "Email sudah terdaftar. Silakan gunakan email lain.";
        }
        
        modelAndView.addObject("error", "Error");
        modelAndView.addObject("message", errorMessage);
        return modelAndView;
    }
}

