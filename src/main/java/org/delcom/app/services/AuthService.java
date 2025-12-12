package org.delcom.app.services;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.AuthTokenRepository;
import org.delcom.app.repositories.UserRepository;
import org.delcom.app.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterForm form) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public String login(LoginForm form) {
        Optional<User> userOpt = userRepository.findByEmail(form.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email atau password salah");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email atau password salah");
        }

        String token = jwtHelper.generateToken(user.getId());
        
        authTokenRepository.deleteByUserId(user.getId());
        
        AuthToken authToken = new AuthToken(token, user.getId());
        authTokenRepository.save(authToken);

        return token;
    }

    @Transactional
    public void logout(String token) {
        authTokenRepository.findByToken(token).ifPresent(authTokenRepository::delete);
    }

    public User getUserByToken(String token) {
        if (!jwtHelper.validateToken(token)) {
            return null;
        }

        Optional<AuthToken> authTokenOpt = authTokenRepository.findByToken(token);
        if (authTokenOpt.isEmpty()) {
            return null;
        }

        UUID userId = authTokenOpt.get().getUserId();
        return userRepository.findById(userId).orElse(null);
    }
}

