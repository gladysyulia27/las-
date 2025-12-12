package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class HerbalMedicineFormTests {
    @Test
    void testDefaultConstructor() {
        HerbalMedicineForm form = new HerbalMedicineForm();
        assertNotNull(form);
    }

    @Test
    void testGettersAndSetters() {
        HerbalMedicineForm form = new HerbalMedicineForm();
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);

        form.setName("Jahe");
        form.setDescription("Deskripsi jahe");
        form.setCategory("Rempah");
        form.setOrigin("Indonesia");
        form.setBenefits("Mengobati mual");
        form.setImage(file);

        assertEquals("Jahe", form.getName());
        assertEquals("Deskripsi jahe", form.getDescription());
        assertEquals("Rempah", form.getCategory());
        assertEquals("Indonesia", form.getOrigin());
        assertEquals("Mengobati mual", form.getBenefits());
        assertEquals(file, form.getImage());
    }
}

