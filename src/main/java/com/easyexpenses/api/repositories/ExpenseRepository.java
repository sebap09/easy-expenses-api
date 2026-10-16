package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("""
        SELECT e
        FROM Expense e
        WHERE e.userProfile.userId = :userId
    """)
    Page<Expense> findAllByUserIdWithPagination(Long userId, Pageable pageable);
}
