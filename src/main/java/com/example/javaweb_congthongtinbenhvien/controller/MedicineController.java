package com.example.javaweb_congthongtinbenhvien.controller;

import com.example.javaweb_congthongtinbenhvien.dto.MedicineRequest;
import com.example.javaweb_congthongtinbenhvien.entity.Medicine;
import com.example.javaweb_congthongtinbenhvien.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<Medicine> medicinePage = medicineService.search(keyword, page, 5);

        model.addAttribute("medicinePage", medicinePage);
        model.addAttribute("medicines", medicinePage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        return "admin/medicines/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("medicine", new MedicineRequest());
        return "admin/medicines/form";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute("medicine") MedicineRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            medicineService.save(request);
            redirectAttributes.addFlashAttribute("success", "Thêm thuốc thành công");
            return "redirect:/admin/medicines";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("medicine", request);
            return "admin/medicines/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model
    ) {
        Medicine medicine = medicineService.findById(id);

        MedicineRequest request = new MedicineRequest();
        request.setId(medicine.getId());
        request.setName(medicine.getName());
        request.setUnit(medicine.getUnit());
        request.setPrice(medicine.getPrice());
        request.setStockQuantity(medicine.getStockQuantity());
        request.setDescription(medicine.getDescription());
        request.setStatus(medicine.getStatus());

        model.addAttribute("medicine", request);
        return "admin/medicines/form";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("medicine") MedicineRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            request.setId(id);
            medicineService.update(id, request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thuốc thành công");
            return "redirect:/admin/medicines";
        } catch (RuntimeException e) {
            request.setId(id);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("medicine", request);
            return "admin/medicines/form";
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
