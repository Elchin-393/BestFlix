package com.bestflix.movie.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


/**
 * Handles generation of unique file names for uploaded files, ensuring filename consistency and uniqueness.
 *
 * <p><strong>Workflow:</strong></p>
 * <ul>
 *   <li>Extracts the file extension from the uploaded MultipartFile.</li>
 *   <li>Generates a UUID-based unique name and appends the extracted extension.</li>
 *   <li>Returns the unique filename string (without storing to disk).</li>
 * </ul>
 *
 * @param file the uploaded file whose name is to be transformed
 * @return a uniquely generated file name string with extension
 * @throws IOException if file metadata cannot be read or processed
 */
@Service
public class FileStorageService {


    public String save(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String uniqueName = UUID.randomUUID().toString() + extension;

        return uniqueName.toString();

    }
}

