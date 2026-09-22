package com.easyexpenses.api.services;

import com.easyexpenses.api.dtos.userprofile.UserProfileResponse;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.mappers.UserProfileMapper;
import com.easyexpenses.api.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final UserProfileMapper userProfileMapper;
    private final UserPaymentMethodService userPaymentMethodService;
    private final UserExpenseCategoryService userExpenseCategoryService;
    private final UserExpenseSubCategoryService userExpenseSubCategoryService;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, UserService userService, UserProfileMapper userProfileMapper, UserPaymentMethodService userPaymentMethodService, UserExpenseCategoryService userExpenseCategoryService, UserExpenseSubCategoryService userExpenseSubCategoryService) {
        this.userProfileRepository = userProfileRepository;
        this.userService = userService;
        this.userProfileMapper = userProfileMapper;
        this.userPaymentMethodService = userPaymentMethodService;
        this.userExpenseCategoryService = userExpenseCategoryService;
        this.userExpenseSubCategoryService = userExpenseSubCategoryService;
    }

    public UserProfile getUserProfile(Long id){
        return userProfileRepository.findByUserId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + id));
    }

    public UserProfileResponse getUserProfileData(Jwt jwt){
        Long userId = userService.findOrCreateUser(jwt).getUserId();

        List<UserPaymentMethod> paymentMethods = userPaymentMethodService.getAllUserPaymentMethods(userId);
        List<UserExpenseCategory> categories = userExpenseCategoryService.getAllUserExpenseCategories(userId);
        List<UserExpenseSubCategory> subCategories = userExpenseSubCategoryService.getAllUserExpenseSubCategories(userId);

        return userProfileMapper.toResponse(paymentMethods, categories, subCategories);
    }
}
