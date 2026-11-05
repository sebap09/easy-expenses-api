package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.UserExpenseCategory;

public class UserExpenseCategoryBuilder {

    private String name;

    public UserExpenseCategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserExpenseCategory build() {
        UserExpenseCategory userExpenseCategory = new UserExpenseCategory();
        userExpenseCategory.setName(name);

        return userExpenseCategory;
    }
}