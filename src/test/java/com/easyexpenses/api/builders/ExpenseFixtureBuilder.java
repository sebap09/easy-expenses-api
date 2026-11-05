package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.fixtures.ExpenseFixture;

import java.util.UUID;

public class ExpenseFixtureBuilder {
    public static String getRandomSubject(){
        return UUID.randomUUID().toString();
    }

    public ExpenseFixture build() {

        User user = new UserBuilder()
                .identityIssuer("https://auth.example.com/realms/myrealm")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();
        user.setId(1L);

        UserProfile userProfile = new UserProfile();
        user.setUserProfile(userProfile);

        UserPaymentMethod paymentMethod =
                new UserPaymentMethodBuilder()
                        .name("UPM 1")
                        .build();
        paymentMethod.setId(1L);
        userProfile.addNewUserPaymentMethod(paymentMethod);

        UserExpenseCategory category =
                new UserExpenseCategoryBuilder()
                        .name("UEC 1")
                        .build();
        category.setId(1L);
        userProfile.addNewUserCategory(category);

        UserExpenseSubCategory subCategory =
                new UserExpenseSubCategoryBuilder()
                        .name("UESC 1")
                        .build();
        subCategory.setId(1L);
        category.addNewSubCategoryRelatedWithThisCategory(subCategory);

        userProfile.setCompleted(true);

        return new ExpenseFixture(
                userProfile,
                paymentMethod,
                category,
                subCategory
        );
    }
}