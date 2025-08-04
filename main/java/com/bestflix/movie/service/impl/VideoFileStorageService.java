package com.bestflix.movie.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


/**
 * Persists uploaded video files to a specified directory on disk using unique filenames.
 *
 * <p><strong>Workflow:</strong></p>
 * <ul>
 *   <li>Ensures the target storage directory exists (creates if missing).</li>
 *   <li>Extracts the file extension and generates a UUID-based unique name.</li>
 *   <li>Copies the file contents to the target path using REPLACE_EXISTING mode.</li>
 *   <li>Returns the full path string where the video file was stored.</li>
 * </ul>
 *
 * @param file the video file to be saved locally
 * @return absolute path to the stored video file on disk
 * @throws IOException if reading or writing the file fails
 */
@Service
public class VideoFileStorageService {

    private final String STORAGE_DIR = "C:/Users/User/Desktop/BestFlix/video";

    public String save(MultipartFile file) throws IOException {
        File directory = new File(STORAGE_DIR);
        if (!directory.exists()) directory.mkdirs();

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uniqueName = UUID.randomUUID().toString() + extension;

        Path destination = Paths.get(STORAGE_DIR).resolve(uniqueName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return destination.toString();
    }
}
