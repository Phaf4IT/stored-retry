package eu.phaf4it.stored_retry.postgres.jdbc;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.phaf4it.stored_retry.core.RetryTaskAction;
import eu.phaf4it.stored_retry.core.RetryTaskActionRepository;
import eu.phaf4it.stored_retry.core.Task;
import eu.phaf4it.stored_retry.postgres.DatabaseConfiguration;
import eu.phaf4it.stored_retry.postgres.ParameterClassNameAndValue;
import eu.phaf4it.stored_retry.postgres.PostgresConnection;
import eu.phaf4it.stored_retry.postgres.StringFormatter;
import eu.phaf4it.stored_retry.postgres.serialisation.JsonObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresRetryTaskActionRepository implements RetryTaskActionRepository {

    private final PostgresConnection postgresConnection;
    private final JsonObjectMapper jsonObjectMapper;
    private final String tableName;
    private static final Logger LOG = LoggerFactory.getLogger(PostgresRetryTaskActionRepository.class);

    public PostgresRetryTaskActionRepository(
            PostgresConnection postgresConnection, JsonObjectMapper jsonObjectMapper,
            DatabaseConfiguration databaseConfiguration
    ) {
        this.postgresConnection = postgresConnection;
        this.jsonObjectMapper = jsonObjectMapper;
        tableName = databaseConfiguration.tablePrefix() + RetryTaskActionRepository.TABLE_NAME + databaseConfiguration.tablePostfix();
    }

    @Override
    public void save(RetryTaskAction retryTaskAction) {
        try (Connection connection = postgresConnection.connect()) {
            var task = retryTaskAction.task();
            String sql = StringFormatter.format("INSERT INTO {}\n", tableName) +
                         """
                                 (
                                 THE_CLASS,
                                 METHOD_NAME,
                                 RETRY_METHOD_NAME,
                                 PARAMETERS,
                                 PARAMETER_VALUES,
                                 EVENT_DATE_TIME
                                 )
                                 """
                         +
                         StringFormatter.format(
                                 "VALUES ('{}','{}','{}','{}', '{}', '{}')\n",
                                 task.theClass().getName(),
                                 task.methodName(),
                                 retryTaskAction.retryMethod(),
                                 jsonObjectMapper.serialize(task.parameters()),
                                 jsonObjectMapper.serialize(retryTaskAction.parameterValues()
                                         .stream()
                                         .map(parameterClassAndValue -> new ParameterClassNameAndValue(
                                                         parameterClassAndValue.theClass(),
                                                         parameterClassAndValue.theValue()
                                                 )
                                         )
                                         .toList()
                                 ),
                                 retryTaskAction.originalEventTime()
                         );
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(RetryTaskAction retryTaskAction) {
        try (Connection connection = postgresConnection.connect()) {
            LOG.info("Removing {}", retryTaskAction);
            String sql = StringFormatter.format("DELETE FROM {}\n", tableName) +
                         StringFormatter.format(
                                 "WHERE " +
                                 "RETRY_METHOD_NAME = '{}' " +
                                 "AND THE_CLASS = '{}' " +
                                 "AND PARAMETER_VALUES = '{}' " +
                                 "AND METHOD_NAME = '{}' " +
                                 "AND EVENT_DATE_TIME = '{}'; \n",
                                 retryTaskAction.retryMethod(),
                                 retryTaskAction.task().theClass().getName(),
                                 jsonObjectMapper.serialize(retryTaskAction.parameterValues()
                                         .stream()
                                         .map(parameterClassAndValue -> new ParameterClassNameAndValue(
                                                         parameterClassAndValue.theClass(),
                                                         parameterClassAndValue.theValue()
                                                 )
                                         )
                                         .toList()
                                 ),
                                 retryTaskAction.task().methodName(),
                                 retryTaskAction.originalEventTime()
                         );
            try (Statement statement = connection.createStatement()) {
                int result = statement.executeUpdate(sql);
                if (result == 0) {
                    throw new RuntimeException("Could not delete retry task action");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RetryTaskAction> getAndRemoveFirstRetryTaskAction(
            Task task,
            String retryMethod
    ) {
        Optional<RetryTaskAction> firstRetryTaskAction = getFirstRetryTaskAction(task, retryMethod);
        firstRetryTaskAction.ifPresent(this::remove);
        return firstRetryTaskAction;
    }

    @Override
    public Optional<RetryTaskAction> getFirstRetryTaskAction(Task task, String retryMethod) {
        try (Connection connection = postgresConnection.connect()) {
            String sql = "SELECT\n" +
                         """
                                 PARAMETER_VALUES,
                                 EVENT_DATE_TIME
                                 """
                         +
                         "FROM " + tableName + "\n"
                         +
                         StringFormatter.format(
                                 "WHERE METHOD_NAME = '{}' AND RETRY_METHOD_NAME = '{}' AND THE_CLASS = '{}' AND PARAMETERS = '{}'\n",
                                 task.methodName(),
                                 retryMethod,
                                 task.theClass().getName(),
                                 jsonObjectMapper.serialize(task.parameters())
                         );
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    if (resultSet.next()) {
                        var originalEventTime = OffsetDateTime.parse(resultSet.getString("EVENT_DATE_TIME").replace(
                                " ",
                                "T"
                        ));
                        List<? extends ParameterClassNameAndValue> parameterValues = jsonObjectMapper.deserialize(
                                resultSet.getString("PARAMETER_VALUES"),
                                new TypeReference<>() {
                                }
                        );
                        return Optional.of(new RetryTaskAction(
                                task,
                                retryMethod,
                                parameterValues
                                        .stream()
                                        .map(PostgresRetryTaskActionRepository::getParameterClassAndValue)
                                        .toList(),
                                originalEventTime
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<RetryTaskAction> getAllRetryTaskActions(Task task, String retryMethod) {
        try (Connection connection = postgresConnection.connect()) {
            String sql = "SELECT\n" +
                         """
                                 PARAMETER_VALUES,
                                 EVENT_DATE_TIME
                                 """
                         +
                         "FROM " + tableName + "\n"
                         +
                         StringFormatter.format(
                                 "WHERE RETRY_METHOD_NAME = '{}' AND THE_CLASS = '{}' AND PARAMETERS = '{}'\n",
                                 retryMethod,
                                 task.theClass().getName(),
                                 jsonObjectMapper.serialize(task.parameters())
                         );
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    List<RetryTaskAction> retryTaskActions = new ArrayList<>();
                    while (resultSet.next()) {
                        var originalEventTime = OffsetDateTime.parse(resultSet.getString("EVENT_DATE_TIME").replace(
                                " ",
                                "T"
                        ));
                        List<? extends ParameterClassNameAndValue> parameterValues = jsonObjectMapper.deserialize(
                                resultSet.getString("PARAMETER_VALUES"),
                                new TypeReference<>() {
                                }
                        );
                        retryTaskActions.add(new RetryTaskAction(
                                task,
                                retryMethod,
                                parameterValues
                                        .stream()
                                        .map(PostgresRetryTaskActionRepository::getParameterClassAndValue)
                                        .toList(),
                                originalEventTime
                        ));
                    }
                    return retryTaskActions;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static RetryTaskAction.ParameterClassAndValue getParameterClassAndValue(ParameterClassNameAndValue parameterClassNameAndValue) {
        return new RetryTaskAction.ParameterClassAndValue(parameterClassNameAndValue.className(), parameterClassNameAndValue.value());
    }

}
