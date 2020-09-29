package ru.x5;

import ru.x5.exceptions.NotEnoughMoneyException;

public class Account {
    private int id;
    private String holder;
    private int amount;

    public Account(int id, String holder, int amount) {
        this.id = id;
        this.holder = holder;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getHolder() {
        return holder;
    }

    public int getAmount() {
        return amount;
    }

    public void withdraw(int amount) throws NotEnoughMoneyException {
        if (amount > this.amount) {
            throw new NotEnoughMoneyException();
        }
        this.amount -= amount;
    }

    public void deposit(int amount) {
        this.amount += amount;
    }
}
