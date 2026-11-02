package com.easyexpenses.api.services;

import com.easyexpenses.api.dtos.userprofile.request.UserProfileRequest;
import com.easyexpenses.api.dtos.userprofile.response.UserProfileResponse;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.errors.ErrorCode;
import com.easyexpenses.api.errors.ValidationException;
import com.easyexpenses.api.mappers.UserProfileMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {
    private final UserService userService;
    private final UserProfileMapper userProfileMapper;
    private final UserPaymentMethodService userPaymentMethodService;
    private final UserExpenseCategoryService userExpenseCategoryService;

    public UserProfileService(UserService userService, UserProfileMapper userProfileMapper, UserPaymentMethodService userPaymentMethodService, UserExpenseCategoryService userExpenseCategoryService) {
        this.userService = userService;
        this.userProfileMapper = userProfileMapper;
        this.userPaymentMethodService = userPaymentMethodService;
        this.userExpenseCategoryService = userExpenseCategoryService;
    }

    public UserProfile findOrCreateUser(Jwt jwt){
        return userService.findOrCreateUser(jwt);
    }

    public UserProfileResponse getUserProfileData(UserProfile userProfile){
        Long userId = userProfile.getUserId();

        List<UserPaymentMethod> paymentMethods = userPaymentMethodService.getAllUserPaymentMethods(userId);
        List<UserExpenseCategory> categories = userExpenseCategoryService.getAllUserExpenseCategories(userId);

        return userProfileMapper.toResponse(paymentMethods, categories);
    }

    public UserProfileResponse setUserProfileData(UserProfile userProfile, UserProfileRequest userProfileRequest) {
        if (!isConfigEmpty(userProfile))
            throw new ValidationException("Config already exists for this user", ErrorCode.CONFIG_ALREADY_EXISTS);

        List<UserPaymentMethod> paymentMethods = userProfileMapper.fromRequestToPaymentMethods(userProfile, userProfileRequest);
        List<UserExpenseCategory> categories = userProfileMapper.fromRequestToCategories(userProfile, userProfileRequest);

        List<UserPaymentMethod> savedPaymentMethods = userPaymentMethodService.saveAll(paymentMethods);
        List<UserExpenseCategory> savedCategories = userExpenseCategoryService.saveAll(categories);

        return userProfileMapper.toResponse(savedPaymentMethods, savedCategories);
    }

    private boolean isConfigEmpty(UserProfile userProfile){
        UserProfileResponse userProfileResponse = getUserProfileData(userProfile);
        return userProfileResponse.categories().isEmpty() && userProfileResponse.paymentMethods().isEmpty();
    }
}
