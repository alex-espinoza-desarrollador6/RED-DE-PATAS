package pe.gob.msi.apiRedDePatas.shared.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pe.gob.msi.apiRedDePatas.security.jwt.SecurityUser;

import java.util.Optional;

/**
 * Utilidades para obtener información del usuario autenticado
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Clase utilitaria
    }

    /**
     * Obtiene el usuario autenticado actual
     */
    public static Optional<SecurityUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Obtiene el ID del usuario autenticado (NUMUSUARIO)
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(SecurityUser::numUsuario);
    }

    /**
     * Obtiene el ID de persona del usuario autenticado (NUMPERSONA)
     */
    public static Optional<Long> getCurrentPersonId() {
        return getCurrentUser().map(SecurityUser::numPersona);
    }

    /**
     * Obtiene el email del usuario autenticado
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(SecurityUser::getUsername);
    }

    /**
     * Obtiene el rol del usuario autenticado
     */
    public static Optional<String> getCurrentUserRole() {
        return getCurrentUser().map(SecurityUser::rol);
    }

    /**
     * Obtiene el nombre completo del usuario autenticado
     */
    public static Optional<String> getCurrentUserFullName() {
        return getCurrentUser().map(SecurityUser::nombreCompleto);
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public static boolean hasRole(String role) {
        return getCurrentUserRole()
                .map(r -> r.equals(role))
                .orElse(false);
    }

    /**
     * Verifica si el usuario es administrador
     */
    public static boolean isAdmin() {
        return hasRole("ADMINISTRADOR");
    }

    /**
     * Verifica si el usuario es paseador
     */
    public static boolean isPaseador() {
        return hasRole("PASEADOR");
    }

    /**
     * Verifica si el usuario es ciudadano
     */
    public static boolean isCiudadano() {
        return hasRole("CIUDADANO");
    }
}
