package com.bestflix.movie.security.userDetails;

import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @InjectMocks
    private MyUserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldReturnCustomUserDetails_whenUserExists() {
        Users mockUser = new Users();
        mockUser.setUsername("elcin");
        mockUser.setPassword("secretPassword");

        when(userRepository.findByUsername("elcin")).thenReturn(mockUser);

        UserDetails details = userDetailsService.loadUserByUsername("elcin");
        assertEquals("elcin", details.getUsername());
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("unknown"));
    }
}
