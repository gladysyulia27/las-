package org.delcom.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class HerbalMedicineForm {
    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(max = 200, message = "Nama maksimal 200 karakter")
    private String name;

    @NotBlank(message = "Deskripsi tidak boleh kosong")
    private String description;

    @NotBlank(message = "Kategori tidak boleh kosong")
    private String category;

    @NotBlank(message = "Asal tidak boleh kosong")
    private String origin;

    @NotBlank(message = "Manfaat tidak boleh kosong")
    private String benefits;

    private MultipartFile image;

    public HerbalMedicineForm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}

