package com.itu.gest_emp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.model.CandidateMatching;

@Repository
public interface CandidateMatchingRepository extends JpaRepository<CandidateMatching, Long> {
    List<CandidateMatching> findByOfferIdOrderByScoreTotalDesc(Long offerId);

    List<CandidateMatching> findTop5ByOrderByCreatedAtDesc();
}