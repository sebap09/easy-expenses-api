package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.fixtures.ExpenseFixture;

public class ExpenseFixtureBuilder {
    public ExpenseFixture build() {

        User user = new UserBuilder()
                .id(1L)
                .role(Role.USER)
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