package com.easyexpenses.api.integrations;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.repositories.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
public class ExpenseDomainIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:18");

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
        User user = new UserBuilder()
                .identityIssuer("issuer")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();

        UserProfile userProfile = new UserProfile();
        userProfile.setCompleted(true);
        user.setUserProfile(userProfile);

        String cash = "Cash";
        UserPaymentMethod userPaymentMethod = new UserPaymentMethodBuilder()
                .name(cash)
                .build();
        userProfile.addNewUserPaymentMethod(userPaymentMethod);

        String name = "Grocery";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .name(name)
                .build();
        userProfile.addNewUserCategory(userExpenseCategory);

        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .name("UESC")
                .build();
        userExpenseCategory.addNewSubCategoryRelatedWithThisCategory(userExpenseSubCategory);

        userRepository.save(userProfile.getUser());

        Expense expense = new ExpenseBuilder()
                .comment("comment")
                .date(new Date())
                .value(100d)
                .build();

        expense.setUserPaymentMethod(userPaymentMethod);
        expense.setUserExpenseCategory(userExpenseCategory);
        expense.setUserExpenseSubCategory(userExpenseSubCategory);
        userProfile.addNewExpense(expense);

        //saving entities to db
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
        assertEquals(userProfile.getUserId(), savedExpense.getUserProfile().getUserId());

        //8
        //OneToMany mappings, remember to not use equals (since the hash is not the same)
        assertTrue(
                savedExpense.getUserProfile()
                        .getExpenses()
                        .stream()
                        .anyMatch(e -> e.getId().equals(savedExpense.getId()))
        );

        assertTrue(
                savedExpense.getUserProfile()
                        .getUserPaymentMethods()
                        .stream()
                        .anyMatch(pm ->
                                pm.getId().equals(savedExpense.getUserPaymentMethod().getId())
                        )
        );

        assertTrue(
                savedExpense.getUserProfile()
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
                savedExpense.getUserProfile().getUserId(),
                savedExpense.getUserPaymentMethod().getUserProfile().getUserId()
        );

        //User <-> UserExpenseCategory consistency check
        assertEquals(
                savedExpense.getUserProfile().getUserId(),
                savedExpense.getUserExpenseCategory().getUserProfile().getUserId()
        );
    }

    @Test
    void shouldPersistMultipleExpensesUsingDifferentUserPaymentMethods() {
        User user = new UserBuilder()
                .identityIssuer("issuer")
                .identitySubject(ExpenseFixtureBuilder.getRandomSubject())
                .build();

        UserProfile userProfile = new UserProfile();
        userProfile.setCompleted(true);
        user.setUserProfile(userProfile);


        UserPaymentMethod card = new UserPaymentMethodBuilder()
                .name("card")
                .build();
        userProfile.addNewUserPaymentMethod(card);

        UserPaymentMethod cash = new UserPaymentMethodBuilder()
                .name("cash")
                .build();
        userProfile.addNewUserPaymentMethod(cash);

        String name = "Grocery";
        UserExpenseCategory userExpenseCategory = new UserExpenseCategoryBuilder()
                .name(name)
                .build();
        userProfile.addNewUserCategory(userExpenseCategory);

        UserExpenseSubCategory userExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .name("UESC")
                .build();
        userExpenseCategory.addNewSubCategoryRelatedWithThisCategory(userExpenseSubCategory);

        userRepository.save(userProfile.getUser());

        Expense cardExpense = new ExpenseBuilder()
                .comment("comment")
                .date(new Date())
                .value(100d)
                .build();

        cardExpense.setUserPaymentMethod(card);
        cardExpense.setUserExpenseCategory(userExpenseCategory);
        cardExpense.setUserExpenseSubCategory(userExpenseSubCategory);
        userProfile.addNewExpense(cardExpense);

        expenseRepository.save(cardExpense);

        Expense cashExpense = new ExpenseBuilder()
                .comment("comment")
                .date(new Date())
                .value(100d)
                .build();

        cashExpense.setUserPaymentMethod(cash);
        cashExpense.setUserExpenseCategory(userExpenseCategory);
        cashExpense.setUserExpenseSubCategory(userExpenseSubCategory);
        userProfile.addNewExpense(cashExpense);

        expenseRepository.save(cashExpense);

        em.flush();
        em.clear();

        Expense savedCardExpense = expenseRepository.findById(cardExpense.getId()).orElseThrow();
        assertEquals("CARD",
                savedCardExpense.getUserPaymentMethod().getName());
        assertEquals(2,
                savedCardExpense.getUserProfile().getUserPaymentMethods().size());

        Expense savedCashExpense = expenseRepository.findById(cashExpense.getId()).orElseThrow();
        assertEquals("CASH",
                savedCashExpense.getUserPaymentMethod().getName());
        assertEquals(2,
                savedCashExpense.getUserProfile().getUserPaymentMethods().size());

        assertEquals(2,
                savedCashExpense.getUserProfile().getExpenses().size());
        assertEquals(savedCardExpense.getUserExpenseCategory().getName(),
                savedCashExpense.getUserExpenseCategory().getName());
        assertEquals(savedCardExpense.getUserExpenseSubCategory().getName(),
                savedCashExpense.getUserExpenseSubCategory().getName());
    }
}
