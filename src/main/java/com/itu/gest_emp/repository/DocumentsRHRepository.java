package com.itu.gest_emp.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.model.DocumentsRH;

import java.time.LocalDate;
import java.util.List;



@Repository
public interface DocumentsRHRepository extends JpaRepository<DocumentsRH, Long> {

    Page<DocumentsRH> findByPersonnelId(Long personnelId, Pageable pageable);

    List<DocumentsRH> findByTypeDocument(String typeDocument);

    List<DocumentsRH> findByIsVerified(Boolean isVerified);

    List<DocumentsRH> findByDateExpirationBefore(LocalDate date);

    List<DocumentsRH> findByDateExpirationBetween(LocalDate start, LocalDate end);

    @Query("SELECT d FROM DocumentsRH d WHERE d.personnel.id = :personnelId AND d.typeDocument = :typeDocument")
    List<DocumentsRH> findByPersonnelAndType(Long personnelId, String typeDocument);

    @Query("SELECT COUNT(d) FROM DocumentsRH d WHERE d.isVerified = false")
    Long countUnverifiedDocuments();

    @Query("SELECT d.typeDocument, COUNT(d) FROM DocumentsRH d GROUP BY d.typeDocument")
    List<Object[]> countByTypeDocument();
}