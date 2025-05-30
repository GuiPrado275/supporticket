package com.guilherme.supporticket.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = Ticket.TABLE_NAME) //database table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ticket {

    public static final String TABLE_NAME = "ticket";

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id is random (id)
    private Long id;

    @ManyToOne //Many tasks for one user
    @JoinColumn(name = "userId", nullable = false, updatable = false) //this is for make reference of "user_id"
    private User user;

    @Column(name = "peopleAndSetorInvolved", length = 100, nullable = false)
    @Size(min = 1, max = 100, message = "Por favor, informe a(s) pessoas e o setor envolvido.")
    private String peopleAndSetorInvolved;

    @Column(name = "description", length = 5000)
    @Size(min = 1, max = 5000, message = "A descrição deve ter entre 1 e 5000 caracteres.")
    @NotBlank(message = "A descrição não pode ficar em branco.")
    private String description;

    @Column(name = "registrationtDate",nullable = false)
    @NotNull(message = "Data de registro não pode ficar em branco.")
    private LocalDateTime registrationDate;

    @Column(name = "isFinished", nullable = false)
    private boolean isFinished;
}
//model, repository, projection (optional), service, controller, and test