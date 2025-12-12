package org.delcom.app.services;

import org.delcom.app.dto.HerbalMedicineForm;
import org.delcom.app.entities.HerbalMedicine;
import org.delcom.app.repositories.HerbalMedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HerbalMedicineService {
    @Autowired
    private HerbalMedicineRepository herbalMedicineRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<HerbalMedicine> findAll() {
        return herbalMedicineRepository.findAll();
    }

    public List<HerbalMedicine> findByUserId(UUID userId) {
        return herbalMedicineRepository.findByUserId(userId);
    }

    public Optional<HerbalMedicine> findById(UUID id) {
        return herbalMedicineRepository.findById(id);
    }

    @Transactional
    public HerbalMedicine create(UUID userId, HerbalMedicineForm form) {
        try {
            String imagePath = null;
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                imagePath = fileStorageService.storeFile(form.getImage());
            }

            HerbalMedicine medicine = new HerbalMedicine(
                    userId,
                    form.getName(),
                    form.getDescription(),
                    form.getCategory(),
                    form.getOrigin(),
                    form.getBenefits(),
                    imagePath
            );

            return herbalMedicineRepository.save(medicine);
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan data: " + e.getMessage());
        }
    }

    @Transactional
    public HerbalMedicine update(UUID id, UUID userId, HerbalMedicineForm form) {
        Optional<HerbalMedicine> medicineOpt = herbalMedicineRepository.findById(id);
        if (medicineOpt.isEmpty()) {
            throw new RuntimeException("Data tidak ditemukan");
        }

        HerbalMedicine medicine = medicineOpt.get();
        if (!medicine.getUserId().equals(userId)) {
            throw new RuntimeException("Tidak memiliki akses untuk mengubah data ini");
        }

        medicine.setName(form.getName());
        medicine.setDescription(form.getDescription());
        medicine.setCategory(form.getCategory());
        medicine.setOrigin(form.getOrigin());
        medicine.setBenefits(form.getBenefits());

        try {
            if (form.getImage() != null && !form.getImage().isEmpty()) {
                if (medicine.getImagePath() != null) {
                    fileStorageService.deleteFile(medicine.getImagePath());
                }
                String imagePath = fileStorageService.storeFile(form.getImage());
                medicine.setImagePath(imagePath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengupdate gambar: " + e.getMessage());
        }

        return herbalMedicineRepository.save(medicine);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        Optional<HerbalMedicine> medicineOpt = herbalMedicineRepository.findById(id);
        if (medicineOpt.isEmpty()) {
            throw new RuntimeException("Data tidak ditemukan");
        }

        HerbalMedicine medicine = medicineOpt.get();
        if (!medicine.getUserId().equals(userId)) {
            throw new RuntimeException("Tidak memiliki akses untuk menghapus data ini");
        }

        if (medicine.getImagePath() != null) {
            fileStorageService.deleteFile(medicine.getImagePath());
        }

        herbalMedicineRepository.delete(medicine);
    }
}

