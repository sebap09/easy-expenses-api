package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.repositories.UserPaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPaymentMethodService {
    private final UserPaymentMethodRepository userPaymentMethodRepository;

    @Autowired
    public UserPaymentMethodService(UserPaymentMethodRepository userPaymentMethodRepository) {
        this.userPaymentMethodRepository = userPaymentMethodRepository;
    }

    public List<UserPaymentMethod> getAllUserPaymentMethods(Long userId) {
        return userPaymentMethodRepository.findAllByUserId(userId);
    }

    public List<UserPaymentMethod> saveAll(List<UserPaymentMethod> paymentMethods) {
        return userPaymentMethodRepository.saveAll(paymentMethods);
    }
}
