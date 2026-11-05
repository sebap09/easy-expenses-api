package com.easyexpenses.api.dtos.userprofile.response;


import java.util.List;

public record CompletedUserProfileResponse(boolean isCompleted, List<PaymentMethodRes> paymentMethods, List<CategoryRes> categories) implements UserProfileResponse{
}