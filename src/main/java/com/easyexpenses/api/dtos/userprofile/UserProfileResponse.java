package com.easyexpenses.api.dtos.userprofile;


import java.util.List;

public record UserProfileResponse(List<PaymentMethod> paymentMethods, List<Category> categories) {
}
