package com.easyexpenses.api.factory;

import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserPaymentMethod;

public class UserPaymentMethodBuilder {

    private String name = "Cash";
    private User user;

    public UserPaymentMethodBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserPaymentMethodBuilder user(User user) {
        this.user = user;
        return this;
    }

    public UserPaymentMethod build() {
        UserPaymentMethod paymentMethod = new UserPaymentMethod();
        paymentMethod.setName(name);

        if (user != null) {
            user.addNewUserPaymentMethod(paymentMethod);
        }
        return paymentMethod;
    }
}