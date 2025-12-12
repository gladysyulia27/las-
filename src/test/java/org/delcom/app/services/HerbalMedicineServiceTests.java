package org.delcom.app.services;

import org.delcom.app.dto.HerbalMedicineForm;
import org.delcom.app.entities.HerbalMedicine;
import org.delcom.app.repositories.HerbalMedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HerbalMedicineServiceTests {
    @Mock
    private HerbalMedicineRepository herbalMedicineRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private HerbalMedicineService herbalMedicineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<HerbalMedicine> medicines = Arrays.asList(new HerbalMedicine(), new HerbalMedicine());
        when(herbalMedicineRepository.findAll()).thenReturn(medicines);

        List<HerbalMedicine> result = herbalMedicineService.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testFindByUserId() {
        UUID userId = UUID.randomUUID();
        List<HerbalMedicine> medicines = Arrays.asList(new HerbalMedicine());
        when(herbalMedicineRepository.findByUserId(userId)).thenReturn(medicines);

        List<HerbalMedicine> result = herbalMedicineService.findByUserId(userId);
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        HerbalMedicine medicine = new HerbalMedicine();
        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(medicine));

        Optional<HerbalMedicine> result = herbalMedicineService.findById(id);
        assertTrue(result.isPresent());
    }

    @Test
    void testCreate() throws Exception {
        UUID userId = UUID.randomUUID();
        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("Jahe");
        form.setDescription("Deskripsi");
        form.setCategory("Rempah");
        form.setOrigin("Indonesia");
        form.setBenefits("Manfaat");
        form.setImage(new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]));

        when(fileStorageService.storeFile(any())).thenReturn("/path/to/image.jpg");
        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.create(userId, form);
        assertNotNull(result);
        assertEquals("Jahe", result.getName());
    }

    @Test
    void testUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine existing = new HerbalMedicine();
        existing.setId(id);
        existing.setUserId(userId);
        existing.setName("Old Name");

        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("New Name");
        form.setDescription("New Desc");
        form.setCategory("New Cat");
        form.setOrigin("New Origin");
        form.setBenefits("New Benefits");

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(existing));
        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.update(id, userId, form);
        assertEquals("New Name", result.getName());
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine medicine = new HerbalMedicine();
        medicine.setId(id);
        medicine.setUserId(userId);

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(medicine));
        doNothing().when(herbalMedicineRepository).delete(medicine);

        assertDoesNotThrow(() -> herbalMedicineService.delete(id, userId));
    }

    @Test
    void testCreate_WithException() throws Exception {
        UUID userId = UUID.randomUUID();
        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("Jahe");
        form.setImage(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes()));

        when(fileStorageService.storeFile(any())).thenThrow(new IOException("Storage error"));
        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> herbalMedicineService.create(userId, form));
        assertTrue(exception.getMessage().contains("Gagal menyimpan data"));
    }

    @Test
    void testCreate_WithNonEmptyImage() throws Exception {
        UUID userId = UUID.randomUUID();
        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("Jahe");
        form.setDescription("Deskripsi");
        form.setCategory("Rempah");
        form.setOrigin("Indonesia");
        form.setBenefits("Manfaat");
        // Non-empty content to ensure isEmpty() is false and storeFile is called
        form.setImage(new MockMultipartFile("image", "test.jpg", "image/jpeg", "img bytes".getBytes()));

        when(fileStorageService.storeFile(any())).thenReturn("/path/to/image.jpg");
        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.create(userId, form);

        assertNotNull(result);
        assertEquals("/path/to/image.jpg", result.getImagePath());
        verify(fileStorageService).storeFile(any());
    }

    @Test
    void testUpdate_WithException() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine existing = new HerbalMedicine();
        existing.setId(id);
        existing.setUserId(userId);
        existing.setImagePath("/old/path.jpg");
        existing.setName("Old Name");

        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("New Name");
        form.setDescription("New Desc");
        form.setCategory("New Cat");
        form.setOrigin("New Origin");
        form.setBenefits("New Benefits");
        // Use non-empty content so isEmpty() returns false and enters the try block
        form.setImage(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes()));

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(existing));
        when(fileStorageService.storeFile(any())).thenThrow(new IOException("Storage error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> herbalMedicineService.update(id, userId, form));
        assertTrue(exception.getMessage().contains("Gagal mengupdate gambar"));
    }

    @Test
    void testCreate_WithoutImage() throws Exception {
        UUID userId = UUID.randomUUID();
        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("Jahe");
        form.setImage(null);

        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.create(userId, form);
        assertNotNull(result);
        assertNull(result.getImagePath());
    }

    @Test
    void testCreate_WithEmptyImage() throws Exception {
        UUID userId = UUID.randomUUID();
        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("Jahe");
        form.setImage(new MockMultipartFile("image", "empty.jpg", "image/jpeg", new byte[0]));

        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.create(userId, form);

        assertNotNull(result);
        assertNull(result.getImagePath());
        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    void testUpdate_WithImagePath() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine existing = new HerbalMedicine();
        existing.setId(id);
        existing.setUserId(userId);
        existing.setImagePath("/old/path.jpg");
        existing.setName("Old Name");

        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("New Name");
        form.setDescription("New Desc");
        form.setCategory("New Cat");
        form.setOrigin("New Origin");
        form.setBenefits("New Benefits");
        // Use non-empty content so isEmpty() returns false
        form.setImage(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes()));

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(existing));
        when(fileStorageService.storeFile(any())).thenReturn("/new/path.jpg");
        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.update(id, userId, form);
        // The existing object is modified in place, so check the existing object
        assertEquals("/new/path.jpg", existing.getImagePath());
        assertEquals("/new/path.jpg", result.getImagePath());
        verify(fileStorageService).deleteFile("/old/path.jpg");
    }

    @Test
    void testDelete_WithImagePath() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine medicine = new HerbalMedicine();
        medicine.setId(id);
        medicine.setUserId(userId);
        medicine.setImagePath("/path/to/image.jpg");

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(medicine));
        doNothing().when(herbalMedicineRepository).delete(medicine);

        assertDoesNotThrow(() -> herbalMedicineService.delete(id, userId));
        verify(fileStorageService).deleteFile("/path/to/image.jpg");
    }

    @Test
    void testDelete_WithoutImagePath() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine medicine = new HerbalMedicine();
        medicine.setId(id);
        medicine.setUserId(userId);
        medicine.setImagePath(null);

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(medicine));
        doNothing().when(herbalMedicineRepository).delete(medicine);

        assertDoesNotThrow(() -> herbalMedicineService.delete(id, userId));
        verify(fileStorageService, never()).deleteFile(any());
    }

    @Test
    void testUpdate_WithoutImagePath() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine existing = new HerbalMedicine();
        existing.setId(id);
        existing.setUserId(userId);
        existing.setImagePath(null);
        existing.setName("Old Name");

        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("New Name");
        form.setDescription("New Desc");
        form.setCategory("New Cat");
        form.setOrigin("New Origin");
        form.setBenefits("New Benefits");
        form.setImage(new MockMultipartFile("image", "test.jpg", "image/jpeg", "test content".getBytes()));

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(existing));
        when(fileStorageService.storeFile(any())).thenReturn("/new/path.jpg");
        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.update(id, userId, form);
        assertEquals("/new/path.jpg", result.getImagePath());
        verify(fileStorageService, never()).deleteFile(any());
    }

    @Test
    void testUpdate_WithoutNewImage() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine existing = new HerbalMedicine();
        existing.setId(id);
        existing.setUserId(userId);
        existing.setImagePath("/old/path.jpg");
        existing.setName("Old Name");

        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("New Name");
        form.setDescription("New Desc");
        form.setCategory("New Cat");
        form.setOrigin("New Origin");
        form.setBenefits("New Benefits");
        form.setImage(null);

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(existing));
        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.update(id, userId, form);
        assertEquals("/old/path.jpg", result.getImagePath());
        verify(fileStorageService, never()).storeFile(any());
        verify(fileStorageService, never()).deleteFile(any());
    }

    @Test
    void testUpdate_WithEmptyImage() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicine existing = new HerbalMedicine();
        existing.setId(id);
        existing.setUserId(userId);
        existing.setImagePath("/old/path.jpg");
        existing.setName("Old Name");

        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName("New Name");
        form.setDescription("New Desc");
        form.setCategory("New Cat");
        form.setOrigin("New Origin");
        form.setBenefits("New Benefits");
        form.setImage(new MockMultipartFile("image", "empty.jpg", "image/jpeg", new byte[0]));

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(existing));
        when(herbalMedicineRepository.save(any(HerbalMedicine.class))).thenAnswer(i -> i.getArguments()[0]);

        HerbalMedicine result = herbalMedicineService.update(id, userId, form);

        assertEquals("/old/path.jpg", result.getImagePath());
        verify(fileStorageService, never()).storeFile(any());
        verify(fileStorageService, never()).deleteFile(any());
    }

    @Test
    void testUpdate_NotFound() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        HerbalMedicineForm form = new HerbalMedicineForm();

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> herbalMedicineService.update(id, userId, form));
    }

    @Test
    void testUpdate_Unauthorized() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        HerbalMedicine existing = new HerbalMedicine();
        existing.setId(id);
        existing.setUserId(otherUserId);
        HerbalMedicineForm form = new HerbalMedicineForm();

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> herbalMedicineService.update(id, userId, form));
    }

    @Test
    void testDelete_NotFound() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> herbalMedicineService.delete(id, userId));
    }

    @Test
    void testDelete_Unauthorized() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        HerbalMedicine medicine = new HerbalMedicine();
        medicine.setId(id);
        medicine.setUserId(otherUserId);

        when(herbalMedicineRepository.findById(id)).thenReturn(Optional.of(medicine));

        assertThrows(RuntimeException.class, () -> herbalMedicineService.delete(id, userId));
    }
}

