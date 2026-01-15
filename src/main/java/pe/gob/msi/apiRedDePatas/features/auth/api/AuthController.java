package pe.gob.msi.apiRedDePatas.features.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.msi.apiRedDePatas.features.auth.dto.LoginRequest;
import pe.gob.msi.apiRedDePatas.features.auth.dto.RefreshTokenRequest;
import pe.gob.msi.apiRedDePatas.features.auth.dto.TokenResponse;
import pe.gob.msi.apiRedDePatas.features.auth.service.AuthService;
import pe.gob.msi.apiRedDePatas.shared.dto.ApiResponse;

/**
 * Controlador de autenticación
 * Endpoints públicos para login y refresh token
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica usuario y retorna tokens JWT")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Genera nuevo access token usando refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refrescado", response));
    }
}
