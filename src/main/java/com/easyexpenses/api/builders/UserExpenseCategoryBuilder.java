package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserExpenseCategory;

public class UserExpenseCategoryBuilder {

    private String name = "Zakupy";
    private User user;

    public UserExpenseCategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserExpenseCategoryBuilder user(User user) {
        this.user = user;
        return this;
    }

    public UserExpenseCategory build() {
        UserExpenseCategory userExpenseCategory = new UserExpenseCategory();
        userExpenseCategory.setName(name);

        if (user != null) {
            user.addNewUserCategory(userExpenseCategory);
        }
        return userExpenseCategory;
    }
}