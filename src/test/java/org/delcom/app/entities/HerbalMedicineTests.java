package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class HerbalMedicineTests {
    @Test
    void testDefaultConstructor() {
        HerbalMedicine medicine = new HerbalMedicine();
        assertNotNull(medicine);
    }

    @Test
    void testParameterizedConstructor() {
        UUID userId = UUID.randomUUID();
        HerbalMedicine medicine = new HerbalMedicine(
                userId, "Jahe", "Jahe adalah rempah-rempah", 
                "Rempah", "Indonesia", "Mengobati mual", "/path/to/image.jpg"
        );
        assertEquals(userId, medicine.getUserId());
        assertEquals("Jahe", medicine.getName());
        assertEquals("Jahe adalah rempah-rempah", medicine.getDescription());
        assertEquals("Rempah", medicine.getCategory());
        assertEquals("Indonesia", medicine.getOrigin());
        assertEquals("Mengobati mual", medicine.getBenefits());
        assertEquals("/path/to/image.jpg", medicine.getImagePath());
    }

    @Test
    void testGettersAndSetters() {
        HerbalMedicine medicine = new HerbalMedicine();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        medicine.setId(id);
        medicine.setUserId(userId);
        medicine.setName("Kunyit");
        medicine.setDescription("Deskripsi kunyit");
        medicine.setCategory("Rempah");
        medicine.setOrigin("Jawa");
        medicine.setBenefits("Anti inflamasi");
        medicine.setImagePath("/path/image.jpg");
        medicine.setCreatedAt(now);
        medicine.setUpdatedAt(now);

        assertEquals(id, medicine.getId());
        assertEquals(userId, medicine.getUserId());
        assertEquals("Kunyit", medicine.getName());
        assertEquals("Deskripsi kunyit", medicine.getDescription());
        assertEquals("Rempah", medicine.getCategory());
        assertEquals("Jawa", medicine.getOrigin());
        assertEquals("Anti inflamasi", medicine.getBenefits());
        assertEquals("/path/image.jpg", medicine.getImagePath());
        assertEquals(now, medicine.getCreatedAt());
        assertEquals(now, medicine.getUpdatedAt());
    }

    @Test
    void testLifecycleCallbacks_onCreate_and_onUpdate() throws Exception {
        HerbalMedicine medicine = new HerbalMedicine();

        Method onCreate = HerbalMedicine.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(medicine);

        assertNotNull(medicine.getCreatedAt());
        assertNotNull(medicine.getUpdatedAt());

        LocalDateTime firstUpdatedAt = medicine.getUpdatedAt();

        Thread.sleep(5); // ensure timestamp difference
        Method onUpdate = HerbalMedicine.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(medicine);

        assertTrue(medicine.getUpdatedAt().isAfter(firstUpdatedAt) || medicine.getUpdatedAt().isEqual(firstUpdatedAt));
    }
}

