package com.bestflix.movie.service;

import com.bestflix.movie.service.impl.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private FileStorageService fileStorageService;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() throws Exception {
        fileStorageService = new FileStorageService(s3Client);

        Field bucketField = FileStorageService.class.getDeclaredField("bucketName");
        bucketField.setAccessible(true);
        bucketField.set(fileStorageService, "test-bucket");
    }

    @Test
    void shouldSaveImageToS3AndReturnUniqueName() throws IOException {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        String originalFilename = "poster.png";
        String contentType = "image/png";
        byte[] fileBytes = "dummy image".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getInputStream()).thenReturn(inputStream);
        when(mockFile.getSize()).thenReturn((long) fileBytes.length);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

        // When
        String result = fileStorageService.saveImage(mockFile);

        // Then
        verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest request = requestCaptor.getValue();
        assertEquals(bucketName, request.bucket());
        assertTrue(request.key().startsWith("images/"));
        assertTrue(request.key().endsWith(".png"));
        assertEquals(contentType, request.contentType());

        assertNotNull(result);
        assertTrue(result.endsWith(".png"));
    }

    @Test
    void shouldSaveVideoToS3AndReturnUniqueName() throws IOException {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        String originalFilename = "trailer.mp4";
        String contentType = "video/mp4";
        byte[] fileBytes = "fake video".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getInputStream()).thenReturn(inputStream);
        when(mockFile.getSize()).thenReturn((long) fileBytes.length);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

        // When
        String result = fileStorageService.saveVideo(mockFile);

        // Then
        verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest request = requestCaptor.getValue();
        assertEquals(bucketName, request.bucket());
        assertTrue(request.key().startsWith("videos/"));
        assertTrue(request.key().endsWith(".mp4"));
        assertEquals(contentType, request.contentType());

        assertNotNull(result);
        assertTrue(result.endsWith(".mp4"));
    }

    @Test
    void shouldThrowIOExceptionWhenFileStreamFails() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("fail.mp4");
        when(mockFile.getInputStream()).thenThrow(new IOException("Stream error"));

        assertThrows(IOException.class, () -> fileStorageService.saveVideo(mockFile));
    }
}
