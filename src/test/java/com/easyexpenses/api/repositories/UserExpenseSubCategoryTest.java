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

        userRepository.save(userProfile.getUser());
        UserExpenseCategory savedUserExpenseCategory=userExpenseCategoryRepository.save(userExpenseCategory);

        UserExpenseSubCategory savedUserExpenseSubCategory = savedUserExpenseCategory.getSubCategoriesRelatedWithThisCategory().stream().findFirst().orElseThrow();
        assertEquals(name.toUpperCase(), savedUserExpenseSubCategory.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNotUnique() {
        //entities & relationships data
        UserProfile userProfile = new UserProfileBuilder()
                .user()
                .build();

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

        userRepository.save(userProfile.getUser());
        userExpenseCategoryRepository.save(userExpenseCategory);

        UserExpenseSubCategory duplicateUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(userProfile)
                .name(name)
                .userExpenseCategory(userExpenseCategory)
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            userExpenseCategoryRepository.save(userExpenseCategory);
            em.flush();
            em.clear();
        });
    }

    @Test
    void shouldAllowTheSameNameForDifferentUsers() {
        UserProfile firstUser = new UserProfileBuilder()
                .user()
                .build();

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

        userRepository.save(firstUser.getUser());
        UserExpenseCategory firstSavedUserExpenseCategory=userExpenseCategoryRepository.save(firstUserExpenseCategory);

        UserProfile secondUser = new UserProfileBuilder()
                .user()
                .build();
        UserExpenseCategory secondUserExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(secondUser)
                .name("Travel")
                .build();
        UserExpenseSubCategory secondUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(secondUser)
                .name(name)
                .userExpenseCategory(secondUserExpenseCategory)
                .build();

        userRepository.save(secondUser.getUser());
        UserExpenseCategory secondSavedUserExpenseCategory=userExpenseCategoryRepository.save(secondUserExpenseCategory);

        UserExpenseSubCategory firstSavedUserExpenseSubCategory = firstSavedUserExpenseCategory.getSubCategoriesRelatedWithThisCategory().stream().findFirst().orElseThrow();
        UserExpenseSubCategory secondSavedUserExpenseSubCategory = secondSavedUserExpenseCategory.getSubCategoriesRelatedWithThisCategory().stream().findFirst().orElseThrow();
        assertEquals(name.toUpperCase(), firstSavedUserExpenseSubCategory.getName());
        assertEquals(name.toUpperCase(), secondSavedUserExpenseSubCategory.getName());
    }
}
