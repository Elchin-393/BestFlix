package com.bestflix.movie.security.userDetails;

import com.bestflix.movie.security.entity.Users;
import com.bestflix.movie.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 * Responsible for loading user details from the database during authentication.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Fetches user details from the database using the provided username.
     *
     * @param username the username input from login
     * @return a {@link UserDetails} implementation representing the authenticated user
     * @throws UsernameNotFoundException if the user does not exist in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = userRepository.findByUsername(username);

        if(user == null){
            System.out.println("User Not Found: " + username);
            throw new UsernameNotFoundException("User not Found");
        }

        return new CustomUserDetails(user);
    }
}
