package com.test.demo.controller;

import com.test.demo.model.User;
import com.test.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST, headers = "accept=Application/json")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            user = this.userService.save(user);
        } catch (Exception e) {
            System.out.println("Erreur " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("erreur lors de l'enregistrement : " + e.getMessage());
        }
        return ResponseEntity.ok(user);
    }

    @RequestMapping(value = "/request-reset-password", method = RequestMethod.POST, headers = "accept=Application/json")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            userService.sendPasswordResetEmail(user);
            return ResponseEntity.ok("Email de réinitialisation du mot de passe envoyé.");
        } else {
            return ResponseEntity.badRequest().body("Utilisateur introuvable.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        if (userService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
        } else {
            return ResponseEntity.badRequest().body("Token invalide.");
        }
    }
}
