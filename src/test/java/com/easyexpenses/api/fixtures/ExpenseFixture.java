package com.easyexpenses.api.fixtures;

import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import lombok.Getter;

@Getter
public class ExpenseFixture {
    private final UserProfile userProfile;
    private final UserPaymentMethod userPaymentMethod;
    private final UserExpenseCategory userExpenseCategory;
    private final UserExpenseSubCategory userExpenseSubCategory;


    public ExpenseFixture(UserProfile userProfile, UserPaymentMethod userPaymentMethod, UserExpenseCategory userExpenseCategory, UserExpenseSubCategory userExpenseSubCategory) {
        this.userProfile = userProfile;
        this.userPaymentMethod = userPaymentMethod;
        this.userExpenseCategory = userExpenseCategory;
        this.userExpenseSubCategory = userExpenseSubCategory;
    }
}
