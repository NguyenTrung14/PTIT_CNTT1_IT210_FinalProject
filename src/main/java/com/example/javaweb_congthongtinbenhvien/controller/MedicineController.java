package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.entity.Medicine;
import com.example.javaweb_congthongtinbenhvien.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            Model model
    ) {
        model.addAttribute("medicines", medicineService.findAll(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/medicines/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("medicine", new Medicine());
        return "admin/medicines/form";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute("medicine") Medicine medicine,
            RedirectAttributes redirectAttributes
    ) {
        try {
            medicineService.save(medicine);
            redirectAttributes.addFlashAttribute("success", "Thêm thuốc thành công");
            return "redirect:/admin/medicines";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/medicines/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("medicine", medicineService.findById(id));
        return "admin/medicines/form";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("medicine") Medicine medicine,
            RedirectAttributes redirectAttributes
    ) {
        try {
            medicine.setId(id);
            medicineService.save(medicine);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thuốc thành công");
            return "redirect:/admin/medicines";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/medicines/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            medicineService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa thuốc thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/medicines";
    }
}