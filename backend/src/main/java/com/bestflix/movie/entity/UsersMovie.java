package com.bestflix.movie.entity;

import com.bestflix.movie.security.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users_movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entity mapping users to movies they have uploaded or interacted with")
public class UsersMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the user-movie relation", example = "501")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Reference to the user entity")
    private Users user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    @Schema(description = "Reference to the movie entity")
    private Movie movie;
}
