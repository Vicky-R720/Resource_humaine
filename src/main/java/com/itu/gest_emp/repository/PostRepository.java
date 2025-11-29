package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.Person;
import com.itu.gest_emp.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByNameContainingIgnoreCase(String name);
    List<Post> findByDescriptionContainingIgnoreCase(String keyword);
  
}