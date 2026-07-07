package com.easyexpenses.api.repositories;

import com.easyexpenses.api.builders.UserProfileBuilder;
import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.builders.UserExpenseSubCategoryBuilder;
import com.easyexpenses.api.entities.UserProfile;
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
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserExpenseCategoryRepository userExpenseCategoryRepository;

    @Autowired
    private UserExpenseSubCategoryRepository userExpenseSubCategoryRepository;

    @Autowired
    private EntityManager em;

    @Test
    void shouldConvertNameToUppercase() {
        UserProfile userProfile = new UserProfileBuilder().build();
        String name = "Grocery";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(userProfile)
                .name("Shopping")
                .build();
        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(userProfile)
                .name(name)
                .userExpenseCategory(userExpenseCategory)
                .build();

        userProfileRepository.save(userProfile);
        userExpenseCategoryRepository.save(userExpenseCategory);
        userExpenseSubCategoryRepository.save(userExpenseSubCategory);

        UserExpenseSubCategory savedUserExpenseSubCategory = userExpenseSubCategoryRepository.findById(userExpenseSubCategory.getId()).orElseThrow();
        assertEquals(name.toUpperCase(), savedUserExpenseSubCategory.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNotUnique() {
        //entities & relationships data
        UserProfile userProfile = new UserProfileBuilder().build();
        String name = "Grocery";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(userProfile)
                .name("Shopping")
                .build();
        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(userProfile)
                .name(name)
                .userExpenseCategory(userExpenseCategory)
                .build();

        userProfileRepository.save(userProfile);
        userExpenseCategoryRepository.save(userExpenseCategory);
        userExpenseSubCategoryRepository.save(userExpenseSubCategory);

        em.flush();
        em.clear();

        UserExpenseSubCategory duplicateUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(userProfile)
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
        UserProfile firstUser = new UserProfileBuilder().build();
        String name = "Grocery";
        UserExpenseCategory firstUserExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(firstUser)
                .name("Shopping")
                .build();
        UserExpenseSubCategory firstUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(firstUser)
                .name(name)
                .userExpenseCategory(firstUserExpenseCategory)
                .build();

        userProfileRepository.save(firstUser);
        userExpenseCategoryRepository.save(firstUserExpenseCategory);
        userExpenseSubCategoryRepository.save(firstUserExpenseSubCategory);

        UserProfile secondUser = new UserProfileBuilder().build();
        UserExpenseCategory secondUserExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(secondUser)
                .name("Travel")
                .build();
        UserExpenseSubCategory secondUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(secondUser)
                .name(name)
                .userExpenseCategory(firstUserExpenseCategory)
                .build();

        userProfileRepository.save(secondUser);
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
