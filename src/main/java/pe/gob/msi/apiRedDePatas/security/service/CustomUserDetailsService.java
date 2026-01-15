package pe.gob.msi.apiRedDePatas.security.service;

import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.gob.msi.apiRedDePatas.security.jwt.SecurityUser;
import pe.gob.msi.apiRedDePatas.shared.util.ProcedureHelper;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * Servicio que carga usuarios desde la base de datos Oracle
 * Utiliza procedimiento almacenado PKG_MSIRDP_AUTENTICACION.PRC_OBTENERUSUARIOPORLOGIN
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private static final String PKG_AUTENTICACION = "PKG_MSIRDP_AUTENTICACION";
    private static final String PRC_OBTENER_USUARIO = "PRC_OBTENERUSUARIOPORLOGIN";
    private static final String CURSOR_DATOS = "PcrDATOS";

    private final ProcedureHelper procedureHelper;

    public CustomUserDetailsService(ProcedureHelper procedureHelper) {
        this.procedureHelper = procedureHelper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        log.debug("Buscando usuario con DNI: {}", dni);

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(procedureHelper.getDataSource())
                    .withSchemaName(procedureHelper.getSchemaName())
                    .withCatalogName(PKG_AUTENTICACION)
                    .withProcedureName(PRC_OBTENER_USUARIO)
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("PvcCODDNI", Types.VARCHAR),
                            new SqlOutParameter(CURSOR_DATOS, OracleTypes.CURSOR, (rs, rowNum) -> {
                                String nombres = rs.getString("TXTNOMBRES");
                                String apellidos = rs.getString("TXTAPELLIDOS");
                                String nombreCompleto = nombres + " " + apellidos;

                                return new SecurityUser(
                                        rs.getLong("NUMUSUARIO"),
                                        rs.getLong("NUMPERSONA"),
                                        rs.getString("CODDNI"),
                                        rs.getString("TXTCORREO"),
                                        rs.getString("TXTCLAVE"),
                                        nombreCompleto,
                                        rs.getString("ROL"),
                                        "ACT".equals(rs.getString("ESTUSUARIO")) && "1".equals(rs.getString("FLGACTIVO"))
                                );
                            })
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("PvcCODDNI", dni);

            Map<String, Object> result = jdbcCall.execute(params);
            List<SecurityUser> usuarios = (List<SecurityUser>) result.get(CURSOR_DATOS);

            if (usuarios == null || usuarios.isEmpty()) {
                log.warn("Usuario no encontrado con DNI: {}", dni);
                throw new UsernameNotFoundException("Usuario no encontrado con DNI: " + dni);
            }

            SecurityUser user = usuarios.get(0);
            log.debug("Usuario encontrado: {} - Activo: {}", user.dni(), user.activo());

            return user;

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al buscar usuario con DNI {}: {}", dni, e.getMessage(), e);
            throw new UsernameNotFoundException("Error al buscar usuario: " + e.getMessage(), e);
        }
    }
}
