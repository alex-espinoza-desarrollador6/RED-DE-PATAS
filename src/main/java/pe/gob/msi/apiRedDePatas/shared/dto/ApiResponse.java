package pe.gob.msi.apiRedDePatas.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Respuesta estándar de la API
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "Operación exitosa", data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "SUCCESS", message, data, LocalDateTime.now());
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> validationError(T errors) {
        return new ApiResponse<>(false, "VALIDATION_ERROR", "Error de validación", errors, LocalDateTime.now());
    }
}
