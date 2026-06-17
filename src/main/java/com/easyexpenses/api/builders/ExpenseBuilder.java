package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserPaymentMethod;

import java.util.Date;

public class ExpenseBuilder {

    private Date date = new Date();
    private String comment = "Comment";
    private double value = 100.99;
    private Long categoryId = 1L;
    private Long subcategoryId = 1L;

    private User user;
    private UserPaymentMethod userPaymentMethod;

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

    public ExpenseBuilder categoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public Expense build() {
        Expense expense = new Expense();
        expense.setDate(date);
        expense.setComment(comment);
        expense.setValue(value);
        expense.setCategoryId(categoryId);
        expense.setSubcategoryId(subcategoryId);

        if (user != null) {
            user.addNewExpense(expense);
        }

        if (userPaymentMethod != null) {
            userPaymentMethod.addNewExpenseRelatedWithThisUserPaymentMethod(expense);
        }
        return expense;
    }
}