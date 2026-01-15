package pe.gob.msi.apiRedDePatas.features.auth.api;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador TEMPORAL para generar claves BCrypt
 * ELIMINAR EN PRODUCCIÓN
 */
@RestController
@RequestMapping("/api/v1/auth")
public class PasswordEncoderController {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/encode")
    public String encodePassword(@RequestParam String password) {
        return encoder.encode(password);
    }
}
