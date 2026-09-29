package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.fixtures.ExpenseFixture;
import com.easyexpenses.api.mappers.ExpenseMapper;
import com.easyexpenses.api.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseService expenseService;


    @Test
    void shouldThrowExceptionWhenUserPaymentMethodDoesNotBelongToUser() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();

        UserProfile anotherUser = new UserProfileBuilder()
                .user(2L)
                .build();
        UserPaymentMethod anotherUserPaymentMethod = new UserPaymentMethodBuilder()
                .id(2L)
                .userProfile(anotherUser)
                .build();


        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                anotherUserPaymentMethod.getId(),
                new Date(),
                100d,
                "Test"
        );

        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.addNewExpense(expenseFixture.getUserProfile(), addNewExpenseRequest);
        });

        verify(expenseRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserExpenseCategoryDoesNotBelongToUser() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();

        UserProfile anotherUser = new UserProfileBuilder()
                .user(2L)
                .build();
        UserExpenseCategory anotherUserExpenseCategory = new UserExpenseCategoryBuilder()
                .id(2L)
                .userProfile(anotherUser)
                .build();


        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                anotherUserExpenseCategory.getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.addNewExpense(expenseFixture.getUserProfile(), addNewExpenseRequest);
        });

        verify(expenseRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserExpenseSubCategoryDoesNotBelongToUser() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();

        UserProfile anotherUser = new UserProfileBuilder()
                .user(2L)
                .build();
        UserExpenseSubCategory anotherUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .id(2L)
                .userProfile(anotherUser)
                .build();


        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserExpenseCategory().getId(),
                anotherUserExpenseSubCategory.getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.addNewExpense(expenseFixture.getUserProfile(), addNewExpenseRequest);
        });

        verify(expenseRepository, never())
                .save(any());
    }

    @Test
    void shouldAddNewExpenseWhenDomainObjectsAreConsistent() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();

        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        expenseService.addNewExpense(expenseFixture.getUserProfile(), addNewExpenseRequest);

        verify(expenseRepository).save(any(Expense.class));
    }
}
