package com.easyexpenses.api;

import com.easyexpenses.api.builders.UserExpenseCategoryBuilder;
import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.builders.ExpenseBuilder;
import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.builders.UserPaymentMethodBuilder;
import com.easyexpenses.api.repositories.ExpenseRepository;
import com.easyexpenses.api.repositories.UserExpenseCategoryRepository;
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

    @Autowired
    private UserExpenseCategoryRepository userExpenseCategoryRepository;

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

        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .user(user)
                .build();

        Expense expense = new ExpenseBuilder()
                .user(user)
                .userPaymentMethod(userPaymentMethod)
                .userExpenseCategory(userExpenseCategory)
                .build();

        //saving entities to db
        userRepository.save(user);
        userPaymentMethodRepository.save(userPaymentMethod);
        userExpenseCategoryRepository.save(userExpenseCategory);
        expenseRepository.save(expense);

        //push from persistence context to actual db
        em.flush();
        em.clear();

        Expense savedExpense = expenseRepository.findById(expense.getId()).orElseThrow();

        //expense data is persisted correctly
        assertEquals(expense.getValue(), savedExpense.getValue());
        //user payment method data is persisted correctly
        assertEquals(userPaymentMethod.getName(), savedExpense.getUserPaymentMethod().getName());
        //user expense category data is persisted correctly
        assertEquals(userExpenseCategory.getName(), savedExpense.getUserExpenseCategory().getName());
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

        assertTrue(
                savedExpense.getUser()
                        .getUserExpenseCategories()
                        .stream()
                        .anyMatch(pm ->
                                pm.getId().equals(savedExpense.getUserExpenseCategory().getId())
                        )
        );

        assertTrue(
                savedExpense.getUserExpenseCategory()
                        .getExpensesRelatedWithThisCategory()
                        .stream()
                        .anyMatch(pm ->
                                pm.getId().equals(savedExpense.getId())
                        )
        );

        //User <-> UserPaymentMethod consistency check
        assertEquals(
                savedExpense.getUser().getId(),
                savedExpense.getUserPaymentMethod().getUser().getId()
        );

        //User <-> UserExpenseCategory consistency check
        assertEquals(
                savedExpense.getUser().getId(),
                savedExpense.getUserExpenseCategory().getUser().getId()
        );
    }

    @Test
    void shouldPersistMultipleExpensesUsingDifferentUserPaymentMethods() {
        User user = new UserBuilder().build();

        UserPaymentMethod card = new UserPaymentMethodBuilder()
                .user(user)
                .name("CARD")
                .build();

        UserPaymentMethod cash = new UserPaymentMethodBuilder()
                .user(user)
                .name("CASH")
                .build();

        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .user(user)
                .build();

        Expense cardExpense = new ExpenseBuilder()
                .user(user)
                .userPaymentMethod(card)
                .userExpenseCategory(userExpenseCategory)
                .build();

        Expense cashExpense = new ExpenseBuilder()
                .user(user)
                .userPaymentMethod(cash)
                .userExpenseCategory(userExpenseCategory)
                .build();

        userRepository.save(user);
        userPaymentMethodRepository.save(card);
        userPaymentMethodRepository.save(cash);
        userExpenseCategoryRepository.save(userExpenseCategory);
        expenseRepository.save(cardExpense);
        expenseRepository.save(cashExpense);

        em.flush();
        em.clear();

        Expense savedCardExpense = expenseRepository.findById(cardExpense.getId()).orElseThrow();
        assertEquals("CARD",
                savedCardExpense.getUserPaymentMethod().getName());
        assertEquals(2,
                savedCardExpense.getUser().getUserPaymentMethods().size());

        Expense savedCashExpense = expenseRepository.findById(cashExpense.getId()).orElseThrow();
        assertEquals("CASH",
                savedCashExpense.getUserPaymentMethod().getName());
        assertEquals(2,
                savedCashExpense.getUser().getUserPaymentMethods().size());

        assertEquals(2,
                savedCashExpense.getUser().getExpenses().size());
        assertEquals(savedCardExpense.getUserExpenseCategory().getName(),
                savedCashExpense.getUserExpenseCategory().getName());
    }
}
