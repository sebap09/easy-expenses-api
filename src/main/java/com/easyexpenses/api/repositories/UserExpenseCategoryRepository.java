package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserExpenseCategoryRepository extends JpaRepository<UserExpenseCategory, Long> {
    @Query("""
        SELECT uec
        FROM UserExpenseCategory uec
        JOIN FETCH uec.subCategoriesRelatedWithThisCategory uesc
        WHERE uec.userProfile.userId = :userId
    """)
    Set<UserExpenseCategory> joinFetchCategoriesAndSubcategoriesAndFilterByUserId(Long userId);

    Optional<UserExpenseCategory> findByIdAndUserProfileUserId(Long id, Long userId);
}
