package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExpenseCategoryRepository extends JpaRepository<UserExpenseCategory, Long> {
}
