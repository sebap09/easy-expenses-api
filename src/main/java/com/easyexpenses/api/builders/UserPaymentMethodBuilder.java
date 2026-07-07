package com.easyexpenses.api.builders;

import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserPaymentMethod;

public class UserPaymentMethodBuilder {

    private Long id;
    private String name = "Cash";
    private UserProfile userProfile;

    public UserPaymentMethodBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserPaymentMethodBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserPaymentMethodBuilder userProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        return this;
    }

    public UserPaymentMethod build() {
        UserPaymentMethod paymentMethod = new UserPaymentMethod();
        paymentMethod.setId(id);
        paymentMethod.setName(name);

        if (userProfile != null) {
            userProfile.addNewUserPaymentMethod(paymentMethod);
        }
        return paymentMethod;
    }
}