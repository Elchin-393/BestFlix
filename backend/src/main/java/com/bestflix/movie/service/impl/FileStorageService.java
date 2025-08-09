package com.bestflix.movie.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;


/**
 * Service for handling file storage operations in AWS S3.
 * <p>
 * This service provides methods to upload image and video files to designated folders
 * within a configured S3 bucket. Files are stored with unique names to prevent collisions.
 * </p>
 *
 * <h2>Configuration</h2>
 * Requires the following property to be set in your application configuration:
 * <pre>
 * cloud.aws.bucket.name=your-s3-bucket-name
 * </pre>
 *
 * <h2>Usage</h2>
 * Inject this service and call {@code saveImage(MultipartFile)} or {@code saveVideo(MultipartFile)}
 * to upload files. The returned string is the unique filename stored in S3.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * String imageName = fileStorageService.saveImage(imageFile);
 * String videoName = fileStorageService.saveVideo(videoFile);
 * }</pre>
 *
 * <h2>Folder Structure</h2>
 * - Images are stored under {@code images/}
 * - Videos are stored under {@code videos/}
 *
 * <h2>Exceptions</h2>
 * Throws {@link IOException} if the file stream cannot be read or uploaded.
 *
 * @author Elcin
 */
@Service
public class FileStorageService {


    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    /**
     * Constructs the service with an injected {@link S3Client}.
     *
     * @param s3Client AWS S3 client used for file operations
     */
    public FileStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Uploads an image file to the {@code images/} folder in the S3 bucket.
     * Generates a unique filename to avoid collisions.
     *
     * @param file the image file to upload
     * @return the unique filename stored in S3
     * @throws IOException if the file cannot be read or uploaded
     */
    public String saveImage(MultipartFile file) throws IOException {
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uniqueName = UUID.randomUUID().toString() + extension;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("images/" + uniqueName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return uniqueName;
    }


    /**
     * Uploads a video file to the {@code videos/} folder in the S3 bucket.
     * Generates a unique filename to avoid collisions.
     *
     * @param file the video file to upload
     * @return the unique filename stored in S3
     * @throws IOException if the file cannot be read or uploaded
     */
    public String saveVideo(MultipartFile file) throws IOException {
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uniqueName = UUID.randomUUID().toString() + extension;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("videos/" + uniqueName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return uniqueName;
    }
}

