package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserExpenseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserExpenseSubCategoryRepository extends JpaRepository<UserExpenseSubCategory, Long> {
    Optional<UserExpenseSubCategory> findByIdAndUserExpenseCategoryId(Long id, Long categoryId);
}
