package pe.gob.msi.apiRedDePatas.shared.util;

import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * Helper para ejecutar procedimientos almacenados de Oracle
 * Simplifica la creación de SimpleJdbcCall y el manejo de cursores
 */
@Component
public class ProcedureHelper {

    private final DataSource dataSource;
    private final String schemaName;

    public ProcedureHelper(DataSource dataSource, String oracleSchema) {
        this.dataSource = dataSource;
        this.schemaName = oracleSchema;
    }

    /**
     * Crea un SimpleJdbcCall configurado para el schema
     */
    public SimpleJdbcCall createCall(String packageName, String procedureName) {
        return new SimpleJdbcCall(dataSource)
                .withSchemaName(schemaName)
                .withCatalogName(packageName)
                .withProcedureName(procedureName);
    }

    /**
     * Crea un SimpleJdbcCall para procedimiento con cursor de salida
     */
    public <T> SimpleJdbcCall createProcedureWithCursor(
            String packageName,
            String procedureName,
            String cursorParamName,
            RowMapper<T> rowMapper
    ) {
        return new SimpleJdbcCall(dataSource)
                .withSchemaName(schemaName)
                .withCatalogName(packageName)
                .withProcedureName(procedureName)
                .returningResultSet(cursorParamName, rowMapper)
                .declareParameters(
                        new SqlOutParameter(cursorParamName, OracleTypes.CURSOR, rowMapper)
                );
    }

    /**
     * Ejecuta un procedimiento y retorna el resultado de un cursor
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> executeWithCursor(
            SimpleJdbcCall jdbcCall,
            MapSqlParameterSource params,
            String cursorParamName
    ) {
        Map<String, Object> result = jdbcCall.execute(params);
        return (List<T>) result.get(cursorParamName);
    }

    /**
     * Obtiene un valor String del resultado
     */
    public String getString(Map<String, Object> result, String paramName) {
        Object value = result.get(paramName);
        return value != null ? value.toString() : null;
    }

    /**
     * Obtiene un valor Long del resultado
     */
    public Long getLong(Map<String, Object> result, String paramName) {
        Object value = result.get(paramName);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    /**
     * Obtiene un valor Integer del resultado
     */
    public Integer getInteger(Map<String, Object> result, String paramName) {
        Object value = result.get(paramName);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    /**
     * Crea parámetro de entrada VARCHAR
     */
    public SqlParameter inVarchar(String name) {
        return new SqlParameter(name, Types.VARCHAR);
    }

    /**
     * Crea parámetro de entrada NUMBER
     */
    public SqlParameter inNumber(String name) {
        return new SqlParameter(name, Types.NUMERIC);
    }

    /**
     * Crea parámetro de entrada DATE
     */
    public SqlParameter inDate(String name) {
        return new SqlParameter(name, Types.DATE);
    }

    /**
     * Crea parámetro de salida VARCHAR
     */
    public SqlOutParameter outVarchar(String name) {
        return new SqlOutParameter(name, Types.VARCHAR);
    }

    /**
     * Crea parámetro de salida NUMBER
     */
    public SqlOutParameter outNumber(String name) {
        return new SqlOutParameter(name, Types.NUMERIC);
    }

    /**
     * Crea parámetro de salida CURSOR
     */
    public <T> SqlOutParameter outCursor(String name, RowMapper<T> rowMapper) {
        return new SqlOutParameter(name, OracleTypes.CURSOR, rowMapper);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getSchemaName() {
        return schemaName;
    }
}
