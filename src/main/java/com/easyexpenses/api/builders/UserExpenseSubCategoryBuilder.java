package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.UserExpenseSubCategory;

public class UserExpenseSubCategoryBuilder {

    private String name;

    public UserExpenseSubCategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserExpenseSubCategory build() {
        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategory();
        userExpenseSubCategory.setName(name);

        return userExpenseSubCategory;
    }
}