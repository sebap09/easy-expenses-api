package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserProfileUserId(Long userId);

    Page<Expense> findByUserProfileUserId(Long userId, Pageable pageable);
}
