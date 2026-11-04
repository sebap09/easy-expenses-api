package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserExpenseCategoryRepository extends JpaRepository<UserExpenseCategory, Long> {
    @Query("""
        SELECT uec
        FROM UserExpenseCategory uec
        JOIN FETCH uec.subCategoriesRelatedWithThisCategory uesc
        WHERE uec.userProfile.userId = :userId
    """)
    Set<UserExpenseCategory> joinFetchCategoriesAndSubcategoriesAndFilterByUserId(Long userId);
}
