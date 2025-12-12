package org.delcom.app.controllers;

import org.delcom.app.repositories.HerbalMedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/charts")
public class ChartController {
    @Autowired
    private HerbalMedicineRepository herbalMedicineRepository;

    @GetMapping
    public String index(Model model) {
        List<Object[]> categoryData = herbalMedicineRepository.countByCategory();
        List<Object[]> originData = herbalMedicineRepository.countByOrigin();

        Map<String, Long> categoryMap = new HashMap<>();
        for (Object[] row : categoryData) {
            if (row[0] != null && row[1] != null) {
                String category = row[0].toString();
                Long count = ((Number) row[1]).longValue();
                categoryMap.put(category, count);
            }
        }

        Map<String, Long> originMap = new HashMap<>();
        for (Object[] row : originData) {
            if (row[0] != null && row[1] != null) {
                String origin = row[0].toString();
                Long count = ((Number) row[1]).longValue();
                originMap.put(origin, count);
            }
        }

        // Convert Map to JSON string manually
        String categoryJson = mapToJson(categoryMap);
        String originJson = mapToJson(originMap);

        model.addAttribute("categoryData", categoryMap);
        model.addAttribute("originData", originMap);
        model.addAttribute("categoryJson", categoryJson);
        model.addAttribute("originJson", originJson);
        return "charts/index";
    }

    private String mapToJson(Map<String, Long> map) {
        if (map.isEmpty()) {
            return "{}";
        }
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(escapeJson(entry.getKey())).append("\":").append(entry.getValue());
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
}

