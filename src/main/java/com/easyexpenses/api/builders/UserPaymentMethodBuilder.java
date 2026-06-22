package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserPaymentMethod;

public class UserPaymentMethodBuilder {

    private Long id;
    private String name = "Cash";
    private User user;

    public UserPaymentMethodBuilder id(Long id) {
        this.id = id;
        return this;
    }

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
        paymentMethod.setId(id);
        paymentMethod.setName(name);

        if (user != null) {
            user.addNewUserPaymentMethod(paymentMethod);
        }
        return paymentMethod;
    }
}