package com.easyexpenses.api.mappers;


import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.builders.UserExpenseSubCategoryBuilder;
import com.easyexpenses.api.builders.UserPaymentMethodBuilder;
import com.easyexpenses.api.dtos.userprofile.request.CategoryReq;
import com.easyexpenses.api.dtos.userprofile.request.UserProfileRequest;
import com.easyexpenses.api.dtos.userprofile.response.CategoryRes;
import com.easyexpenses.api.dtos.userprofile.response.PaymentMethodRes;
import com.easyexpenses.api.dtos.userprofile.response.SubCategoryRes;
import com.easyexpenses.api.dtos.userprofile.response.UserProfileResponse;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.entities.UserProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserProfileMapper {
    public UserProfileResponse toResponse(List<UserPaymentMethod> userPaymentMethods, List<UserExpenseCategory> userExpenseCategories) {
        List<PaymentMethodRes> paymentMethods = userPaymentMethods.stream()
                .map(userPaymentMethod -> new PaymentMethodRes(
                        userPaymentMethod.getId(),
                        userPaymentMethod.getName()))
                .toList();

        List<CategoryRes> categories = userExpenseCategories.stream()
                .map(userExpenseCategory -> new CategoryRes(
                        userExpenseCategory.getId(),
                        userExpenseCategory.getName(),
                        userExpenseCategory.getSubCategoriesRelatedWithThisCategory().stream()
                                .filter(userExpenseSubCategory -> Objects.equals(
                                        userExpenseSubCategory.getUserExpenseCategory().getId(),
                                        userExpenseCategory.getId()))
                                .map(userExpenseSubCategory -> new SubCategoryRes(
                                        userExpenseSubCategory.getId(),
                                        userExpenseSubCategory.getUserExpenseCategory().getId(),
                                        userExpenseSubCategory.getName()))
                                .toList()))
                .toList();


        return new UserProfileResponse(paymentMethods, categories);
    }

    public List<UserPaymentMethod> fromRequestToPaymentMethods(UserProfile userProfile, UserProfileRequest userProfileRequest) {
        return userProfileRequest.paymentMethods().stream().map(paymentMethod -> new UserPaymentMethodBuilder()
                .userProfile(userProfile)
                .name(paymentMethod)
                .build()).toList();
    }

    public List<UserExpenseCategory> fromRequestToCategories(UserProfile userProfile, UserProfileRequest userProfileRequest) {
        List<UserExpenseCategory> categories = new ArrayList<>();
        for(CategoryReq category: userProfileRequest.categories()){
            UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                    .userProfile(userProfile)
                    .name(category.name())
                    .build();
            categories.add(userExpenseCategory);
            for(String subCategory: category.subCategories()){
                new UserExpenseSubCategoryBuilder()
                        .userProfile(userProfile)
                        .name(subCategory)
                        .userExpenseCategory(userExpenseCategory)
                        .build();
            }
        }


        return categories;
    }
}
