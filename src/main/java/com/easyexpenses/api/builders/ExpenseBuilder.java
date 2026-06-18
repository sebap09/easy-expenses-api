package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.*;

import java.util.Date;

public class ExpenseBuilder {

    private Date date = new Date();
    private String comment = "Comment";
    private double value = 100.99;

    private User user;
    private UserPaymentMethod userPaymentMethod;
    private UserExpenseCategory userExpenseCategory;
    private UserExpenseSubCategory userExpenseSubCategory;

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

    public ExpenseBuilder user(User user) {
        this.user = user;
        return this;
    }

    public ExpenseBuilder userPaymentMethod(UserPaymentMethod userPaymentMethod) {
        this.userPaymentMethod = userPaymentMethod;
        return this;
    }

    public ExpenseBuilder userExpenseCategory(UserExpenseCategory userExpenseCategory) {
        this.userExpenseCategory = userExpenseCategory;
        return this;
    }

    public ExpenseBuilder userExpenseSubCategory(UserExpenseSubCategory userExpenseSubCategory) {
        this.userExpenseSubCategory = userExpenseSubCategory;
        return this;
    }

    public Expense build() {
        Expense expense = new Expense();
        expense.setDate(date);
        expense.setComment(comment);
        expense.setValue(value);

        if (user != null) {
            user.addNewExpense(expense);
        }

        if (userPaymentMethod != null) {
            userPaymentMethod.addNewExpenseRelatedWithThisUserPaymentMethod(expense);
        }

        if (userExpenseCategory != null) {
            userExpenseCategory.addNewExpenseRelatedWithThisCategory(expense);
        }

        if (userExpenseSubCategory != null) {
            userExpenseSubCategory.addNewExpenseRelatedWithThisSubCategory(expense);
        }
        return expense;
    }
}