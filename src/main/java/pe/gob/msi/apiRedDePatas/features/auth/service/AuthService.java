package pe.gob.msi.apiRedDePatas.features.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pe.gob.msi.apiRedDePatas.features.auth.dto.LoginRequest;
import pe.gob.msi.apiRedDePatas.features.auth.dto.RefreshTokenRequest;
import pe.gob.msi.apiRedDePatas.features.auth.dto.TokenResponse;
import pe.gob.msi.apiRedDePatas.features.auth.repository.AuthRepository;
import pe.gob.msi.apiRedDePatas.security.jwt.JwtTokenProvider;
import pe.gob.msi.apiRedDePatas.security.jwt.SecurityUser;
import pe.gob.msi.apiRedDePatas.security.service.CustomUserDetailsService;
import pe.gob.msi.apiRedDePatas.shared.exception.BusinessException;

/**
 * Servicio de autenticación
 * Orquesta el login y genera tokens JWT
 */
@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(
            AuthRepository authRepository,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService userDetailsService
    ) {
        this.authRepository = authRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Autentica usuario por DNI y genera tokens JWT
     */
    public TokenResponse login(LoginRequest request) {
        // Autenticar con Spring Security (DNI como username)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.dni(),
                        request.clave()
                )
        );

        SecurityUser user = (SecurityUser) authentication.getPrincipal();

        // Registrar acceso (opcional, no falla si el SP no existe)
        authRepository.registrarAcceso(user.numUsuario());

        // Generar tokens
        String accessToken = jwtTokenProvider.generateTokenFromUser(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // Construir respuesta
        TokenResponse.UsuarioInfo usuarioInfo = new TokenResponse.UsuarioInfo(
                user.numUsuario(),
                user.numPersona(),
                user.dni(),
                user.correo(),
                user.nombreCompleto(),
                user.rol()
        );

        return TokenResponse.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getExpirationMs(),
                usuarioInfo
        );
    }

    /**
     * Refresca el token de acceso usando el refresh token
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        // Validar refresh token
        if (!jwtTokenProvider.validateToken(request.refreshToken())) {
            throw new BusinessException("INVALID_TOKEN", "El refresh token es inválido o ha expirado");
        }

        // Obtener DNI del token (username = DNI)
        String dni = jwtTokenProvider.getUsernameFromToken(request.refreshToken());
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(dni);

        // Generar nuevos tokens
        String accessToken = jwtTokenProvider.generateTokenFromUser(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(dni);

        // Construir respuesta
        TokenResponse.UsuarioInfo usuarioInfo = new TokenResponse.UsuarioInfo(
                user.numUsuario(),
                user.numPersona(),
                user.dni(),
                user.correo(),
                user.nombreCompleto(),
                user.rol()
        );

        return TokenResponse.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getExpirationMs(),
                usuarioInfo
        );
    }
}
