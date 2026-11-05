package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.errors.ErrorCode;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {
    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserPaymentMethodService userPaymentMethodService;

    @Mock
    private UserExpenseCategoryService userExpenseCategoryService;

    @Mock
    private UserExpenseSubCategoryService userExpenseSubCategoryService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseService expenseService;


    @Test
    void shouldThrowExceptionWhenUserPaymentMethodDoesNotBelongToUser() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        Long userId = expenseFixture.getUserProfile()
                .getUserId();
        UserProfile anotherUser = new UserProfile();
        anotherUser.setUserId(2L);

        UserPaymentMethod anotherUserPaymentMethod = new UserPaymentMethodBuilder()
                .name("PM 2")
                .build();
        anotherUserPaymentMethod.setId(2L);
        anotherUser.addNewUserPaymentMethod(anotherUserPaymentMethod);

        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                anotherUserPaymentMethod.getId(),
                new Date(),
                100d,
                "Test"
        );

        //mocks behavior definition
        when(userProfileService.getUserProfileById(userId))
                .thenReturn(expenseFixture.getUserProfile());

        //wrong userProfile payment method
        when(userPaymentMethodService.getByIdAndUserId(anotherUserPaymentMethod.getId(), userId))
                .thenThrow(new ResourceNotFoundException("Error", ErrorCode.USER_PAYMENT_METHOD_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.addNewExpense(userId, addNewExpenseRequest);
        });

        assertEquals(
                ErrorCode.USER_PAYMENT_METHOD_NOT_FOUND,
                exception.getErrorCode()
                    );

        verify(expenseRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserExpenseCategoryDoesNotBelongToUser() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        Long userId = expenseFixture.getUserProfile()
                .getUserId();

        UserProfile anotherUser = new UserProfile();
        anotherUser.setUserId(2L);

        UserExpenseCategory anotherUserExpenseCategory = new UserExpenseCategoryBuilder()
                .name("UEC 2")
                .build();
        anotherUserExpenseCategory.setId(2L);

        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                anotherUserExpenseCategory.getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        //mocks behavior definition
        when(userProfileService.getUserProfileById(userId))
                .thenReturn(expenseFixture.getUserProfile());

        when(userPaymentMethodService.getByIdAndUserId(addNewExpenseRequest.userPaymentMethodId(), userId))
                .thenReturn(expenseFixture.getUserPaymentMethod());

        //wrong category
        when(userExpenseCategoryService.getByIdAndUserId(addNewExpenseRequest.categoryId(), userId))
                .thenThrow(new ResourceNotFoundException("Error", ErrorCode.USER_EXPENSE_CATEGORY_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.addNewExpense(userId, addNewExpenseRequest);
        });

        assertEquals(
                ErrorCode.USER_EXPENSE_CATEGORY_NOT_FOUND,
                exception.getErrorCode()
                    );

        verify(expenseRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserExpenseSubCategoryDoesNotBelongToUser() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        Long userId = expenseFixture.getUserProfile()
                .getUserId();

        UserProfile anotherUser = new UserProfile();
        anotherUser.setUserId(2L);

        UserExpenseSubCategory anotherUserExpenseSubCategory = new UserExpenseSubCategoryBuilder()
                .name("UESC 2")
                .build();
        anotherUserExpenseSubCategory.setId(2L);

        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserExpenseCategory().getId(),
                anotherUserExpenseSubCategory.getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        //mocks behavior definition
        when(userProfileService.getUserProfileById(userId))
                .thenReturn(expenseFixture.getUserProfile());

        when(userPaymentMethodService.getByIdAndUserId(addNewExpenseRequest.userPaymentMethodId(), userId))
                .thenReturn(expenseFixture.getUserPaymentMethod());

        when(userExpenseCategoryService.getByIdAndUserId(addNewExpenseRequest.categoryId(), userId))
                .thenReturn(expenseFixture.getUserExpenseCategory());

        //wrong subcategory
        when(userExpenseSubCategoryService.getByIdAndCategoryId(addNewExpenseRequest.subCategoryId(), addNewExpenseRequest.categoryId()))
                .thenThrow(new ResourceNotFoundException("Error", ErrorCode.USER_EXPENSE_SUB_CATEGORY_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.addNewExpense(userId, addNewExpenseRequest);
        });

        assertEquals(
                ErrorCode.USER_EXPENSE_SUB_CATEGORY_NOT_FOUND,
                exception.getErrorCode()
                    );

        verify(expenseRepository, never())
                .save(any());
    }

    @Test
    void shouldAddNewExpenseWhenDomainObjectsAreConsistent() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        Long userId = expenseFixture.getUserProfile()
                .getUserId();

        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        //mocks behavior definition
        when(userProfileService.getUserProfileById(userId))
                .thenReturn(expenseFixture.getUserProfile());

        when(userPaymentMethodService.getByIdAndUserId(addNewExpenseRequest.userPaymentMethodId(), userId))
                .thenReturn(expenseFixture.getUserPaymentMethod());

        when(userExpenseCategoryService.getByIdAndUserId(addNewExpenseRequest.categoryId(), userId))
                .thenReturn(expenseFixture.getUserExpenseCategory());

        //wrong subcategory
        when(userExpenseSubCategoryService.getByIdAndCategoryId(addNewExpenseRequest.subCategoryId(), addNewExpenseRequest.categoryId()))
                .thenReturn(expenseFixture.getUserExpenseSubCategory());

        expenseService.addNewExpense(userId, addNewExpenseRequest);

        verify(expenseRepository).save(any(Expense.class));
    }
}
