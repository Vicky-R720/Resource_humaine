// CareerHistoryService.java
package com.itu.gest_emp.modules.personnel.service;

import com.itu.gest_emp.modules.personnel.model.CareerHistoryRh;
import com.itu.gest_emp.modules.personnel.repository.CareerHistoryRhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CareerHistoryService {

    private final CareerHistoryRhRepository careerHistoryRhRepository;

    public List<CareerHistoryRh> findAll() {
        return careerHistoryRhRepository.findAll();
    }

    public Optional<CareerHistoryRh> findById(Long id) {
        return careerHistoryRhRepository.findById(id);
    }

    public List<CareerHistoryRh> findByPersonnelId(Long personnelId) {
        return careerHistoryRhRepository.findByPersonnelIdOrderByDateMouvementDesc(personnelId);
    }

    public List<CareerHistoryRh> findByTypeMouvement(String typeMouvement) {
        return careerHistoryRhRepository.findByTypeMouvement(typeMouvement);
    }

    public CareerHistoryRh save(CareerHistoryRh careerHistory) {
        return careerHistoryRhRepository.save(careerHistory);
    }

    public void deleteById(Long id) {
        careerHistoryRhRepository.deleteById(id);
    }
}
