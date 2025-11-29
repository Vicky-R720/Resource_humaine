package com.itu.gest_emp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itu.gest_emp.model.QcmQuestion;
import com.itu.gest_emp.repository.QcmQuestionRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class QcmService {

    @Autowired
    private QcmQuestionRepository questionRepository;

    public List<QcmQuestion> getQuestionsEntrepriseAlcool() {
        return questionRepository.findByCategorie("entreprise_alcool");
    }

    public List<QcmQuestion> getQuestionsCultureGenerale() {
        return questionRepository.findByCategorie("culture_generale");
    }

    public List<QcmQuestion> getAllQuestions() {
        return questionRepository.findAll(); // CORRECTION: plus simple
    }

    public List<QcmQuestion> getRandomQuestions(int limit) {
        return questionRepository.findRandomQuestions(limit);
    }

    public QcmQuestion getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    // CORRECTION: Implémentation de la méthode manquante
    public List<QcmQuestion> getQuestionsByCategorie(String categorie) {
        return questionRepository.findByCategorie(categorie);
    }
}