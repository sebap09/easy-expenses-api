package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserExpenseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserExpenseSubCategoryRepository extends JpaRepository<UserExpenseSubCategory, Long> {
    @Query("""
        SELECT s
        FROM UserExpenseSubCategory s
        WHERE s.userProfile.userId = :userId
    """)
    List<UserExpenseSubCategory> findAllByUserId(Long userId);
}
