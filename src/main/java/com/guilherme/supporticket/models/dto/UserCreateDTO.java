package com.guilherme.supporticket.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateDTO {

    @NotBlank
    @Size(min = 3, max = 50)
    @Email(message = "O e-mail deve ser válido!")
    private String email;

    @NotBlank
    @Size(min = 6, max = 60)
    private String password;

}