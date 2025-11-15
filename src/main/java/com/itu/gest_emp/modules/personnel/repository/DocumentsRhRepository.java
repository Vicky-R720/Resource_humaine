package com.itu.gest_emp.modules.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.personnel.model.DocumentsRh;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentsRhRepository extends JpaRepository<DocumentsRh, Long> {
    List<DocumentsRh> findByPersonnelId(Long personnelId);
    List<DocumentsRh> findByTypeDocument(String typeDocument);
    List<DocumentsRh> findByDateExpirationBefore(LocalDate date);
    List<DocumentsRh> findByIsVerified(Boolean isVerified);
}