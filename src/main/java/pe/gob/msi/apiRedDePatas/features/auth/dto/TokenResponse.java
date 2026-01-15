package pe.gob.msi.apiRedDePatas.features.auth.dto;

/**
 * Response con token JWT y datos del usuario
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UsuarioInfo usuario
) {

    /**
     * Información básica del usuario autenticado
     */
    public record UsuarioInfo(
            Long numUsuario,
            Long numPersona,
            String dni,
            String correo,
            String nombreCompleto,
            String rol
    ) {}

    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn, UsuarioInfo usuario) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn, usuario);
    }
}
