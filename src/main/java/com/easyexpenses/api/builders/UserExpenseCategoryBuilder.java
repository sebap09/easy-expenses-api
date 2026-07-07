package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserExpenseCategory;

public class UserExpenseCategoryBuilder {

    private Long id;
    private String name = "Zakupy";
    private UserProfile userProfile;

    public UserExpenseCategoryBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserExpenseCategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserExpenseCategoryBuilder userProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        return this;
    }

    public UserExpenseCategory build() {
        UserExpenseCategory userExpenseCategory = new UserExpenseCategory();
        userExpenseCategory.setId(id);
        userExpenseCategory.setName(name);

        if (userProfile != null) {
            userProfile.addNewUserCategory(userExpenseCategory);
        }
        return userExpenseCategory;
    }
}