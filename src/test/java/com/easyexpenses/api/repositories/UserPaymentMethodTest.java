package com.easyexpenses.api.repositories;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.entities.*;
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
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserPaymentMethodRepository userPaymentMethodRepository;

    @Autowired
    private EntityManager em;

    @Test
    void shouldConvertNameToUppercase() {
        UserProfile userProfile = new UserProfileBuilder().build();
        String name = "Cash";
        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .userProfile(userProfile)
                .name(name)
                .build();

        userProfileRepository.save(userProfile);
        userPaymentMethodRepository.save(userPaymentMethod);

        UserPaymentMethod savedUserPaymentMethod = userPaymentMethodRepository.findById(userPaymentMethod.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedUserPaymentMethod.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNotUnique() {
        UserProfile userProfile = new UserProfileBuilder().build();
        String name = "Cash";
        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .userProfile(userProfile)
                .name(name)
                .build();

        userProfileRepository.save(userProfile);
        userPaymentMethodRepository.save(userPaymentMethod);

        em.flush();
        em.clear();

        UserPaymentMethod duplicateUserPaymentMethod = new UserPaymentMethodBuilder()
                .userProfile(userProfile)
                .name(name)
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            userPaymentMethodRepository.save(duplicateUserPaymentMethod);
            em.flush();
            em.clear();
        });
    }

    @Test
    void shouldAllowTheSameNameForDifferentUsers() {
        UserProfile firstUser = new UserProfileBuilder().build();
        String name = "Cash";
        UserPaymentMethod firstUserPaymentMethod = new UserPaymentMethodBuilder()
                .userProfile(firstUser)
                .name(name)
                .build();

        userProfileRepository.save(firstUser);
        userPaymentMethodRepository.save(firstUserPaymentMethod);

        UserProfile secondUser = new UserProfileBuilder().build();
        UserPaymentMethod secondUserPaymentMethod = new UserPaymentMethodBuilder()
                .userProfile(secondUser)
                .name(name)
                .build();

        userProfileRepository.save(secondUser);
        userPaymentMethodRepository.save(secondUserPaymentMethod);

        em.flush();
        em.clear();

        UserPaymentMethod savedFirstUserPaymentMethod = userPaymentMethodRepository.findById(firstUserPaymentMethod.getId()).orElseThrow();
        UserPaymentMethod savedSecondUserPaymentMethod = userPaymentMethodRepository.findById(secondUserPaymentMethod.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedFirstUserPaymentMethod.getName());
        assertEquals(name.toUpperCase(), savedSecondUserPaymentMethod.getName());
    }

}
