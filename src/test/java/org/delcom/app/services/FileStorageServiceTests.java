package org.delcom.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTests {
    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
    }

    @Test
    void testStoreFile() throws IOException {
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes());
        String result = fileStorageService.storeFile(file);
        assertNotNull(result);
        assertTrue(result.contains("test.jpg") || result.contains(".jpg"));
    }

    @Test
    void testStoreFileNull() throws IOException {
        String result = fileStorageService.storeFile(null);
        assertNull(result);
    }

    @Test
    void testStoreFileEmpty() throws IOException {
        MultipartFile file = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);
        String result = fileStorageService.storeFile(file);
        assertNull(result);
    }

    @Test
    void testDeleteFile() {
        assertDoesNotThrow(() -> fileStorageService.deleteFile("/path/to/file.jpg"));
    }

    @Test
    void testDeleteFileNull() {
        assertDoesNotThrow(() -> fileStorageService.deleteFile(null));
    }

    @Test
    void testStoreFileWithNullOriginalFilename() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(null);
        String result = fileStorageService.storeFile(file);
        assertNull(result);
    }

    @Test
    void testStoreFileWithoutExtension() throws IOException {
        MultipartFile file = new MockMultipartFile("image", "testfile", "image/jpeg", "test content".getBytes());
        String result = fileStorageService.storeFile(file);
        assertNotNull(result);
        assertTrue(result.contains("/uploads/"));
    }

    @Test
    void testStoreFileWithDotAtStart() throws IOException {
        MultipartFile file = new MockMultipartFile("image", ".hidden", "image/jpeg", "test content".getBytes());
        String result = fileStorageService.storeFile(file);
        assertNotNull(result);
        assertTrue(result.contains("/uploads/"));
    }

    @Test
    void testDeleteFileWithExistingFile() throws IOException {
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes());
        String storedPath = fileStorageService.storeFile(file);
        assertNotNull(storedPath);
        
        assertDoesNotThrow(() -> fileStorageService.deleteFile(storedPath));
    }

    @Test
    void testDeleteFileWithExistingAbsolutePath() throws IOException {
        Path actualFile = Files.createFile(tempDir.resolve("to-delete.txt"));
        assertTrue(Files.exists(actualFile));

        fileStorageService.deleteFile(actualFile.toString());

        assertFalse(Files.exists(actualFile));
    }

    @Test
    void testDeleteFileWithEmptyString() {
        assertDoesNotThrow(() -> fileStorageService.deleteFile(""));
    }

    @Test
    void testDeleteFileWithoutLeadingSlash() throws IOException {
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes());
        String storedPath = fileStorageService.storeFile(file);
        assertNotNull(storedPath);
        
        // Remove leading slash
        String pathWithoutSlash = storedPath.substring(1);
        assertDoesNotThrow(() -> fileStorageService.deleteFile(pathWithoutSlash));
    }

    @Test
    void testDeleteFileWhenFileDoesNotExist() {
        assertDoesNotThrow(() -> fileStorageService.deleteFile("/uploads/nonexistent.jpg"));
    }

    @Test
    void testDeleteFileWithIOException() throws IOException {
        // Create a file that will cause IOException when trying to delete
        // We'll use a path that exists but might cause issues
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes());
        String storedPath = fileStorageService.storeFile(file);
        assertNotNull(storedPath);
        
        // Delete it first
        fileStorageService.deleteFile(storedPath);
        
        // Try to delete again (should not throw, just handle gracefully)
        assertDoesNotThrow(() -> fileStorageService.deleteFile(storedPath));
    }

    @Test
    void testDeleteFile_WhenFileDoesNotExist() {
        // Test deleteFile when file doesn't exist (Files.exists returns false)
        assertDoesNotThrow(() -> fileStorageService.deleteFile("/uploads/nonexistent.jpg"));
    }

    @Test
    void testDeleteFile_WithIOExceptionInCatch() throws IOException {
        // Test that IOException in deleteFile is caught and doesn't throw
        // We'll create a file and then try to delete with a path that might cause IOException
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes());
        String storedPath = fileStorageService.storeFile(file);
        assertNotNull(storedPath);
        
        // Delete the file normally first
        fileStorageService.deleteFile(storedPath);
        
        // Now try to delete a non-existent file (should not throw)
        assertDoesNotThrow(() -> fileStorageService.deleteFile("/uploads/nonexistent-file-12345.jpg"));
    }

    @Test
    void testDeleteFile_WithIOExceptionFromDelete() {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            filesMock.when(() -> Files.delete(any(Path.class))).thenThrow(new IOException("fail delete"));

            assertDoesNotThrow(() -> fileStorageService.deleteFile("/uploads/test.jpg"));

            filesMock.verify(() -> Files.exists(any(Path.class)));
            filesMock.verify(() -> Files.delete(any(Path.class)));
        }
    }

    @Test
    void testStoreFileWhenDirectoryExists() throws IOException {
        // First call creates directory
        MultipartFile file1 = new MockMultipartFile("image", "test1.jpg", "image/jpeg", "test content".getBytes());
        String result1 = fileStorageService.storeFile(file1);
        assertNotNull(result1);
        
        // Second call should use existing directory
        MultipartFile file2 = new MockMultipartFile("image", "test2.jpg", "image/jpeg", "test content 2".getBytes());
        String result2 = fileStorageService.storeFile(file2);
        assertNotNull(result2);
    }

    @Test
    void testStoreFileCreatesDirectoryWhenMissing() throws IOException {
        Path newDir = tempDir.resolve("nestedUploads");
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", newDir.toString());

        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes());

        String result = fileStorageService.storeFile(file);

        assertNotNull(result);
        assertTrue(Files.exists(newDir));
    }
}

