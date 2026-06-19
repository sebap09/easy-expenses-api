package com.easyexpenses.api;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.repositories.*;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserPaymentMethodTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPaymentMethodRepository userPaymentMethodRepository;

    @Autowired
    private EntityManager em;

    @Test
    void shouldConvertNameToUppercase() {
        User user = new UserBuilder().build();
        String name = "Cash";
        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .user(user)
                .name(name)
                .build();

        userRepository.save(user);
        userPaymentMethodRepository.save(userPaymentMethod);

        UserPaymentMethod savedUserPaymentMethod = userPaymentMethodRepository.findById(userPaymentMethod.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedUserPaymentMethod.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNotUnique() {
        //entities & relationships data
        User user = new UserBuilder().build();
        String name = "Cash";
        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .user(user)
                .name(name)
                .build();

        userRepository.save(user);
        userPaymentMethodRepository.save(userPaymentMethod);

        em.flush();
        em.clear();

        UserPaymentMethod duplicateUserPaymentMethod = new UserPaymentMethodBuilder()
                .user(user)
                .name(name)
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            userPaymentMethodRepository.save(duplicateUserPaymentMethod);
            em.flush();
            em.clear();
        });
    }

}
