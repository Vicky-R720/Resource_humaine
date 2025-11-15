package com.itu.gest_emp.modules.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.model.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByNameContainingIgnoreCase(String name);
    List<Post> findByDescriptionContainingIgnoreCase(String keyword);
  
}