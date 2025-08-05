package com.bestflix.movie.service;

import com.bestflix.movie.service.impl.VideoFileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoFileStorageServiceTest {

    @TempDir
    File tempDir;

    @Test
    void save_shouldStoreFileInTempDirectory() throws IOException {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile(
                "video", "sample.mp4", "video/mp4", "random content".getBytes()
        );

        VideoFileStorageService service = new VideoFileStorageService() {
            @Override
            public String save(MultipartFile file) throws IOException {
                File directory = tempDir;
                if (!directory.exists()) directory.mkdirs();

                String extension = file.getOriginalFilename()
                        .substring(file.getOriginalFilename().lastIndexOf("."));
                String uniqueName = UUID.randomUUID().toString() + extension;

                Path destination = Paths.get(directory.getAbsolutePath()).resolve(uniqueName);
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

                return destination.toString();
            }
        };

        // Act
        String savedPath = service.save(multipartFile);

        // Assert
        assertTrue(savedPath.endsWith(".mp4"));
        assertTrue(Files.exists(Paths.get(savedPath)));
        assertEquals("random content", Files.readString(Paths.get(savedPath)));
    }
}
