package com.itu.gest_emp.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.model.AuditLogRH;

import java.time.LocalDateTime;
import java.util.List;



@Repository
public interface AuditLogRHRepository extends JpaRepository<AuditLogRH, Long> {

    Page<AuditLogRH> findByUserId(Long userId, Pageable pageable);

    List<AuditLogRH> findByTableNameAndRecordId(String tableName, Long recordId);

    List<AuditLogRH> findByAction(String action);

    List<AuditLogRH> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM AuditLogRH a WHERE a.tableName = :tableName AND a.createdAt BETWEEN :start AND :end")
    List<AuditLogRH> findByTableNameAndPeriod(String tableName, LocalDateTime start, LocalDateTime end);

    @Query("SELECT DISTINCT a.tableName FROM AuditLogRH a")
    List<String> findDistinctTableNames();

    @Query("SELECT COUNT(a) FROM AuditLogRH a WHERE a.createdAt BETWEEN :start AND :end")
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}