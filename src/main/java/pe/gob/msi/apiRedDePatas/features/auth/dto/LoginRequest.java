package pe.gob.msi.apiRedDePatas.features.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request para login de usuario por DNI
 */
public record LoginRequest(
        @NotBlank(message = "El DNI es requerido")
        @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe contener solo números")
        String dni,

        @NotBlank(message = "La contraseña es requerida")
        String clave
) {}
