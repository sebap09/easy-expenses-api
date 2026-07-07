package com.easyexpenses.api.config;

import com.easyexpenses.api.builders.UserProfileBuilder;
import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.builders.UserExpenseSubCategoryBuilder;
import com.easyexpenses.api.builders.UserPaymentMethodBuilder;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.repositories.UserExpenseCategoryRepository;
import com.easyexpenses.api.repositories.UserExpenseSubCategoryRepository;
import com.easyexpenses.api.repositories.UserPaymentMethodRepository;
import com.easyexpenses.api.repositories.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevDataInitializer {

    @Bean
    CommandLineRunner init(
            UserProfileRepository userProfileRepository,
            UserPaymentMethodRepository paymentMethodRepository,
            UserExpenseCategoryRepository userExpenseCategoryRepository,
            UserExpenseSubCategoryRepository userExpenseSubCategoryRepository
    ) {
        return args -> {
            UserProfile userProfile = new UserProfileBuilder()
                    .build();

            UserPaymentMethod card = new UserPaymentMethodBuilder()
                    .userProfile(userProfile)
                    .name("CARD")
                    .build();

            UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                    .userProfile(userProfile)
                    .name("Zakupy")
                    .build();

            UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                    .userProfile(userProfile)
                    .name("Jedzenie/kosmetyki/chemia/inne")
                    .userExpenseCategory(userExpenseCategory)
                    .build();

            userProfileRepository.save(userProfile);
            paymentMethodRepository.save(card);
            userExpenseCategoryRepository.save(userExpenseCategory);
            userExpenseSubCategoryRepository.save(userExpenseSubCategory);
        };
    }
}