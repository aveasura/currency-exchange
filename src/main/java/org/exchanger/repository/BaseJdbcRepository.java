package org.exchanger.repository;

import org.exchanger.config.connection.ConnectionProvider;
import org.exchanger.exception.DataAccessException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class BaseJdbcRepository {

    protected final ConnectionProvider connectionProvider;

    public BaseJdbcRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    protected boolean isUniqueConstraintViolation(Throwable throwable) {
        Throwable cause = throwable.getCause();

        return cause instanceof SQLiteException sqliteException
               && sqliteException.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE;
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected <T> T executeSingleResult(
            String sql,
            PreparedStatementSetter statementSetter,
            RowMapper<T> rowMapper,
            Supplier<? extends RuntimeException> failException
    ) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (statementSetter != null) {
                statementSetter.setValues(preparedStatement);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return rowMapper.mapRow(resultSet);
                }

                throw failException.get();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Database error while executing single result query", e);
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    protected <T> List<T> executeList(
            String sql,
            PreparedStatementSetter statementSetter,
            RowMapper<T> rowMapper
    ) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (statementSetter != null) {
                statementSetter.setValues(preparedStatement);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(rowMapper.mapRow(resultSet));
                }
                return result;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Database error while executing list query", e);
        }
    }

    protected <T> List<T> executeList(String sql, RowMapper<T> rowMapper) {
        return executeList(sql, null, rowMapper);
    }

    @FunctionalInterface
    protected interface PreparedStatementSetter {
        void setValues(PreparedStatement preparedStatement) throws SQLException;
    }

    @FunctionalInterface
    protected interface RowMapper<T> {
        T mapRow(ResultSet resultSet) throws SQLException;
    }
}
