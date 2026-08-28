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
                    .identityIssuer("https://auth.example.com/realms/myrealm")
                    .identitySubject("7f3a9c21-6b84-4d17-a5e2-9c8f1b7d6043")
                    .build();

            UserProfile userProfile = new UserProfileBuilder()
                    .user(user)
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

            userRepository.save(user);
            paymentMethodRepository.save(card);
            userExpenseCategoryRepository.save(userExpenseCategory);
            userExpenseSubCategoryRepository.save(userExpenseSubCategory);
        };
    }
}