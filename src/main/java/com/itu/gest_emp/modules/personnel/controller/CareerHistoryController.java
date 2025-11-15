// CareerHistoryController.java
package com.itu.gest_emp.modules.personnel.controller;

import com.itu.gest_emp.modules.personnel.model.CareerHistoryRh;
import com.itu.gest_emp.modules.personnel.service.CareerHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/api/career-history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CareerHistoryController {

    private final CareerHistoryService careerHistoryService;

    @GetMapping
    public ResponseEntity<List<CareerHistoryRh>> getAllCareerHistory() {
        return ResponseEntity.ok(careerHistoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareerHistoryRh> getCareerHistoryById(@PathVariable Long id) {
        return careerHistoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/personnel/{personnelId}")
    public ResponseEntity<List<CareerHistoryRh>> getCareerHistoryByPersonnelId(@PathVariable Long personnelId) {
        return ResponseEntity.ok(careerHistoryService.findByPersonnelId(personnelId));
    }

    @GetMapping("/type/{typeMouvement}")
    public ResponseEntity<List<CareerHistoryRh>> getCareerHistoryByType(@PathVariable String typeMouvement) {
        return ResponseEntity.ok(careerHistoryService.findByTypeMouvement(typeMouvement));
    }

    @PostMapping
    public ResponseEntity<CareerHistoryRh> createCareerHistory(@RequestBody CareerHistoryRh careerHistory) {
        CareerHistoryRh created = careerHistoryService.save(careerHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCareerHistory(@PathVariable Long id) {
        careerHistoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
