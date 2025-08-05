package com.bestflix.movie.security.service;

import com.bestflix.movie.exception.EmailSendingException;
import com.bestflix.movie.security.userService.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendPasswordResetEmail_shouldSendEmailSuccessfully() {
        String email = "test@example.com";
        String token = "abc123";

        emailService.sendPasswordResetEmail(email, token);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertEquals(email, sentMessage.getTo()[0]);
        assertTrue(sentMessage.getText().contains("token=" + token));
        assertEquals("BestFlix – Password Reset", sentMessage.getSubject());
    }

    @Test
    void sendPasswordResetEmail_shouldThrowException_whenMailFails() {
        String email = "test@example.com";
        String token = "abc123";

        doThrow(new MailSendException("Simulated fail"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        EmailSendingException ex = assertThrows(EmailSendingException.class, () ->
                emailService.sendPasswordResetEmail(email, token));

        assertTrue(ex.getMessage().contains(email));
    }
}

