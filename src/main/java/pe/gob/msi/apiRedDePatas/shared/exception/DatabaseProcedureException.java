package pe.gob.msi.apiRedDePatas.shared.exception;

/**
 * Excepción para errores en procedimientos almacenados de Oracle
 */
public class DatabaseProcedureException extends RuntimeException {

    private final String procedureName;
    private final String oracleErrorCode;

    public DatabaseProcedureException(String procedureName, String message) {
        super(String.format("Error en procedimiento %s: %s", procedureName, message));
        this.procedureName = procedureName;
        this.oracleErrorCode = null;
    }

    public DatabaseProcedureException(String procedureName, String oracleErrorCode, String message) {
        super(String.format("Error en procedimiento %s [%s]: %s", procedureName, oracleErrorCode, message));
        this.procedureName = procedureName;
        this.oracleErrorCode = oracleErrorCode;
    }

    public DatabaseProcedureException(String procedureName, String message, Throwable cause) {
        super(String.format("Error en procedimiento %s: %s", procedureName, message), cause);
        this.procedureName = procedureName;
        this.oracleErrorCode = null;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public String getOracleErrorCode() {
        return oracleErrorCode;
    }
}
