package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserExpenseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExpenseSubCategoryRepository extends JpaRepository<UserExpenseSubCategory, Long> {
}
