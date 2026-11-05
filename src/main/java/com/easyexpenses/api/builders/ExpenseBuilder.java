package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.*;

import java.util.Date;

public class ExpenseBuilder {
    private Date date;
    private String comment;
    private double value;

    public ExpenseBuilder date(Date date) {
        this.date = date;
        return this;
    }

    public ExpenseBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public ExpenseBuilder value(double value) {
        this.value = value;
        return this;
    }

    public Expense build() {
        Expense expense = new Expense();
        expense.setDate(date);
        expense.setComment(comment);
        expense.setValue(value);

        return expense;
    }
}