package com.test.demo.serviceImpl;

import com.test.demo.model.User;
import com.test.demo.repository.UserRepository;
import com.test.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Environment env;

    @Override
    public void sendPasswordResetEmail(User user) {
        String token = generateToken();
        user.setResetToken(token);
        userRepository.save(user);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        // Envoyer l'email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(env.getProperty("spring.mail.username"));
        message.setTo(user.getEmail());
        message.setSubject("Réinitialisation du mot de passe");
        message.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + resetLink);
        mailSender.send(message);
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user != null) {
            // Réinitialiser le mot de passe et effacer le token
            user.setMotDePasse(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public User save(User user) {
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private String generateToken() {
        // Générer un token unique, par exemple en utilisant UUID.randomUUID().toString()
        return UUID.randomUUID().toString();
    }
}
