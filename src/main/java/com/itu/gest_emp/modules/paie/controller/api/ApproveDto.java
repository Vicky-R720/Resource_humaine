package com.itu.gest_emp.modules.paie.controller.api;

public class ApproveDto {
    private Long approbateurUtilisateurId;
    private String commentaire;

    public Long getApprobateurUtilisateurId() { return approbateurUtilisateurId; }
    public String getCommentaire() { return commentaire; }

    public void setApprobateurUtilisateurId(Long approbateurUtilisateurId) { this.approbateurUtilisateurId = approbateurUtilisateurId; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
