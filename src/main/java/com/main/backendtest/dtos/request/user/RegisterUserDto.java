package com.main.backendtest.dtos.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class RegisterUserDto {
    @NotNull(message = "email can't be null.")
    @NotBlank(message = "email can't be empty.")
    @Email(message = "email must be an valid E-mail format.")
    private String email;

    @NotNull(message = "password can't be null.")
    @NotBlank(message = "password can't be empty.")
    @Size(min = 6, message = "password must have at least 6 characters.")
    private String password;

    @NotNull(message = "name can't be null.")
    @NotBlank(message = "name can't be empty.")
    @Size(min = 5, message = "name must have at least 5 characters.")
    private String name;
}
