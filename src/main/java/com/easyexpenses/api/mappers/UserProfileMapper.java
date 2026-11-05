package com.easyexpenses.api.mappers;


import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.builders.UserExpenseSubCategoryBuilder;
import com.easyexpenses.api.builders.UserPaymentMethodBuilder;
import com.easyexpenses.api.dtos.userprofile.request.CategoryReq;
import com.easyexpenses.api.dtos.userprofile.request.UserProfileRequest;
import com.easyexpenses.api.dtos.userprofile.response.*;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.entities.UserProfile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserProfileMapper {
    public CompletedUserProfileResponse toCompletedUserProfileResponse(UserProfile userProfile, Set<UserExpenseCategory> userExpenseCategories) {
        List<PaymentMethodRes> paymentMethods = userProfile.getUserPaymentMethods().stream()
                .map(userPaymentMethod -> new PaymentMethodRes(
                        userPaymentMethod.getId(),
                        userPaymentMethod.getName()))
                .toList();

        List<CategoryRes> categories = userExpenseCategories.stream()
                .map(userExpenseCategory -> new CategoryRes(
                        userExpenseCategory.getId(),
                        userExpenseCategory.getName(),
                        userExpenseCategory.getSubCategoriesRelatedWithThisCategory().stream()
                                .map(userExpenseSubCategory -> new SubCategoryRes(
                                        userExpenseSubCategory.getId(),
                                        userExpenseSubCategory.getUserExpenseCategory().getId(),
                                        userExpenseSubCategory.getName()))
                                .toList()))
                .toList();


        return new CompletedUserProfileResponse(userProfile.isCompleted(), paymentMethods, categories);
    }

    public NotCompletedUserProfileResponse toNotCompletedUserProfileResponse(UserProfile userProfile){
        return new NotCompletedUserProfileResponse(userProfile.isCompleted());
    }
    public Set<UserPaymentMethod> fromRequestToPaymentMethods(UserProfileRequest userProfileRequest) {
        return userProfileRequest.paymentMethods().stream().map(paymentMethod -> new UserPaymentMethodBuilder()
                .name(paymentMethod)
                .build()).collect(Collectors.toSet());
    }

    public Set<UserExpenseCategory> fromRequestToCategories(UserProfileRequest userProfileRequest) {
        Set<UserExpenseCategory> categories = new HashSet<>();
        for(CategoryReq category: userProfileRequest.categories()){
            UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                    .name(category.name())
                    .build();

            for(String subCategory: category.subCategories()){
                UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                        .name(subCategory)
                        .build();

                userExpenseCategory.addNewSubCategoryRelatedWithThisCategory(userExpenseSubCategory);
            }

            categories.add(userExpenseCategory);
        }

        return categories;
    }
}
