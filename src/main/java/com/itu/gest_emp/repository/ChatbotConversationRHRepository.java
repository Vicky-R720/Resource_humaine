package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface ChatbotConversationRHRepository extends JpaRepository<ChatbotConversationRH, Long> {
    List<ChatbotConversationRH> findByPersonIdOrderByCreatedAtDesc(Long personId);

    List<ChatbotConversationRH> findBySessionIdOrderByCreatedAtDesc(String sessionId);

    List<ChatbotConversationRH> findByCategorie(String categorie);
}
