package com.easyexpenses.api.config;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.builders.UserPaymentMethodBuilder;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.repositories.UserPaymentMethodRepository;
import com.easyexpenses.api.repositories.UserRepository;
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
            UserPaymentMethodRepository paymentMethodRepository
    ) {
        return args -> {
            User user = new UserBuilder()
                     .username("user1")
                    .build();

            UserPaymentMethod card = new UserPaymentMethodBuilder()
                    .user(user)
                    .name("CARD")
                    .build();

            userRepository.save(user);
            paymentMethodRepository.save(card);
        };
    }
}