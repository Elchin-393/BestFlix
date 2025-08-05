package com.bestflix.movie.security.userService;

import com.bestflix.movie.exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications to users.
 * Primarily used for dispatching password reset instructions.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Sends a password reset email containing a link with a JWT token.
     *
     * @param toEmail the recipient's email address
     * @param token   the JWT token embedded in the reset link
     * @throws EmailSendingException if the mail fails to dispatch
     */
    public void sendPasswordResetEmail(String toEmail, String token) {

        try{
        String resetLink = "http://127.0.0.1:5500/html/reset-password.html?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("BestFlix – Password Reset");
        message.setText("To reset your password, click the link below. This link will expire in 30 minutes.\n" + resetLink);

        mailSender.send(message);
    } catch (MailException ex) {
        throw new EmailSendingException("Failed to send password reset email to: " + toEmail, ex);
    }

}

}

