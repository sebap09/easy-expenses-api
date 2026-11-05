package com.easyexpenses.api.services;

import com.easyexpenses.api.dtos.userprofile.request.UserProfileRequest;
import com.easyexpenses.api.dtos.userprofile.response.CompletedUserProfileResponse;
import com.easyexpenses.api.dtos.userprofile.response.UserProfileResponse;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.errors.ErrorCode;
import com.easyexpenses.api.errors.ValidationException;
import com.easyexpenses.api.mappers.UserProfileMapper;
import com.easyexpenses.api.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserProfileService {
    private static final int MAX_PAYMENT_METHODS_LIMIT = 20;
    private static final int MAX_CATEGORIES_LIMIT = 30;
    private static final int MAX_SUB_CATEGORIES_LIMIT = 20;

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserExpenseCategoryService userExpenseCategoryService;

    public UserProfileService(UserProfileRepository userProfileRepository, UserProfileMapper userProfileMapper, UserExpenseCategoryService userExpenseCategoryService) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.userExpenseCategoryService = userExpenseCategoryService;
    }

    public UserProfile getUserProfileById(Long userId){
        return userProfileRepository.findById(userId).orElseThrow();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileData(Long userId){
        UserProfile userProfile = getUserProfileById(userId);
        if(!isProfileAlreadyCompleted(userProfile))
            return userProfileMapper.toNotCompletedUserProfileResponse(userProfile);

        Set<UserExpenseCategory> categories = userExpenseCategoryService.getAllWithUserSubExpenseCategoriesByUserId(userId);
        return userProfileMapper.toCompletedUserProfileResponse(userProfile, categories);
    }

    @Transactional
    public CompletedUserProfileResponse setUserProfileData(Long userId, UserProfileRequest userProfileRequest) {
        validateLimitOfUserProfileConfig(userProfileRequest);

        UserProfile userProfile = getUserProfileById(userId);
        validateIfUserProfileIsAlreadyCompleted(userProfile);

        Set<UserPaymentMethod> paymentMethods = userProfileMapper.fromRequestToPaymentMethods(userProfileRequest);
        Set<UserExpenseCategory> categories = userProfileMapper.fromRequestToCategories(userProfileRequest);

        paymentMethods.forEach(userProfile::addNewUserPaymentMethod);
        categories.forEach(userProfile::addNewUserCategory);

        userProfile.setCompleted(true);

        UserProfile completedUserProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toCompletedUserProfileResponse(completedUserProfile, completedUserProfile.getUserExpenseCategories());
    }

    private void validateLimitOfUserProfileConfig(UserProfileRequest userProfileRequest){
        if (isMaxNumberOfUserPaymentMethodsExceeded(userProfileRequest))
            throw new ValidationException(String.format("Limit [%d] of user payment methods was exceeded!", MAX_PAYMENT_METHODS_LIMIT), ErrorCode.MAX_NUMBER_OF_USER_PAYMENT_METHODS_EXCEEDED);

        if (isMaxNumberOfUserExpenseCategoriesExceeded(userProfileRequest))
            throw new ValidationException(String.format("Limit [%d] of user expense categories was exceeded!", MAX_CATEGORIES_LIMIT), ErrorCode.MAX_NUMBER_OF_USER_EXPENSE_CATEGORIES_EXCEEDED);

        validateIfMaxNumberOfUserExpenseSubCategoriesExceeded(userProfileRequest);
    }

    private boolean isMaxNumberOfUserPaymentMethodsExceeded(UserProfileRequest userProfileRequest){
        return userProfileRequest.paymentMethods().size() > MAX_PAYMENT_METHODS_LIMIT;
    }

    private boolean isMaxNumberOfUserExpenseCategoriesExceeded(UserProfileRequest userProfileRequest){
        return userProfileRequest.categories().size() > MAX_CATEGORIES_LIMIT;
    }

    private void validateIfMaxNumberOfUserExpenseSubCategoriesExceeded(UserProfileRequest userProfileRequest){
        userProfileRequest.categories().stream().filter(
                        categoryReq -> categoryReq.subCategories().size() > MAX_SUB_CATEGORIES_LIMIT)
                .findFirst()
                .ifPresent(
                        invalidCategory -> {
                            throw new ValidationException(
                                    String.format("Limit [%d] of user expense sub categories was exceeded for [%s] user expense category!", MAX_SUB_CATEGORIES_LIMIT, invalidCategory),
                                    ErrorCode.MAX_NUMBER_OF_USER_EXPENSE_SUB_CATEGORIES_EXCEEDED);
                        });
    }

    private void validateIfUserProfileIsAlreadyCompleted(UserProfile userProfile){
        if (isProfileAlreadyCompleted(userProfile))
            throw new ValidationException("User Profile is already completed!", ErrorCode.USER_PROFILE_IS_ALREADY_COMPLETED);
    }

    private boolean isProfileAlreadyCompleted(UserProfile userProfile){
        return userProfile.isCompleted();
    }
}
