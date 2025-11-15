package com.itu.gest_emp.modules.personnel.service;

import com.itu.gest_emp.modules.personnel.model.DocumentsRh;
import com.itu.gest_emp.modules.personnel.repository.DocumentsRhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentsRhService {
    
    private final DocumentsRhRepository documentsRhRepository;
    
    public List<DocumentsRh> findAll() {
        return documentsRhRepository.findAll();
    }
    
    public Optional<DocumentsRh> findById(Long id) {
        return documentsRhRepository.findById(id);
    }
    
    public List<DocumentsRh> findByPersonnelId(Long personnelId) {
        return documentsRhRepository.findByPersonnelId(personnelId);
    }
    
    public List<DocumentsRh> findByTypeDocument(String typeDocument) {
        return documentsRhRepository.findByTypeDocument(typeDocument);
    }
    
    public List<DocumentsRh> findDocumentsExpirant(LocalDate dateLimit) {
        return documentsRhRepository.findByDateExpirationBefore(dateLimit);
    }
    
    public List<DocumentsRh> findDocumentsNonVerifies() {
        return documentsRhRepository.findByIsVerified(false);
    }
    
    public DocumentsRh save(DocumentsRh document) {
        return documentsRhRepository.save(document);
    }
    
    public void deleteById(Long id) {
        documentsRhRepository.deleteById(id);
    }
    
    public DocumentsRh verifierDocument(Long documentId, Long verifierId) {
        DocumentsRh document = documentsRhRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID: " + documentId));
        
        document.setIsVerified(true);
        document.setVerifiedAt(LocalDateTime.now());
        // Set verifiedBy avec Person entity
        
        return documentsRhRepository.save(document);
    }
}
