package com.itu.gest_emp.modules.temps_presence.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itu.gest_emp.modules.temps_presence.model.TypeHs;
import com.itu.gest_emp.modules.temps_presence.repository.TypeHsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TypeHsService {

    private final TypeHsRepository typeHsRepository;

    public TypeHs findByCode(String code) {
        return typeHsRepository.findByCode(code);
    }

    public TypeHs findById(Long id) {
        return typeHsRepository.findById(id).orElseThrow(() -> new RuntimeException("Type HS non trouvé"));
    }

    public boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() == 7; // 7 = dimanche
    }

    // public boolean isFerie(LocalDate date) {
    // // Exemple simple → plus tard tu liras depuis la table "jours_feries"
    // return typeHsRepository.isJourFerie(date);
    // }

    public boolean isNight(LocalTime start, LocalTime end) {
        // Nuit = 22h - 5h
        return start.isAfter(LocalTime.of(22, 0)) || end.isBefore(LocalTime.of(5, 0));
    }

    public List<TypeHs> findAll() {
        return typeHsRepository.findAll();
    }
}
