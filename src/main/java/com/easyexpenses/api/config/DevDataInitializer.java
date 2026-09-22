package com.easyexpenses.api.config;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevDataInitializer {

    @Bean
    CommandLineRunner init(
            UserRepository userRepository,
            UserPaymentMethodRepository paymentMethodRepository,
            UserExpenseCategoryRepository userExpenseCategoryRepository,
            UserExpenseSubCategoryRepository userExpenseSubCategoryRepository
    ) {
        return args -> {
            User user = new UserBuilder()
                    .identityIssuer("http://localhost:9090/realms/my-realm")
                    .identitySubject("f759323e-5b47-44a6-8d0f-a8b5563cccd1")
                    .build();

            UserProfile userProfile = new UserProfileBuilder()
                    .user(user)
                    .build();

            UserPaymentMethod card = new UserPaymentMethodBuilder()
                    .userProfile(userProfile)
                    .name("CARD")
                    .build();

            UserExpenseCategory firstUserExpenseCategory = new UserExpenseCategoryBuilder()
                    .userProfile(userProfile)
                    .name("Zakupy")
                    .build();

            UserExpenseSubCategory firstSubCategoryForFirstCategory = new UserExpenseSubCategoryBuilder()
                    .userProfile(userProfile)
                    .name("Jedzenie/kosmetyki/chemia/inne")
                    .userExpenseCategory(firstUserExpenseCategory)
                    .build();

            UserExpenseSubCategory secondSubCategoryForFirstCategory = new UserExpenseSubCategoryBuilder()
                    .userProfile(userProfile)
                    .name("Apteka")
                    .userExpenseCategory(firstUserExpenseCategory)
                    .build();

            UserExpenseCategory secondUserExpenseCategory = new UserExpenseCategoryBuilder()
                    .userProfile(userProfile)
                    .name("Transport")
                    .build();

            UserExpenseSubCategory firstSubCategoryForSecondCategory = new UserExpenseSubCategoryBuilder()
                    .userProfile(userProfile)
                    .name("Paliwo")
                    .userExpenseCategory(secondUserExpenseCategory)
                    .build();

            UserExpenseSubCategory secondSubCategoryForSecondCategory = new UserExpenseSubCategoryBuilder()
                    .userProfile(userProfile)
                    .name("Parking")
                    .userExpenseCategory(secondUserExpenseCategory)
                    .build();

            userRepository.save(user);
            paymentMethodRepository.save(card);
            userExpenseCategoryRepository.save(firstUserExpenseCategory);
            userExpenseSubCategoryRepository.save(firstSubCategoryForFirstCategory);
            userExpenseSubCategoryRepository.save(secondSubCategoryForFirstCategory);

            userExpenseCategoryRepository.save(secondUserExpenseCategory);
            userExpenseSubCategoryRepository.save(firstSubCategoryForSecondCategory);
            userExpenseSubCategoryRepository.save(secondSubCategoryForSecondCategory);
        };
    }
}