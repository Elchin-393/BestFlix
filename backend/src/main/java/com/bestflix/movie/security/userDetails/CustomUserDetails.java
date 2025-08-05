package com.bestflix.movie.security.userDetails;

import com.bestflix.movie.security.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of Spring Security's {@link UserDetails} interface.
 * Wraps a {@link Users} entity and exposes its authentication-related properties.
 */
public class CustomUserDetails implements UserDetails {

    private final Users user;

    /**
     * Constructs a CustomUserDetails wrapper around a {@link Users} entity.
     *
     * @param user the authenticated application user
     */
    public CustomUserDetails(Users user) {
        this.user = user;
    }

    /**
     * @return username used for login
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * @return hashed user password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * @return granted authorities for this user; currently defaults to role USER
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    /**
     * Indicates whether the user's account has expired.
     * Always returns true (non-expired).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * Always returns true (non-locked).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) are expired.
     * Always returns true (valid).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     * Always returns true.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
