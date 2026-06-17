package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;

import java.util.Date;

public class ExpenseBuilder {

    private Date date = new Date();
    private String comment = "Comment";
    private double value = 100.99;
    private Long subcategoryId = 1L;

    private User user;
    private UserPaymentMethod userPaymentMethod;
    private UserExpenseCategory userExpenseCategory;

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

    public ExpenseBuilder subcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
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

    public Expense build() {
        Expense expense = new Expense();
        expense.setDate(date);
        expense.setComment(comment);
        expense.setValue(value);
        expense.setSubcategoryId(subcategoryId);

        if (user != null) {
            user.addNewExpense(expense);
        }

        if (userPaymentMethod != null) {
            userPaymentMethod.addNewExpenseRelatedWithThisUserPaymentMethod(expense);
        }

        if (userExpenseCategory != null) {
            userExpenseCategory.addNewExpenseRelatedWithThisCategory(expense);
        }
        return expense;
    }
}