package com.easyexpenses.api.repositories;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.entities.User;
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
        User user = new UserBuilder().build();
        String name = "Shopping";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .user(user)
                .name(name)
                .build();

        userRepository.save(user);
        userExpenseCategoryRepository.save(userExpenseCategory);

        UserExpenseCategory savedUserExpenseCategory = userExpenseCategoryRepository.findById(userExpenseCategory.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedUserExpenseCategory.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNotUnique() {
        //entities & relationships data
        User user = new UserBuilder().build();
        String name = "Shopping";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .user(user)
                .name(name)
                .build();

        userRepository.save(user);
        userExpenseCategoryRepository.save(userExpenseCategory);

        em.flush();
        em.clear();

        UserExpenseCategory duplicateUserExpenseCategory = new UserExpenseCategoryBuilder()
                .user(user)
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
        User firstUser = new UserBuilder().build();
        String name = "Shopping";
        UserExpenseCategory firstUserExpenseCategory = new UserExpenseCategoryBuilder()
                .user(firstUser)
                .name(name)
                .build();

        userRepository.save(firstUser);
        userExpenseCategoryRepository.save(firstUserExpenseCategory);

        User secondUser = new UserBuilder().build();
        UserExpenseCategory secondUserExpenseCategory = new UserExpenseCategoryBuilder()
                .user(secondUser)
                .name(name)
                .build();

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
