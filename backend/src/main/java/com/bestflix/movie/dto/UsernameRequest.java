package com.bestflix.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Request object containing a username for user-specific queries")
public class UsernameRequest {

    @Schema(description = "Username of the user", example = "Elcin.dev")
    private String username;
}
