package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.QcmQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QcmQuestionRepository extends JpaRepository<QcmQuestion, Long> {
    
    List<QcmQuestion> findByCategorie(String categorie);

    @Query("SELECT q FROM QcmQuestion q WHERE q.categorie IN :categories")
    List<QcmQuestion> findByCategories(@Param("categories") List<String> categories);

    // CORRECTION: Pour PostgreSQL utilisez RANDOM(), pour MySQL utilisez RAND()
    @Query(value = "SELECT * FROM qcm_questions ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<QcmQuestion> findRandomQuestions(@Param("limit") int limit);
    
    // Alternative pour MySQL si nécessaire :
    // @Query(value = "SELECT * FROM qcm_questions ORDER BY RAND() LIMIT :limit", nativeQuery = true)
}
