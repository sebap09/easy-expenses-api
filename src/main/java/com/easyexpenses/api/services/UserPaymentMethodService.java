package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.errors.ErrorCode;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.repositories.UserPaymentMethodRepository;
import org.springframework.stereotype.Service;

@Service
public class UserPaymentMethodService {
    private final UserPaymentMethodRepository userPaymentMethodRepository;

    public UserPaymentMethodService(UserPaymentMethodRepository userPaymentMethodRepository) {
        this.userPaymentMethodRepository = userPaymentMethodRepository;
    }

    public UserPaymentMethod getByIdAndUserId(Long id, Long userId){
        return userPaymentMethodRepository.findByIdAndUserProfileUserId(id, userId).orElseThrow(() ->
                new ResourceNotFoundException("User Payment Method was not found for this user!", ErrorCode.USER_PAYMENT_METHOD_NOT_FOUND));
    }
}
