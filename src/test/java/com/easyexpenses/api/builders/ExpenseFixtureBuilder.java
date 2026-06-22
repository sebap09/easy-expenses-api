package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.fixtures.ExpenseFixture;

public class ExpenseFixtureBuilder {
    public ExpenseFixture build() {

        User user = new UserBuilder()
                .id(1L)
                .build();

        UserPaymentMethod paymentMethod =
                new UserPaymentMethodBuilder()
                        .id(1L)
                        .user(user)
                        .build();

        UserExpenseCategory category =
                new UserExpenseCategoryBuilder()
                        .id(1L)
                        .user(user)
                        .build();

        UserExpenseSubCategory subCategory =
                new UserExpenseSubCategoryBuilder()
                        .id(1L)
                        .user(user)
                        .userExpenseCategory(category)
                        .build();

        return new ExpenseFixture(
                user,
                paymentMethod,
                category,
                subCategory
        );
    }
}