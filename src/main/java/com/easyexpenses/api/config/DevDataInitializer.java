package com.easyexpenses.api.config;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.builders.UserExpenseSubCategoryBuilder;
import com.easyexpenses.api.builders.UserPaymentMethodBuilder;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.repositories.UserExpenseCategoryRepository;
import com.easyexpenses.api.repositories.UserExpenseSubCategoryRepository;
import com.easyexpenses.api.repositories.UserPaymentMethodRepository;
import com.easyexpenses.api.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevDataInitializer {

    @Bean
    CommandLineRunner init(
            UserRepository userRepository,
            UserPaymentMethodRepository paymentMethodRepository,
            UserExpenseCategoryRepository userExpenseCategoryRepository,
            UserExpenseSubCategoryRepository userExpenseSubCategoryRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User user = new UserBuilder()
                     .username("defaultUser")
                    .password(passwordEncoder.encode("password"))
                    .build();

            UserPaymentMethod card = new UserPaymentMethodBuilder()
                    .user(user)
                    .name("CARD")
                    .build();

            UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                    .user(user)
                    .name("Zakupy")
                    .build();

            UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                    .user(user)
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