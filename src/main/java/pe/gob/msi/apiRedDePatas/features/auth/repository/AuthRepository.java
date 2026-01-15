package pe.gob.msi.apiRedDePatas.features.auth.repository;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.msi.apiRedDePatas.shared.exception.DatabaseProcedureException;
import pe.gob.msi.apiRedDePatas.shared.util.ProcedureHelper;

import java.sql.Types;
import java.util.Map;

/**
 * Repositorio para operaciones de autenticación
 * Llama a procedimientos del paquete PKG_AUTH
 */
@Repository
public class AuthRepository {

    private static final String PKG_AUTH = "PKG_AUTH";

    private final ProcedureHelper procedureHelper;

    public AuthRepository(ProcedureHelper procedureHelper) {
        this.procedureHelper = procedureHelper;
    }

    /**
     * Valida login llamando a PKG_AUTH.PRC_LOGIN
     * @return Map con P_RESULTADO (1=éxito, 0=error) y P_MENSAJE
     */
    public Map<String, Object> validarLogin(String correo) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(procedureHelper.getDataSource())
                .withSchemaName(procedureHelper.getSchemaName())
                .withCatalogName(PKG_AUTH)
                .withProcedureName("PRC_LOGIN")
                .declareParameters(
                        new SqlParameter("P_CORREO", Types.VARCHAR),
                        new SqlOutParameter("P_RESULTADO", Types.NUMERIC),
                        new SqlOutParameter("P_MENSAJE", Types.VARCHAR)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_CORREO", correo);

        try {
            return jdbcCall.execute(params);
        } catch (Exception e) {
            throw new DatabaseProcedureException("PKG_AUTH.PRC_LOGIN", e.getMessage(), e);
        }
    }

    /**
     * Registra el último acceso del usuario
     * Llamado después de un login exitoso
     */
    public void registrarAcceso(Long numUsuario) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(procedureHelper.getDataSource())
                .withSchemaName(procedureHelper.getSchemaName())
                .withCatalogName(PKG_AUTH)
                .withProcedureName("PRC_REGISTRAR_ACCESO")
                .declareParameters(
                        new SqlParameter("P_NUMUSUARIO", Types.NUMERIC)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_NUMUSUARIO", numUsuario);

        try {
            jdbcCall.execute(params);
        } catch (Exception e) {
            // Log pero no fallar el login por esto
        }
    }
}
