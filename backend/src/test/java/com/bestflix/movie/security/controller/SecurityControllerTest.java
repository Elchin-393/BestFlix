package com.bestflix.movie.security.controller;

import com.bestflix.movie.entity.Movie;
import com.bestflix.movie.exception.UserMoviesNotFoundException;
import com.bestflix.movie.handler.GlobalExceptionHandler;
import com.bestflix.movie.security.SecurityController;
import com.bestflix.movie.security.dto.ForgotPasswordRequest;
import com.bestflix.movie.security.dto.ResetPasswordRequest;
import com.bestflix.movie.security.dto.UsersRequest;
import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.userService.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private SecurityController securityController;


    @BeforeEach
    void setup(){
        SecurityController securityController = new SecurityController(userService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(securityController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown(){

    }

    @Test
    void should_return_UserRegisteredSuccessfully() throws Exception {
        Users user = new Users();
        user.setUsername("elcin");
        user.setPassword("securePassword");
        user.setEmail("elcin@gmail.com");

        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);


        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(userService).register(user);


    }

    @Test
    void shouldReturnBadRequestForMissingFields() throws Exception {

        Users user = new Users();

        String userJson = new ObjectMapper().writeValueAsString(user);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails.message").value("Unknown Error: " +
                        "password: Password cannot be null, email: Email cannot be null, " +
                        "username: Username cannot be empty, email: Email cannot be empty, " +
                        "username: Username cannot be null, password: Password cannot be empty"))
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.errorDetails.errorCode").value("900"));

        verify(userService, never()).register(any());
    }


    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        UsersRequest user = new UsersRequest();
        user.setUsername("elcin");
        user.setPassword("securePassword");

        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);

        String token = "token";

        when(userService.verify(user)).thenReturn(token);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(userService).verify(any(UsersRequest.class));

    }

    @Test
    void shouldReturnBadRequestMissingFieldsForLoginUser() throws Exception {

        UsersRequest user = new UsersRequest();

        String userJson = new ObjectMapper().writeValueAsString(user);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails.message").value("Unknown Error: " +
                        "password: Password cannot be empty, username: Username cannot be empty, " +
                        "password: Password cannot be null, username: Username cannot be null"))
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.errorDetails.errorCode").value("900"));

        verify(userService, never()).verify(any());
    }

    @Test
    void shouldGenerateResetTokenSuccessfully() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("elcin@gmail.com");

        String email = request.getEmail();

        String token = "token";

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        when(userService.generateResetToken(email)).thenReturn(token);

        mockMvc.perform(post("/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Reset link sent if email exists"));

        verify(userService).generateResetToken(any(String.class));

    }

    @Test
    void shouldReturnBadRequestForMissingEmail() throws Exception {

        ForgotPasswordRequest request = new ForgotPasswordRequest();

        String requestJson = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails.message").value("Unknown Error: " +
                "email: Email cannot be null, email: Email is required"))
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.errorDetails.errorCode").value("900"));


        verify(userService, never()).generateResetToken(any(String.class));
    }

    @Test
    void shouldResetPasswordSuccessfully() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("myToken");
        request.setNewPassword("123456");

        String token = request.getToken();
        String newPassword = request.getNewPassword();


        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        when(userService.resetPassword(token, newPassword)).thenReturn(true);

        mockMvc.perform(post("/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Password successfully reset"));

        verify(userService).resetPassword(token, newPassword);

    }

    @Test
    void shouldReturnBadRequestWhenResetPassword() throws Exception {

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("myToken");
//        request.setNewPassword("1234"); Edge case for char<6

        String token = request.getToken();
        String newPassword = request.getNewPassword();

        String requestJson = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorDetails.message").value("Unknown Error: " +
                "newPassword: Password cannot be empty, newPassword: Password cannot be null"))
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.errorDetails.errorCode").value("900"));

        verify(userService, never()).resetPassword(token, newPassword);
    }

}
