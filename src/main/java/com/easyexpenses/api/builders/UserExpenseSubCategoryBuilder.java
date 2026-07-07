package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;

public class UserExpenseSubCategoryBuilder {

    private Long id;
    private String name = "Jedzenie/kosmetyki/chemia/inne";
    private UserProfile userProfile;
    private UserExpenseCategory userExpenseCategory;

    public UserExpenseSubCategoryBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserExpenseSubCategoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserExpenseSubCategoryBuilder userProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        return this;
    }

    public UserExpenseSubCategoryBuilder userExpenseCategory(UserExpenseCategory userExpenseCategory) {
        this.userExpenseCategory = userExpenseCategory;
        return this;
    }

    public UserExpenseSubCategory build() {
        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategory();
        userExpenseSubCategory.setId(id);
        userExpenseSubCategory.setName(name);

        if (userProfile != null) {
            userProfile.addNewUserSubCategory(userExpenseSubCategory);
        }

        if (userExpenseCategory != null) {
            userExpenseCategory.addNewSubCategoryRelatedWithThisCategory(userExpenseSubCategory);
        }
        return userExpenseSubCategory;
    }
}