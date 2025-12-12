package org.delcom.app.controllers;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.dto.HerbalMedicineForm;
import org.delcom.app.entities.HerbalMedicine;
import org.delcom.app.services.HerbalMedicineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HerbalMedicineControllerTests {
    @Mock
    private HerbalMedicineService herbalMedicineService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private HerbalMedicineController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        AuthContext.clear();
    }

    @Test
    void testList() {
        List<HerbalMedicine> medicines = Arrays.asList(new HerbalMedicine());
        when(herbalMedicineService.findAll()).thenReturn(medicines);

        String view = controller.list(model);
        assertEquals("herbal-medicines/list", view);
        verify(model).addAttribute("medicines", medicines);
    }

    @Test
    void testMyMedicines() {
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
        List<HerbalMedicine> medicines = Arrays.asList(new HerbalMedicine());
        when(herbalMedicineService.findByUserId(userId)).thenReturn(medicines);

        String view = controller.myMedicines(model);
        assertEquals("herbal-medicines/my-medicines", view);
    }

    @Test
    void testShowAddForm() {
        String view = controller.showAddForm(model);
        assertEquals("herbal-medicines/add", view);
    }

    @Test
    void testAdd() {
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
        HerbalMedicineForm form = new HerbalMedicineForm();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(herbalMedicineService.create(any(), any())).thenReturn(new HerbalMedicine());

        String view = controller.add(form, bindingResult, redirectAttributes);
        assertEquals("redirect:/herbal-medicines/my-medicines", view);
    }

    @Test
    void testDetail() {
        UUID id = UUID.randomUUID();
        HerbalMedicine medicine = new HerbalMedicine();
        when(herbalMedicineService.findById(id)).thenReturn(Optional.of(medicine));

        String view = controller.detail(id, model);
        assertEquals("herbal-medicines/detail", view);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
        doNothing().when(herbalMedicineService).delete(id, userId);

        String view = controller.delete(id, redirectAttributes);
        assertEquals("redirect:/herbal-medicines/my-medicines", view);
    }

    @Test
    void testAdd_WithException() {
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
        HerbalMedicineForm form = new HerbalMedicineForm();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(herbalMedicineService.create(any(), any())).thenThrow(new RuntimeException("Error creating"));

        String view = controller.add(form, bindingResult, redirectAttributes);
        assertEquals("redirect:/herbal-medicines/add", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Error creating"));
    }

    @Test
    void testUpdate_WithException() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
        HerbalMedicineForm form = new HerbalMedicineForm();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(herbalMedicineService.update(any(), any(), any())).thenThrow(new RuntimeException("Error updating"));

        String view = controller.update(id, form, bindingResult, redirectAttributes);
        assertEquals("redirect:/herbal-medicines/" + id + "/edit", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Error updating"));
    }

    @Test
    void testUpdate_Success() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
        HerbalMedicineForm form = new HerbalMedicineForm();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(herbalMedicineService.update(id, userId, form)).thenReturn(new HerbalMedicine());

        String view = controller.update(id, form, bindingResult, redirectAttributes);

        assertEquals("redirect:/herbal-medicines/my-medicines", view);
        verify(herbalMedicineService).update(id, userId, form);
        verify(redirectAttributes).addFlashAttribute(eq("success"), eq("Data berhasil diupdate"));
    }

    @Test
    void testDelete_WithException() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthContext.setUserId(userId);
        doThrow(new RuntimeException("Error deleting")).when(herbalMedicineService).delete(id, userId);

        String view = controller.delete(id, redirectAttributes);
        assertEquals("redirect:/herbal-medicines/my-medicines", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Error deleting"));
    }

    @Test
    void testAdd_WithValidationErrors() {
        HerbalMedicineForm form = new HerbalMedicineForm();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.add(form, bindingResult, redirectAttributes);
        assertEquals("herbal-medicines/add", view);
    }

    @Test
    void testUpdate_WithValidationErrors() {
        UUID id = UUID.randomUUID();
        HerbalMedicineForm form = new HerbalMedicineForm();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.update(id, form, bindingResult, redirectAttributes);
        assertEquals("redirect:/herbal-medicines/" + id + "/edit", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Validasi gagal"));
    }

    @Test
    void testDetail_NotFound() {
        UUID id = UUID.randomUUID();
        when(herbalMedicineService.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.detail(id, model));
    }

    @Test
    void testShowEditForm_NotFound() {
        UUID id = UUID.randomUUID();
        when(herbalMedicineService.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.showEditForm(id, model));
    }

    @Test
    void testShowEditForm() {
        UUID id = UUID.randomUUID();
        HerbalMedicine medicine = new HerbalMedicine();
        medicine.setId(id);
        medicine.setName("Test");
        medicine.setDescription("Desc");
        medicine.setCategory("Cat");
        medicine.setOrigin("Origin");
        medicine.setBenefits("Benefits");
        medicine.setImagePath("/path/to/image.jpg");

        when(herbalMedicineService.findById(id)).thenReturn(Optional.of(medicine));

        String view = controller.showEditForm(id, model);
        assertEquals("herbal-medicines/edit", view);
        verify(model).addAttribute(eq("herbalMedicineForm"), any());
        verify(model).addAttribute("medicineId", id);
        verify(model).addAttribute("currentImage", "/path/to/image.jpg");
    }
}

