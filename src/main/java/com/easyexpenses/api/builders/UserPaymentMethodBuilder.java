package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.UserPaymentMethod;

public class UserPaymentMethodBuilder {
    private String name;

    public UserPaymentMethodBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserPaymentMethod build() {
        UserPaymentMethod paymentMethod = new UserPaymentMethod();
        paymentMethod.setName(name);

        return paymentMethod;
    }
}