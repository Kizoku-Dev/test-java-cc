package fr.ccomptes.test.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountCreationRequest(@NotBlank(message = "Nom invalide") String name) {}