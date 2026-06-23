package com.easyexpenses.api.repositories;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.builders.UserExpenseSubCategoryBuilder;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserExpenseSubCategory;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserExpenseSubCategoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserExpenseCategoryRepository userExpenseCategoryRepository;

    @Autowired
    private UserExpenseSubCategoryRepository userExpenseSubCategoryRepository;

    @Autowired
    private EntityManager em;

    @Test
    void shouldConvertNameToUppercase() {
        User user = new UserBuilder().build();
        String name = "Grocery";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .user(user)
                .name("Shopping")
                .build();
        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .user(user)
                .name(name)
                .userExpenseCategory(userExpenseCategory)
                .build();

        userRepository.save(user);
        userExpenseCategoryRepository.save(userExpenseCategory);
        userExpenseSubCategoryRepository.save(userExpenseSubCategory);

        UserExpenseSubCategory savedUserExpenseSubCategory = userExpenseSubCategoryRepository.findById(userExpenseSubCategory.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedUserExpenseSubCategory.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNotUnique() {
        //entities & relationships data
        User user = new UserBuilder().build();
        String name = "Grocery";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .user(user)
                .name("Shopping")
                .build();
        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .user(user)
                .name(name)
                .userExpenseCategory(userExpenseCategory)
                .build();

        userRepository.save(user);
        userExpenseCategoryRepository.save(userExpenseCategory);
        userExpenseSubCategoryRepository.save(userExpenseSubCategory);

        em.flush();
        em.clear();

        UserExpenseSubCategory duplicateUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .user(user)
                .name(name)
                .userExpenseCategory(userExpenseCategory)
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            userExpenseSubCategoryRepository.save(duplicateUserExpenseSubCategory);
            em.flush();
            em.clear();
        });
    }

    @Test
    void shouldAllowTheSameNameForDifferentUsers() {
        User firstUser = new UserBuilder().build();
        String name = "Grocery";
        UserExpenseCategory firstUserExpenseCategory = new UserExpenseCategoryBuilder()
                .user(firstUser)
                .name("Shopping")
                .build();
        UserExpenseSubCategory firstUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .user(firstUser)
                .name(name)
                .userExpenseCategory(firstUserExpenseCategory)
                .build();

        userRepository.save(firstUser);
        userExpenseCategoryRepository.save(firstUserExpenseCategory);
        userExpenseSubCategoryRepository.save(firstUserExpenseSubCategory);

        User secondUser = new UserBuilder().build();
        UserExpenseCategory secondUserExpenseCategory = new UserExpenseCategoryBuilder()
                .user(secondUser)
                .name("Travel")
                .build();
        UserExpenseSubCategory secondUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .user(secondUser)
                .name(name)
                .userExpenseCategory(firstUserExpenseCategory)
                .build();

        userRepository.save(secondUser);
        userExpenseCategoryRepository.save(secondUserExpenseCategory);
        userExpenseSubCategoryRepository.save(secondUserExpenseSubCategory);

        em.flush();
        em.clear();

        UserExpenseSubCategory firstSavedUserExpenseSubCategory = userExpenseSubCategoryRepository.findById(firstUserExpenseSubCategory.getId()).orElseThrow();
        UserExpenseSubCategory secondSavedUserExpenseSubCategory = userExpenseSubCategoryRepository.findById(secondUserExpenseSubCategory.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), firstSavedUserExpenseSubCategory.getName());
        assertEquals(name.toUpperCase(), secondSavedUserExpenseSubCategory.getName());
    }
}
