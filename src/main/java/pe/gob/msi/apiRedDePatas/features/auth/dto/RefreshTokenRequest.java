package pe.gob.msi.apiRedDePatas.features.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request para refrescar token
 */
public record RefreshTokenRequest(
        @NotBlank(message = "El refresh token es requerido")
        String refreshToken
) {}
