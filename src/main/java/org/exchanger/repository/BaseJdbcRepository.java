package org.exchanger.repository;

import org.exchanger.config.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseJdbcRepository {

    protected final ConnectionProvider connectionProvider;

    public BaseJdbcRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    protected <T> T executeSingleResult(String sql, PreparedStatementSetter statementSetter, RowMapper<T> rowMapper) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (statementSetter != null) {
                statementSetter.setValues(preparedStatement);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return rowMapper.mapRow(resultSet);
                }
            }

            throw new RuntimeException("Expected single result, but got none");

        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    @FunctionalInterface
    protected interface PreparedStatementSetter {
        void setValues(PreparedStatement preparedStatement) throws SQLException;
    }

    @FunctionalInterface
    protected interface RowMapper<T> {
        T mapRow(ResultSet resultSet) throws SQLException;
    }
}
