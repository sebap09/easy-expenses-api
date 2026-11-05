package com.easyexpenses.api.repositories;

import com.easyexpenses.api.builders.ExpenseFixtureBuilder;
import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserExpenseCategory;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataJpaTest
public class UserExpenseCategoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:18");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserExpenseCategoryRepository userExpenseCategoryRepository;

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

        String name = "Shopping";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .name(name)
                .build();
        userProfile.addNewUserCategory(userExpenseCategory);

        userRepository.save(userProfile.getUser());
        userExpenseCategoryRepository.save(userExpenseCategory);

        UserExpenseCategory savedUserExpenseCategory = userExpenseCategoryRepository.findById(userExpenseCategory.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedUserExpenseCategory.getName());
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

        String name = "Shopping";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .name(name)
                .build();
        userProfile.addNewUserCategory(userExpenseCategory);


        UserExpenseCategory duplicateUserExpenseCategory = new UserExpenseCategoryBuilder()
                .name(name)
                .build();
        userProfile.addNewUserCategory(userExpenseCategory);

        assertThrows(ConstraintViolationException.class, () -> {
            userExpenseCategoryRepository.save(duplicateUserExpenseCategory);
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

        String name = "Shopping";
        UserExpenseCategory firstUserExpenseCategory = new UserExpenseCategoryBuilder()
                .name(name)
                .build();
        firstUserProfile.addNewUserCategory(firstUserExpenseCategory);

        userRepository.save(firstUser);
        userExpenseCategoryRepository.save(firstUserExpenseCategory);

        User secondUser = new UserBuilder()
                .identityIssuer("issuer")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();

        UserProfile secondUserProfile = new UserProfile();
        secondUserProfile.setCompleted(false);
        secondUser.setUserProfile(secondUserProfile);

        UserExpenseCategory secondUserExpenseCategory = new UserExpenseCategoryBuilder()
                .name(name)
                .build();
        secondUserProfile.addNewUserCategory(secondUserExpenseCategory);

        userRepository.save(secondUser);
        userExpenseCategoryRepository.save(secondUserExpenseCategory);

        em.flush();
        em.clear();

        UserExpenseCategory firstSavedUserExpenseCategory = userExpenseCategoryRepository.findById(firstUserExpenseCategory.getId()).orElseThrow();
        UserExpenseCategory secondSavedUserExpenseCategory = userExpenseCategoryRepository.findById(secondUserExpenseCategory.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), firstSavedUserExpenseCategory.getName());
        assertEquals(name.toUpperCase(), secondSavedUserExpenseCategory.getName());
    }
}
