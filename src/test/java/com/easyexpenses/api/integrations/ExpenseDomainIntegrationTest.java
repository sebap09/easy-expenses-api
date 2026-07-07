package com.easyexpenses.api.integrations;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.repositories.*;
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
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserPaymentMethodRepository userPaymentMethodRepository;

    @Autowired
    private UserExpenseCategoryRepository userExpenseCategoryRepository;

    @Autowired
    private UserExpenseSubCategoryRepository userExpenseSubCategoryRepository;

    //required for flush() and clear() to be able to test whole database cycle instead of only persistence context
    @Autowired
    private EntityManager em;

    @Test
    void shouldPersistExpenseWithDomain() {
        //entities & relationships data
        UserProfile userProfile = new UserProfileBuilder().build();

        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .userProfile(userProfile)
                .build();

        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(userProfile)
                .build();

        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(userProfile)
                .userExpenseCategory(userExpenseCategory)
                .build();

        Expense expense = new ExpenseBuilder()
                .userProfile(userProfile)
                .userPaymentMethod(userPaymentMethod)
                .userExpenseCategory(userExpenseCategory)
                .userExpenseSubCategory(userExpenseSubCategory)
                .build();

        //saving entities to db
        userProfileRepository.save(userProfile);
        userPaymentMethodRepository.save(userPaymentMethod);
        userExpenseCategoryRepository.save(userExpenseCategory);
        userExpenseSubCategoryRepository.save(userExpenseSubCategory);
        expenseRepository.save(expense);

        //push from persistence context to actual db
        em.flush();
        em.clear();

        Expense savedExpense = expenseRepository.findById(expense.getId()).orElseThrow();

        //expense data is persisted correctly
        assertEquals(expense.getValue(), savedExpense.getValue());
        //userProfile payment method data is persisted correctly
        assertEquals(userPaymentMethod.getName(), savedExpense.getUserPaymentMethod().getName());
        //userProfile expense category data is persisted correctly
        assertEquals(userExpenseCategory.getName(), savedExpense.getUserExpenseCategory().getName());
        //userProfile expense sub category data is persisted correctly
        assertEquals(userExpenseSubCategory.getName(), savedExpense.getUserExpenseSubCategory().getName());
        //userProfile data is persisted correctly
        assertEquals(userProfile.getId(), savedExpense.getUser().getId());

        //8
        //OneToMany mappings, remember to not use equals (since the hash is not the same)
        assertTrue(
                savedExpense.getUser()
                        .getExpenses()
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
                savedExpense.getUser()
                        .getUserExpenseSubCategories()
                        .stream()
                        .anyMatch(pm ->
                                pm.getId().equals(savedExpense.getUserExpenseSubCategory().getId())
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

        assertTrue(
                savedExpense.getUserExpenseCategory()
                        .getSubCategoriesRelatedWithThisCategory()
                        .stream()
                        .anyMatch(pm ->
                                pm.getId().equals(savedExpense.getUserExpenseSubCategory().getId())
                        )
        );

        assertTrue(
                savedExpense.getUserPaymentMethod()
                        .getExpensesRelatedWithThisUserPaymentMethod()
                        .stream()
                        .anyMatch(e -> e.getId().equals(savedExpense.getId()))
        );

        assertTrue(
                savedExpense.getUserExpenseSubCategory()
                        .getExpensesRelatedWithThisSubCategory()
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

        //User <-> UserExpenseSubCategory consistency check
        assertEquals(
                savedExpense.getUser().getId(),
                savedExpense.getUserExpenseSubCategory().getUser().getId()
        );
    }

    @Test
    void shouldPersistMultipleExpensesUsingDifferentUserPaymentMethods() {
        UserProfile userProfile = new UserProfileBuilder().build();

        UserPaymentMethod card = new UserPaymentMethodBuilder()
                .userProfile(userProfile)
                .name("CARD")
                .build();

        UserPaymentMethod cash = new UserPaymentMethodBuilder()
                .userProfile(userProfile)
                .name("CASH")
                .build();

        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .userProfile(userProfile)
                .build();

        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .userProfile(userProfile)
                .userExpenseCategory(userExpenseCategory)
                .build();

        Expense cardExpense = new ExpenseBuilder()
                .userProfile(userProfile)
                .userPaymentMethod(card)
                .userExpenseCategory(userExpenseCategory)
                .userExpenseSubCategory(userExpenseSubCategory)
                .build();

        Expense cashExpense = new ExpenseBuilder()
                .userProfile(userProfile)
                .userPaymentMethod(cash)
                .userExpenseCategory(userExpenseCategory)
                .userExpenseSubCategory(userExpenseSubCategory)
                .build();

        userProfileRepository.save(userProfile);
        userPaymentMethodRepository.save(card);
        userPaymentMethodRepository.save(cash);
        userExpenseCategoryRepository.save(userExpenseCategory);
        userExpenseSubCategoryRepository.save(userExpenseSubCategory);
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
        assertEquals(savedCardExpense.getUserExpenseSubCategory().getName(),
                savedCashExpense.getUserExpenseSubCategory().getName());
    }
}
