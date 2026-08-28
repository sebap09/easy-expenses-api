package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.fixtures.ExpenseFixture;

public class ExpenseFixtureBuilder {
    public ExpenseFixture build() {

        UserProfile userProfile = new UserProfileBuilder()
                .user(1L)
                .build();

        UserPaymentMethod paymentMethod =
                new UserPaymentMethodBuilder()
                        .id(1L)
                        .userProfile(userProfile)
                        .build();

        UserExpenseCategory category =
                new UserExpenseCategoryBuilder()
                        .id(1L)
                        .userProfile(userProfile)
                        .build();

        UserExpenseSubCategory subCategory =
                new UserExpenseSubCategoryBuilder()
                        .id(1L)
                        .userProfile(userProfile)
                        .userExpenseCategory(category)
                        .build();

        return new ExpenseFixture(
                userProfile,
                paymentMethod,
                category,
                subCategory
        );
    }
}