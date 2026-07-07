package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.*;

import java.util.Date;

public class ExpenseBuilder {

    private Long id;
    private Date date = new Date();
    private String comment = "Comment";
    private double value = 100.99;

    private UserProfile userProfile;
    private UserPaymentMethod userPaymentMethod;
    private UserExpenseCategory userExpenseCategory;
    private UserExpenseSubCategory userExpenseSubCategory;

    public ExpenseBuilder id(Long id) {
        this.id = id;
        return this;
    }

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

    public ExpenseBuilder userProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
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
        expense.setId(id);
        expense.setDate(date);
        expense.setComment(comment);
        expense.setValue(value);

        if (userProfile != null) {
            userProfile.addNewExpense(expense);
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