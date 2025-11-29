package com.itu.gest_emp.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.itu.gest_emp.model.DocumentsRH;
import com.itu.gest_emp.model.Person;
import com.itu.gest_emp.repository.DocumentsRHRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;




@Service
public class DocumentArchivingService {

    @Autowired
    private DocumentsRHRepository documentsRHRepository;

    public Page<DocumentsRH> getAllDocuments(Pageable pageable) {
        return documentsRHRepository.findAll(pageable);
    }

    public DocumentsRH saveDocument(DocumentsRH document) {
        if (document.getCreatedAt() == null) {
            document.setCreatedAt(LocalDateTime.now());
        }
        document.setUpdatedAt(LocalDateTime.now());
        return documentsRHRepository.save(document);
    }

    public Page<DocumentsRH> getDocumentsByPersonnel(Long personnelId, Pageable pageable) {
        return documentsRHRepository.findByPersonnelId(personnelId, pageable);
    }

    public List<DocumentsRH> getDocumentsByType(String typeDocument) {
        return documentsRHRepository.findByTypeDocument(typeDocument);
    }

    public List<DocumentsRH> getExpiringDocuments(LocalDate threshold) {
        return documentsRHRepository.findByDateExpirationBefore(threshold);
    }

    public List<DocumentsRH> getUnverifiedDocuments() {
        return documentsRHRepository.findByIsVerified(false);
    }

    public DocumentsRH verifyDocument(Long documentId) {
        DocumentsRH document = documentsRHRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID: " + documentId));

        document.setIsVerified(true);
        document.setVerifiedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        return documentsRHRepository.save(document);
    }

    public Long getUnverifiedDocumentsCount() {
        return documentsRHRepository.countUnverifiedDocuments();
    }

    public List<Object[]> getDocumentsStatistics() {
        return documentsRHRepository.countByTypeDocument();
    }

    public void deleteDocument(Long documentId) {
        documentsRHRepository.deleteById(documentId);
    }

    public DocumentsRH getDocumentById(Long documentId) {
        Optional<DocumentsRH> document = documentsRHRepository.findById(documentId);
        return document.orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID: " + documentId));
    }
}