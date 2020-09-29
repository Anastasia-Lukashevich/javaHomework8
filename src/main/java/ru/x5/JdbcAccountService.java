package ru.x5;

import ru.x5.exceptions.NotEnoughMoneyException;
import ru.x5.exceptions.UnknownAccountException;

import java.sql.*;

public class JdbcAccountService implements AccountService {
    private final String connectionUrl;

    public JdbcAccountService(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    @Override
    public void withdraw(int accountId, int amount) throws NotEnoughMoneyException, UnknownAccountException {
        try (Connection connection = getConnection()) {
            Account account = getAccount(accountId, connection);
            account.withdraw(amount);
            saveAccount(account, connection);
            System.out.println("Указанная сумма списана со счета.");
        } catch (SQLException exception) {
            throw new RuntimeException("db fail", exception);
        }
    }

    @Override
    public void balance(int accountId) throws UnknownAccountException {
        try (Connection connection = getConnection()) {
            Account account = getAccount(accountId, connection);
            System.out.println("Сумма на счете " + account.getAmount());
        } catch (SQLException exception) {
            throw new RuntimeException("db fail", exception);
        }
    }

    @Override
    public void deposit(int accountId, int amount) throws UnknownAccountException {
        try (Connection connection = getConnection()) {
            Account account = getAccount(accountId, connection);
            account.deposit(amount);
            saveAccount(account, connection);
            System.out.println("Указанная сумма зачислена на счет.");
        } catch (SQLException exception) {
            throw new RuntimeException("db fail", exception);
        }
    }

    @Override
    public void transfer(int from, int to, int amount) throws NotEnoughMoneyException, UnknownAccountException {
        try (Connection connection = getConnection()) {
            try {
                connection.setAutoCommit(false);
                Account accountFrom = getAccount(from, connection);
                Account accountTo = getAccount(to, connection);
                accountFrom.withdraw(amount);
                accountTo.deposit(amount);
                saveAccount(accountFrom, connection);
                saveAccount(accountTo, connection);
                System.out.println("Указанная сумма успешно переведена.");
            } catch (SQLException exception) {
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("db fail", exception);
        }
    }

    private Account getAccount(int accountId, Connection connection) throws UnknownAccountException, SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from ACCOUNTS where id = ?");
        preparedStatement.setInt(1, accountId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            throw new UnknownAccountException();
        }
        return new Account(resultSet.getInt("id"),
                resultSet.getString("holder"),
                resultSet.getInt("amount"));
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    private void saveAccount(Account account, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update ACCOUNTS set amount = ? where id = ?");
        preparedStatement.setInt(1, account.getAmount());
        preparedStatement.setInt(2, account.getId());
        preparedStatement.executeUpdate();
    }
}

