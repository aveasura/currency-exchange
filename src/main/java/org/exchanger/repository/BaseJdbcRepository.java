package org.exchanger.repository;

import org.exchanger.config.ConnectionProvider;
import org.exchanger.exception.DataAccessException;

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

    protected <T> T executeSingleResult(
            String sql, PreparedStatementSetter
            statementSetter,
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
            throw new DataAccessException(e.getMessage());
        }
    }

    protected <T> List<T> executeList(String sql, PreparedStatementSetter statementSetter, RowMapper<T> rowMapper) {
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
            } catch (SQLException e) {
                throw new RuntimeException("Data base exception: " + e.getMessage());
            }

        } catch (SQLException e) {
            throw new RuntimeException("b");
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
