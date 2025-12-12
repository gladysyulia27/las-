package org.delcom.app.controllers;

import org.delcom.app.repositories.HerbalMedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChartControllerTests {
    @Mock
    private HerbalMedicineRepository herbalMedicineRepository;

    @Mock
    private Model model;

    @InjectMocks
    private ChartController chartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIndex() {
        Object[] categoryRow = new Object[]{"Rempah", 5L};
        Object[] originRow = new Object[]{"Indonesia", 3L};
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(categoryRow);
        List<Object[]> originData = new ArrayList<>();
        originData.add(originRow);

        when(herbalMedicineRepository.countByCategory()).thenReturn(categoryData);
        when(herbalMedicineRepository.countByOrigin()).thenReturn(originData);

        String view = chartController.index(model);
        assertEquals("charts/index", view);
        verify(model).addAttribute(eq("categoryData"), any());
        verify(model).addAttribute(eq("originData"), any());
        verify(model).addAttribute(eq("categoryJson"), any());
        verify(model).addAttribute(eq("originJson"), any());
    }

    @Test
    void testIndex_WithNullValues() {
        Object[] categoryRow = new Object[]{null, 5L};
        Object[] originRow = new Object[]{"Indonesia", null};
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(categoryRow);
        List<Object[]> originData = new ArrayList<>();
        originData.add(originRow);

        when(herbalMedicineRepository.countByCategory()).thenReturn(categoryData);
        when(herbalMedicineRepository.countByOrigin()).thenReturn(originData);

        String view = chartController.index(model);
        assertEquals("charts/index", view);
        verify(model).addAttribute(eq("categoryData"), any());
        verify(model).addAttribute(eq("originData"), any());
    }

    @Test
    void testIndex_WithEmptyData() {
        List<Object[]> categoryData = new ArrayList<>();
        List<Object[]> originData = new ArrayList<>();

        when(herbalMedicineRepository.countByCategory()).thenReturn(categoryData);
        when(herbalMedicineRepository.countByOrigin()).thenReturn(originData);

        String view = chartController.index(model);
        assertEquals("charts/index", view);
        verify(model).addAttribute(eq("categoryData"), any());
        verify(model).addAttribute(eq("originData"), any());
        verify(model).addAttribute(eq("categoryJson"), eq("{}"));
        verify(model).addAttribute(eq("originJson"), eq("{}"));
    }

    @Test
    void testIndex_WithSpecialCharacters() {
        Object[] categoryRow = new Object[]{"Rempah\n\"Test\"", 5L};
        Object[] originRow = new Object[]{"Indonesia\t\\Test", 3L};
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(categoryRow);
        List<Object[]> originData = new ArrayList<>();
        originData.add(originRow);

        when(herbalMedicineRepository.countByCategory()).thenReturn(categoryData);
        when(herbalMedicineRepository.countByOrigin()).thenReturn(originData);

        String view = chartController.index(model);
        assertEquals("charts/index", view);
        verify(model).addAttribute(eq("categoryData"), any());
        verify(model).addAttribute(eq("originData"), any());
        verify(model).addAttribute(eq("categoryJson"), any());
        verify(model).addAttribute(eq("originJson"), any());
    }

    @Test
    void testIndex_WithNullCategory() {
        Object[] categoryRow = new Object[]{null, 5L};
        Object[] originRow = new Object[]{"Indonesia", 3L};
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(categoryRow);
        List<Object[]> originData = new ArrayList<>();
        originData.add(originRow);

        when(herbalMedicineRepository.countByCategory()).thenReturn(categoryData);
        when(herbalMedicineRepository.countByOrigin()).thenReturn(originData);

        String view = chartController.index(model);
        assertEquals("charts/index", view);
        verify(model).addAttribute(eq("categoryData"), any());
        verify(model).addAttribute(eq("originData"), any());
    }

    @Test
    void testIndex_WithNullCount() {
        Object[] categoryRow = new Object[]{"Rempah", null};
        Object[] originRow = new Object[]{"Indonesia", 3L};
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(categoryRow);
        List<Object[]> originData = new ArrayList<>();
        originData.add(originRow);

        when(herbalMedicineRepository.countByCategory()).thenReturn(categoryData);
        when(herbalMedicineRepository.countByOrigin()).thenReturn(originData);

        String view = chartController.index(model);
        assertEquals("charts/index", view);
        verify(model).addAttribute(eq("categoryData"), any());
        verify(model).addAttribute(eq("originData"), any());
    }

    @Test
    void testIndex_WithNullOriginName() {
        Object[] categoryRow = new Object[]{"Rempah", 2L};
        Object[] originRow = new Object[]{null, 3L};
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(categoryRow);
        List<Object[]> originData = new ArrayList<>();
        originData.add(originRow);

        when(herbalMedicineRepository.countByCategory()).thenReturn(categoryData);
        when(herbalMedicineRepository.countByOrigin()).thenReturn(originData);

        String view = chartController.index(model);
        assertEquals("charts/index", view);
        verify(model).addAttribute(eq("categoryData"), any());
        verify(model).addAttribute(eq("originData"), any());
        verify(model).addAttribute(eq("originJson"), eq("{}"));
    }

    @Test
    void testIndex_WithMultipleEntries() {
        Object[] categoryRow1 = new Object[]{"Rempah", 5L};
        Object[] categoryRow2 = new Object[]{"Buah", 3L};
        Object[] originRow1 = new Object[]{"Indonesia", 4L};
        Object[] originRow2 = new Object[]{"Malaysia", 2L};
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(categoryRow1);
        categoryData.add(categoryRow2);
        List<Object[]> originData = new ArrayList<>();
        originData.add(originRow1);
        originData.add(originRow2);

        when(herbalMedicineRepository.countByCategory()).thenReturn(categoryData);
        when(herbalMedicineRepository.countByOrigin()).thenReturn(originData);

        String view = chartController.index(model);
        assertEquals("charts/index", view);
        verify(model).addAttribute(eq("categoryData"), any());
        verify(model).addAttribute(eq("originData"), any());
        verify(model).addAttribute(eq("categoryJson"), any());
        verify(model).addAttribute(eq("originJson"), any());
    }
}

