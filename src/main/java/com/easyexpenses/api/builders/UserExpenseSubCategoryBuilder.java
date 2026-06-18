package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;

public class UserExpenseSubCategoryBuilder {

    private String name = "Jedzenie/kosmetyki/chemia/inne";
    private User user;
    private UserExpenseCategory userExpenseCategory;

    public UserExpenseSubCategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserExpenseSubCategoryBuilder user(User user) {
        this.user = user;
        return this;
    }

    public UserExpenseSubCategoryBuilder userExpenseCategory(UserExpenseCategory userExpenseCategory) {
        this.userExpenseCategory = userExpenseCategory;
        return this;
    }

    public UserExpenseSubCategory build() {
        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategory();
        userExpenseSubCategory.setName(name);

        if (user != null) {
            user.addNewUserSubCategory(userExpenseSubCategory);
        }

        if (userExpenseCategory != null) {
            userExpenseCategory.addNewSubCategoryRelatedWithThisCategory(userExpenseSubCategory);
        }
        return userExpenseSubCategory;
    }
}