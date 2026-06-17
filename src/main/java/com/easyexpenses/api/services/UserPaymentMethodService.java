package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.repositories.UserPaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPaymentMethodService {
    private final UserPaymentMethodRepository userPaymentMethodRepository;

    @Autowired
    public UserPaymentMethodService(UserPaymentMethodRepository userPaymentMethodRepository) {
        this.userPaymentMethodRepository = userPaymentMethodRepository;
    }

    public UserPaymentMethod getUserPaymentMethod(Long id){
        return userPaymentMethodRepository.findById(id).orElseThrow();
    }
}
