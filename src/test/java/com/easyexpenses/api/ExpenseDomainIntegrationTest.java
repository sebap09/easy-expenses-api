package com.easyexpenses.api;

import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.factory.ExpenseBuilder;
import com.easyexpenses.api.factory.UserBuilder;
import com.easyexpenses.api.factory.UserPaymentMethodBuilder;
import com.easyexpenses.api.repositories.ExpenseRepository;
import com.easyexpenses.api.repositories.UserPaymentMethodRepository;
import com.easyexpenses.api.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ExpenseDomainIntegrationTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPaymentMethodRepository userPaymentMethodRepository;

    //required for flush() and clear() to be able to test whole database cycle instead of only persistence context
    @Autowired
    private EntityManager em;

    @Test
    void shouldPersistExpenseWithDomain() {
        //entities & relationships data
        User user = new UserBuilder().build();

        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .user(user)
                .build();

        Expense expense = new ExpenseBuilder()
                .user(user)
                .userPaymentMethod(userPaymentMethod)
                .build();

        //saving entities to db
        userRepository.save(user);
        userPaymentMethodRepository.save(userPaymentMethod);
        expenseRepository.save(expense);

        //push from persistence context to actual db
        em.flush();
        em.clear();

        Expense savedExpense = expenseRepository.findById(expense.getId()).orElseThrow();

        //expense data is persisted correctly
        assertEquals(expense.getValue(), savedExpense.getValue());
        //user payment method data is persisted correctly
        assertEquals(userPaymentMethod.getName(), savedExpense.getUserPaymentMethod().getName());
        //user data is persisted correctly
        assertEquals(user.getUsername(), savedExpense.getUser().getUsername());

        //OneToMany mappings, remember to not use equals (since the hash is not the same)
        assertTrue(
                savedExpense.getUser()
                        .getExpenses()
                        .stream()
                        .anyMatch(e -> e.getId().equals(savedExpense.getId()))
        );

        assertTrue(
                savedExpense.getUserPaymentMethod()
                        .getExpensesRelatedWithThisUserPaymentMethod()
                        .stream()
                        .anyMatch(e -> e.getId().equals(savedExpense.getId()))
        );

        assertTrue(
                savedExpense.getUser()
                        .getUserPaymentMethods()
                        .stream()
                        .anyMatch(pm ->
                                pm.getId().equals(savedExpense.getUserPaymentMethod().getId())
                        )
        );

        //User <-> UserPaymentMethods consistency check
        assertEquals(
                savedExpense.getUser().getId(),
                savedExpense.getUserPaymentMethod().getUser().getId()
        );
    }
}
