package com.easyexpenses.api.dtos.userprofile.response;


import java.util.List;

public record UserProfileResponse(List<PaymentMethodRes> paymentMethods, List<CategoryRes> categories) {
}
