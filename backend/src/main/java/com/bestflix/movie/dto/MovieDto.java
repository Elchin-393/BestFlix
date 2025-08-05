package com.bestflix.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Movie data transfer object representing core movie metadata and media content")
public class MovieDto {

    @Schema(description = "Unique identifier of the movie", example = "101")
    private long id;

    @Schema(description = "Name of the movie", example = "Inception")
    private String movieName;

    @Schema(description = "Country where the movie was produced", example = "USA")
    private String country;

    @Schema(description = "Release date of the movie", example = "2010-07-16")
    private LocalDate releaseDate;

    @Schema(description = "List of cast members", example = "Leonardo DiCaprio, Joseph Gordon-Levitt")
    private String casts;

    @Schema(description = "Duration of the movie", example = "2h 28m")
    private String duration;

    @Schema(description = "Brief description or synopsis", example = "A skilled thief enters dreams to extract secrets.")
    private String about;

    @Schema(description = "Genre or category of the movie", example = "Sci-Fi")
    private String category;

    @Schema(description = "Image filename", example = "inception-poster.jpg")
    private String imageName;

    @Schema(description = "Image MIME type", example = "image/jpeg")
    private String imageType;

    @Schema(description = "Video filename", example = "inception.mp4")
    private String videoName;

    @Schema(description = "Video MIME type", example = "video/mp4")
    private String videoType;

    @Schema(description = "Byte array containing image data")
    private byte[] imageData;

    @Schema(description = "Byte array containing video data")
    private byte[] videoData;
}
