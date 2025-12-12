package org.delcom.app.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, 
                       HttpServletResponse response, Model model) {
        if (result.hasErrors()) {
            return "auth/login";
        }

        try {
            String token = authService.login(loginForm);
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);
            return "redirect:/herbal-medicines";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm registerForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(registerForm);
            model.addAttribute("success", "Registrasi berhasil. Silakan login.");
            model.addAttribute("loginForm", new LoginForm());
            return "auth/login";
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Terjadi kesalahan saat registrasi. Silakan coba lagi.";
            }
            model.addAttribute("error", errorMessage);
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}

