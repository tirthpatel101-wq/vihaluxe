package com.vihaluxe.service;

import org.springframework.transaction.annotation.Transactional;
import com.vihaluxe.model.PasswordResetToken;
import com.vihaluxe.model.User;
import com.vihaluxe.repository.PasswordResetTokenRepository;
import com.vihaluxe.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void createResetToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return;
        }

        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );

        tokenRepository.save(resetToken);

        String resetLink =
                "http://172.20.10.3:8080/reset-password?token="
                        + token;

        emailService.sendPasswordResetEmail(
                email,
                resetLink
        );
    }

    public boolean isValidToken(String token) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElse(null);

        if (resetToken == null) {
            return false;
        }

        return resetToken.getExpiryDate()
                .isAfter(LocalDateTime.now());
    }

    public void resetPassword(
            String token,
            String newPassword) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid reset token"
                                ));

        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Reset token has expired"
            );
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}