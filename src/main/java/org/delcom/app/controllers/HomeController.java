package org.delcom.app.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        // Cek apakah user sudah login
        String token = getTokenFromRequest(request);
        if (token != null) {
            User user = authService.getUserByToken(token);
            if (user != null) {
                // Jika sudah login, tambahkan informasi user ke model
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true);
            } else {
                model.addAttribute("isLoggedIn", false);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        // Tampilkan halaman index dengan informasi yang sesuai
        return "index";
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
