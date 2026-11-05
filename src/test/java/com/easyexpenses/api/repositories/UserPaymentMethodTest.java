package com.easyexpenses.api.repositories;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.entities.*;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataJpaTest
public class UserPaymentMethodTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:18");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPaymentMethodRepository userPaymentMethodRepository;

    @Autowired
    private EntityManager em;

    @Test
    void shouldConvertNameToUppercase() {
        User user = new UserBuilder()
                .identityIssuer("issuer")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();

        UserProfile userProfile = new UserProfile();
        userProfile.setCompleted(false);
        user.setUserProfile(userProfile);

        String name = "Cash";
        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .name(name)
                .build();
        userProfile.addNewUserPaymentMethod(userPaymentMethod);

        userRepository.save(user);
        userPaymentMethodRepository.save(userPaymentMethod);

        UserPaymentMethod savedUserPaymentMethod = userPaymentMethodRepository.findById(userPaymentMethod.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedUserPaymentMethod.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNotUnique() {
        User user = new UserBuilder()
                .identityIssuer("issuer")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();

        UserProfile userProfile = new UserProfile();
        userProfile.setCompleted(false);
        user.setUserProfile(userProfile);

        String name = "Cash";
        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .name(name)
                .build();
        userProfile.addNewUserPaymentMethod(userPaymentMethod);

        userRepository.save(user);
        userPaymentMethodRepository.save(userPaymentMethod);

        em.flush();
        em.clear();

        UserPaymentMethod duplicateUserPaymentMethod = new UserPaymentMethodBuilder()
                .name(name)
                .build();
        userProfile.addNewUserPaymentMethod(duplicateUserPaymentMethod);

        assertThrows(ConstraintViolationException.class, () -> {
            userPaymentMethodRepository.save(duplicateUserPaymentMethod);
            em.flush();
            em.clear();
        });
    }

    @Test
    void shouldAllowTheSameNameForDifferentUsers() {
        User firstUser = new UserBuilder()
                .identityIssuer("issuer")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();

        UserProfile firstUserProfile = new UserProfile();
        firstUserProfile.setCompleted(false);
        firstUser.setUserProfile(firstUserProfile);

        String name = "Cash";
        UserPaymentMethod firstUserPaymentMethod = new UserPaymentMethodBuilder()
                .name(name)
                .build();
        firstUserProfile.addNewUserPaymentMethod(firstUserPaymentMethod);

        userRepository.save(firstUser);
        userPaymentMethodRepository.save(firstUserPaymentMethod);

        User secondUser = new UserBuilder()
                .identityIssuer("issuer")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();

        UserProfile secondUserProfile = new UserProfile();
        secondUserProfile.setCompleted(false);
        secondUser.setUserProfile(secondUserProfile);

        UserPaymentMethod secondUserPaymentMethod = new UserPaymentMethodBuilder()
                .name(name)
                .build();
        secondUserProfile.addNewUserPaymentMethod(secondUserPaymentMethod);

        userRepository.save(secondUser);
        userPaymentMethodRepository.save(secondUserPaymentMethod);

        em.flush();
        em.clear();

        UserPaymentMethod savedFirstUserPaymentMethod = userPaymentMethodRepository.findById(firstUserPaymentMethod.getId()).orElseThrow();
        UserPaymentMethod savedSecondUserPaymentMethod = userPaymentMethodRepository.findById(secondUserPaymentMethod.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedFirstUserPaymentMethod.getName());
        assertEquals(name.toUpperCase(), savedSecondUserPaymentMethod.getName());
    }

}
