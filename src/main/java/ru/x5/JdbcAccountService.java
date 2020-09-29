package ru.x5;

import ru.x5.exceptions.NotEnoughMoneyException;
import ru.x5.exceptions.UnknownAccountException;

import java.io.*;
import java.sql.*;

public class JdbcAccountService implements AccountService {
    private final String connectionUrl;

    public JdbcAccountService(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    @Override
    public void withdraw(int accountId, int amount) throws NotEnoughMoneyException, UnknownAccountException {
        Account account = getAccount(accountId);
        account.withdraw(amount);
        saveAccount(account);
        System.out.println("Указанная сумма списана со счета.");
    }

    @Override
    public void balance(int accountId) throws UnknownAccountException {
        Account account = getAccount(accountId);
        System.out.println("Сумма на счете " + account.getAmount());
    }

    @Override
    public void deposit(int accountId, int amount) throws UnknownAccountException {
        Account account = getAccount(accountId);
        account.deposit(amount);
        saveAccount(account);
        System.out.println("Указанная сумма зачислена на счет.");
    }

    @Override
    public void transfer(int from, int to, int amount) throws NotEnoughMoneyException, UnknownAccountException {
        Account accountFrom = getAccount(from);
        Account accountTo = getAccount(to);
        accountFrom.withdraw(amount);
        accountTo.deposit(amount);
        saveAccount(accountFrom);
        saveAccount(accountTo);
        System.out.println("Указанная сумма успешно переведена.");
    }

    private Account getAccount(int accountId) throws UnknownAccountException {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from ACCOUNTS where id = ?");
            preparedStatement.setInt(1, accountId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new UnknownAccountException();
            }
            return new Account(resultSet.getInt("id"),
                    resultSet.getString("holder"),
                    resultSet.getInt("amount"));

        } catch (SQLException throwables) {
            throw new RuntimeException("db fail");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    private void saveAccount(Account account) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("accounts/" + account.getId() + ".txt"))) {
            writer.println(account.getHolder());
            writer.println(account.getAmount());
        } catch (IOException e) {
            throw new RuntimeException("File work error.");
        }
    }
}

