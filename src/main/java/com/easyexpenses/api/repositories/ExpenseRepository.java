package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("""
        SELECT e
        FROM Expense e
        WHERE e.userProfile.userId = :userId
        ORDER BY e.id
    """)
    List<Expense> findAllByUserId(Long userId);
}
