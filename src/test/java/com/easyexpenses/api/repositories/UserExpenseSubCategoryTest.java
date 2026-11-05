package com.easyexpenses.api.repositories;

import com.easyexpenses.api.builders.ExpenseFixtureBuilder;
import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.builders.UserExpenseSubCategoryBuilder;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
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
public class UserExpenseSubCategoryTest {

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

        String name = "Grocery";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .name("UEC")
                .build();
        userProfile.addNewUserCategory(userExpenseCategory);

        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .name(name)
                .build();
        userExpenseCategory.addNewSubCategoryRelatedWithThisCategory(userExpenseSubCategory);

        userRepository.save(user);
        UserExpenseCategory savedUserExpenseCategory=userExpenseCategoryRepository.save(userExpenseCategory);

        UserExpenseSubCategory savedUserExpenseSubCategory = savedUserExpenseCategory.getSubCategoriesRelatedWithThisCategory().stream().findFirst().orElseThrow();
        assertEquals(name.toUpperCase(), savedUserExpenseSubCategory.getName());
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

        String name = "Grocery";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .name("UEC")
                .build();
        userProfile.addNewUserCategory(userExpenseCategory);

        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .name(name)
                .build();
        userExpenseCategory.addNewSubCategoryRelatedWithThisCategory(userExpenseSubCategory);

        userRepository.save(user);

        UserExpenseSubCategory duplicateUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .name(name)
                .build();
        userExpenseCategory.addNewSubCategoryRelatedWithThisCategory(duplicateUserExpenseSubCategory);

        assertThrows(ConstraintViolationException.class, () -> {
            userExpenseCategoryRepository.save(userExpenseCategory);
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

        String name = "Grocery";
        UserExpenseCategory firstUserExpenseCategory = new UserExpenseCategoryBuilder()
                .name("UEC")
                .build();
        firstUserProfile.addNewUserCategory(firstUserExpenseCategory);

        UserExpenseSubCategory firstUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .name(name)
                .build();
        firstUserExpenseCategory.addNewSubCategoryRelatedWithThisCategory(firstUserExpenseSubCategory);

        userRepository.save(firstUser);
        UserExpenseCategory firstSavedUserExpenseCategory=userExpenseCategoryRepository.save(firstUserExpenseCategory);

        User secondUser = new UserBuilder()
                .identityIssuer("issuer")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();

        UserProfile secondUserProfile = new UserProfile();
        secondUserProfile.setCompleted(false);
        secondUser.setUserProfile(secondUserProfile);

        UserExpenseCategory secondUserExpenseCategory = new UserExpenseCategoryBuilder()
                .name("UEC")
                .build();
        secondUserProfile.addNewUserCategory(secondUserExpenseCategory);

        UserExpenseSubCategory secondUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .name(name)
                .build();
        secondUserExpenseCategory.addNewSubCategoryRelatedWithThisCategory(secondUserExpenseSubCategory);

        userRepository.save(secondUser);
        UserExpenseCategory secondSavedUserExpenseCategory=userExpenseCategoryRepository.save(secondUserExpenseCategory);

        UserExpenseSubCategory firstSavedUserExpenseSubCategory = firstSavedUserExpenseCategory.getSubCategoriesRelatedWithThisCategory().stream().findFirst().orElseThrow();
        UserExpenseSubCategory secondSavedUserExpenseSubCategory = secondSavedUserExpenseCategory.getSubCategoriesRelatedWithThisCategory().stream().findFirst().orElseThrow();
        assertEquals(name.toUpperCase(), firstSavedUserExpenseSubCategory.getName());
        assertEquals(name.toUpperCase(), secondSavedUserExpenseSubCategory.getName());
    }
}
