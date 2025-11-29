package com.itu.gest_emp.modules.absence_conge.dto;

import java.time.LocalDate;

public  class LeaveRequestDTO {
        private Long personnelId;
        private Long leaveTypeId;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String motif;

        public Long getPersonnelId() {
            return personnelId;
        }

        public void setPersonnelId(Long personnelId) {
            this.personnelId = personnelId;
        }

        public Long getLeaveTypeId() {
            return leaveTypeId;
        }

        public void setLeaveTypeId(Long leaveTypeId) {
            this.leaveTypeId = leaveTypeId;
        }

        public LocalDate getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;
        }

        public LocalDate getDateFin() {
            return dateFin;
        }

        public void setDateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
        }

        public String getMotif() {
            return motif;
        }

        public void setMotif(String motif) {
            this.motif = motif;
        }

        // Getters et setters...
    }