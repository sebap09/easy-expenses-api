package com.easyexpenses.api.mappers;

import com.easyexpenses.api.dtos.userprofile.Category;
import com.easyexpenses.api.dtos.userprofile.PaymentMethod;
import com.easyexpenses.api.dtos.userprofile.SubCategory;
import com.easyexpenses.api.dtos.userprofile.UserProfileResponse;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserProfileMapper {
    public UserProfileResponse toResponse(List<UserPaymentMethod> userPaymentMethods, List<UserExpenseCategory> userExpenseCategories, List<UserExpenseSubCategory> userExpenseSubCategories) {
        List<PaymentMethod> paymentMethods = userPaymentMethods.stream()
                .map(userPaymentMethod -> new PaymentMethod(
                        userPaymentMethod.getId(),
                        userPaymentMethod.getName()))
                .toList();

        List<Category> categories = userExpenseCategories.stream()
                .map(userExpenseCategory -> new Category(
                        userExpenseCategory.getId(),
                        userExpenseCategory.getName(),
                        userExpenseSubCategories.stream()
                                .filter(userExpenseSubCategory -> Objects.equals(
                                        userExpenseSubCategory.getUserExpenseCategory().getId(),
                                        userExpenseCategory.getId()))
                                .map(userExpenseSubCategory -> new SubCategory(
                                        userExpenseSubCategory.getId(),
                                        userExpenseSubCategory.getUserExpenseCategory().getId(),
                                        userExpenseSubCategory.getName()))
                                .toList()))
                .toList();


        return new UserProfileResponse(paymentMethods, categories);
    }
}
