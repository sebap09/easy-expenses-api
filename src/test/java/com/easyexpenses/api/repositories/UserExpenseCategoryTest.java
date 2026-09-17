package com.easyexpenses.api.repositories;

import com.easyexpenses.api.builders.UserProfileBuilder;
import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserExpenseCategory;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserExpenseCategoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserExpenseCategoryRepository userExpenseCategoryRepository;

    @Autowired
    private EntityManager em;

    @Test
    void shouldConvertNameToUppercase() {
        UserProfile userProfile = new UserProfileBuilder()
                .user()
                .build();

        String name = "Shopping";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(userProfile)
                .name(name)
                .build();

        userRepository.save(userProfile.getUser());
        userExpenseCategoryRepository.save(userExpenseCategory);

        UserExpenseCategory savedUserExpenseCategory = userExpenseCategoryRepository.findById(userExpenseCategory.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedUserExpenseCategory.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNotUnique() {
        //entities & relationships data
        UserProfile userProfile = new UserProfileBuilder()
                .user()
                .build();

        String name = "Shopping";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(userProfile)
                .name(name)
                .build();

        userRepository.save(userProfile.getUser());
        userExpenseCategoryRepository.save(userExpenseCategory);

        em.flush();
        em.clear();

        UserExpenseCategory duplicateUserExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(userProfile)
                .name(name)
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            userExpenseCategoryRepository.save(duplicateUserExpenseCategory);
            em.flush();
            em.clear();
        });
    }

    @Test
    void shouldAllowTheSameNameForDifferentUsers() {
        UserProfile firstUser = new UserProfileBuilder()
                .user()
                .build();

        String name = "Shopping";
        UserExpenseCategory firstUserExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(firstUser)
                .name(name)
                .build();

        userRepository.save(firstUser.getUser());
        userExpenseCategoryRepository.save(firstUserExpenseCategory);

        UserProfile secondUser = new UserProfileBuilder()
                .user()
                .build();
        UserExpenseCategory secondUserExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(secondUser)
                .name(name)
                .build();

        userRepository.save(secondUser.getUser());
        userExpenseCategoryRepository.save(secondUserExpenseCategory);

        em.flush();
        em.clear();

        UserExpenseCategory firstSavedUserExpenseCategory = userExpenseCategoryRepository.findById(firstUserExpenseCategory.getId()).orElseThrow();
        UserExpenseCategory secondSavedUserExpenseCategory = userExpenseCategoryRepository.findById(secondUserExpenseCategory.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), firstSavedUserExpenseCategory.getName());
        assertEquals(name.toUpperCase(), secondSavedUserExpenseCategory.getName());
    }
}
