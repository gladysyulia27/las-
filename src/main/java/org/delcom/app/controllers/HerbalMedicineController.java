package org.delcom.app.controllers;

import jakarta.validation.Valid;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.dto.HerbalMedicineForm;
import org.delcom.app.entities.HerbalMedicine;
import org.delcom.app.services.HerbalMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/herbal-medicines")
public class HerbalMedicineController {
    @Autowired
    private HerbalMedicineService herbalMedicineService;

    @GetMapping
    public String list(Model model) {
        List<HerbalMedicine> medicines = herbalMedicineService.findAll();
        model.addAttribute("medicines", medicines);
        return "herbal-medicines/list";
    }

    @GetMapping("/my-medicines")
    public String myMedicines(Model model) {
        UUID userId = AuthContext.getUserId();
        List<HerbalMedicine> medicines = herbalMedicineService.findByUserId(userId);
        model.addAttribute("medicines", medicines);
        return "herbal-medicines/my-medicines";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("herbalMedicineForm", new HerbalMedicineForm());
        return "herbal-medicines/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute HerbalMedicineForm form, BindingResult result, 
                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "herbal-medicines/add";
        }

        try {
            UUID userId = AuthContext.getUserId();
            herbalMedicineService.create(userId, form);
            redirectAttributes.addFlashAttribute("success", "Data berhasil ditambahkan");
            return "redirect:/herbal-medicines/my-medicines";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/herbal-medicines/add";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        HerbalMedicine medicine = herbalMedicineService.findById(id)
                .orElseThrow(() -> new RuntimeException("Data tidak ditemukan"));
        model.addAttribute("medicine", medicine);
        return "herbal-medicines/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        HerbalMedicine medicine = herbalMedicineService.findById(id)
                .orElseThrow(() -> new RuntimeException("Data tidak ditemukan"));
        
        HerbalMedicineForm form = new HerbalMedicineForm();
        form.setName(medicine.getName());
        form.setDescription(medicine.getDescription());
        form.setCategory(medicine.getCategory());
        form.setOrigin(medicine.getOrigin());
        form.setBenefits(medicine.getBenefits());
        
        model.addAttribute("herbalMedicineForm", form);
        model.addAttribute("medicineId", id);
        model.addAttribute("currentImage", medicine.getImagePath());
        return "herbal-medicines/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute HerbalMedicineForm form, 
                        BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validasi gagal");
            return "redirect:/herbal-medicines/" + id + "/edit";
        }

        try {
            UUID userId = AuthContext.getUserId();
            herbalMedicineService.update(id, userId, form);
            redirectAttributes.addFlashAttribute("success", "Data berhasil diupdate");
            return "redirect:/herbal-medicines/my-medicines";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/herbal-medicines/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            UUID userId = AuthContext.getUserId();
            herbalMedicineService.delete(id, userId);
            redirectAttributes.addFlashAttribute("success", "Data berhasil dihapus");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/herbal-medicines/my-medicines";
    }
}

