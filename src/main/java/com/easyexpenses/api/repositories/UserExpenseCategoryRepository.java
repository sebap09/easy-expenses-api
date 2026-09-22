package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserExpenseCategoryRepository extends JpaRepository<UserExpenseCategory, Long> {
    @Query("""
        SELECT c
        FROM UserExpenseCategory c
        WHERE c.userProfile.userId = :userId
    """)
    List<UserExpenseCategory> findAllByUserId(Long userId);
}
