package com.itu.gest_emp.modules.absence_conge.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest.LeaveStatus;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
        List<LeaveRequest> findByPersonnel_Id(Long personnelId);

        List<LeaveRequest> findByStatut(String statut);

        List<LeaveRequest> findByValidatedByIsNullAndStatut(String statut);

        @Query("SELECT lr FROM LeaveRequest lr WHERE lr.dateDebut <= :date AND lr.dateFin >= :date AND lr.statut = 'approuve'")
        List<LeaveRequest> findApprovedByDate(@Param("date") LocalDate date);

        @Query("""
                           SELECT lr
                           FROM LeaveRequest lr
                           WHERE lr.statut = 'approuve'
                           AND lr.personnel.post.equipe.service.id = :serviceId
                           AND (
                                (lr.dateDebut <= :dateFin AND lr.dateFin >= :dateDebut)
                           )
                        """)
        List<LeaveRequest> findApprovedLeavesForServiceDuringPeriod(
                        @Param("serviceId") Long serviceId,
                        @Param("dateDebut") LocalDate dateDebut,
                        @Param("dateFin") LocalDate dateFin);

        
        List<LeaveRequest> findByStatutAndPersonnel_Post_Equipe_Service_Id(LeaveStatus approuve, Long serviceId);

        LeaveRequest findByPersonnelAndDateDebut(PersonnelRh personnel, LocalDate dateAbsence);

        List<LeaveRequest> findByPersonnel_IdAndDateDebutBetween(Long personnelId, LocalDate startDate,
                LocalDate endDate);

        LeaveRequest findByDateDebut(LocalDate dateAbsence);

        // long countAbsencesNonAutoriseesRecent(Long personnelId, LocalDate minusMonths, LocalDate dateAbsence);
}