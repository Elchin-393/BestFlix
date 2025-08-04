package com.bestflix.movie.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entity representing a movie's metadata, media files, and classification")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique movie identifier", example = "1001")
    private long id;

    @Schema(description = "Title of the movie", example = "Interstellar")
    private String movieName;

    @Schema(description = "Country of origin", example = "USA")
    private String country;

    @Schema(description = "Release date of the movie", example = "2014-11-07")
    private LocalDate releaseDate;

    @Schema(description = "Cast members in the movie", example = "Matthew McConaughey, Anne Hathaway")
    private String casts;

    @Schema(description = "Duration of the movie", example = "2h 49m")
    private String duration;

    @Column(length = 1000)
    @Schema(description = "Synopsis or description of the movie", example = "A team of explorers travel through a wormhole in space...")
    private String about;

    @Schema(description = "Genre/category of the movie", example = "Science Fiction")
    private String category;

    @Schema(description = "Name of the uploaded image file", example = "interstellar-poster.jpg")
    private String imageName;

    @Schema(description = "Image MIME type", example = "image/jpeg")
    private String imageType;

    @Schema(description = "Name of the uploaded video file", example = "interstellar-trailer.mp4")
    private String videoName;

    @Schema(description = "Video MIME type", example = "video/mp4")
    private String videoType;

    @Column(name = "image_data")
    @Schema(description = "Binary data for the image")
    private byte[] imageData;

    @Column(name = "video_path")
    @Schema(description = "Server path to the video file", example = "/videos/interstellar-trailer.mp4")
    private String videoPath;
}