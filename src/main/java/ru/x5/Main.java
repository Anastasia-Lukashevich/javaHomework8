package ru.x5;

import ru.x5.exceptions.NotEnoughMoneyException;
import ru.x5.exceptions.UnknownAccountException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, UnknownAccountException, NotEnoughMoneyException, SQLException {
        try (Connection dbConnection = DriverManager.getConnection("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM './schema.sql'\\;RUNSCRIPT FROM './data.sql'")) {
        }
        Scanner scanner = new Scanner(System.in);
        JdbcAccountService jdbcAccountService = new JdbcAccountService("jdbc:h2:mem:test");
        while (true) {
            String[] command = scanner.nextLine().split(" ");
            switch (command[0]) {
                case "balance" -> jdbcAccountService.balance(Integer.parseInt(command[1]));
                case "withdraw" -> jdbcAccountService.withdraw(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                case "deposite" -> jdbcAccountService.deposit(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                case "transfer" -> jdbcAccountService.transfer(Integer.parseInt(command[1]), Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                case "exit" -> System.exit(0);
                default -> System.out.println("Неверная команда. Если хотите выйти, введите exit.");
            }
        }


    }
}
