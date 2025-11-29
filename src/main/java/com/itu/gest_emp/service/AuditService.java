package com.itu.gest_emp.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.itu.gest_emp.model.AuditLogRH;
import com.itu.gest_emp.model.Person;
import com.itu.gest_emp.repository.AuditLogRHRepository;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRHRepository auditLogRepository;

    public void logAction(Person user, String action, String tableName, Long recordId,
            String oldValues, String newValues, HttpServletRequest request) {
        AuditLogRH auditLog = new AuditLogRH(user, action, tableName, recordId);
        auditLog.setOldValues(oldValues);
        auditLog.setNewValues(newValues);

        if (request != null) {
            auditLog.setIpAddress(getClientIpAddress(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
        }

        auditLogRepository.save(auditLog);
    }

    public void logAction(Person user, String action, String tableName, Long recordId) {
        logAction(user, action, tableName, recordId, null, null, null);
    }

    public Page<AuditLogRH> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<AuditLogRH> getAuditLogsByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    public List<AuditLogRH> getAuditTrail(String tableName, Long recordId) {
        return auditLogRepository.findByTableNameAndRecordId(tableName, recordId);
    }

    public List<AuditLogRH> getAuditLogsByPeriod(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByCreatedAtBetween(start, end);
    }

    public List<String> getAuditedTables() {
        return auditLogRepository.findDistinctTableNames();
    }

    public Long getAuditCountByPeriod(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.countByCreatedAtBetween(start, end);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
