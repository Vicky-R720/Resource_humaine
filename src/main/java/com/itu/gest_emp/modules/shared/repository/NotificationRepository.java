package com.itu.gest_emp.modules.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.shared.model.Notification;
import com.itu.gest_emp.modules.shared.model.Person;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPersonOrderByCreatedAtAsc(Person person);
 
}