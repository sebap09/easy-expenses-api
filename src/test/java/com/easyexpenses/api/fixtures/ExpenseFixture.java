package com.easyexpenses.api.fixtures;

import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import lombok.Getter;

@Getter
public class ExpenseFixture {
    private final User user;
    private final UserPaymentMethod userPaymentMethod;
    private final UserExpenseCategory userExpenseCategory;
    private final UserExpenseSubCategory userExpenseSubCategory;


    public ExpenseFixture(User user, UserPaymentMethod userPaymentMethod, UserExpenseCategory userExpenseCategory, UserExpenseSubCategory userExpenseSubCategory) {
        this.user = user;
        this.userPaymentMethod = userPaymentMethod;
        this.userExpenseCategory = userExpenseCategory;
        this.userExpenseSubCategory = userExpenseSubCategory;
    }
}
