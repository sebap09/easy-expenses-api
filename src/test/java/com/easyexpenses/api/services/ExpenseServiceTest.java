package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.*;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.errors.ErrorCode;
import com.easyexpenses.api.errors.ValidationException;
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
    void shouldThrowExceptionWhenUserIdIsNotConsistentAcrossDomain() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();

        UserProfile anotherUser = new UserProfileBuilder()
                .id(2L)
                .build();
        UserPaymentMethod anotherUserPaymentMethod = new UserPaymentMethodBuilder()
                .id(2L)
                .userProfile(anotherUser)
                .build();


        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUser().getId(),
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        //mocks behavior definition
        when(userProfileService.getUserProfile(addNewExpenseRequest.userId()))
                .thenReturn(expenseFixture.getUser());

        //wrong userProfile payment method
        when(userPaymentMethodService.getUserPaymentMethod(addNewExpenseRequest.userPaymentMethodId()))
                .thenReturn(anotherUserPaymentMethod);

        when(userExpenseCategoryService.getUserExpenseCategory(addNewExpenseRequest.categoryId()))
                .thenReturn(expenseFixture.getUserExpenseCategory());

        when(userExpenseSubCategoryService.getUserExpenseSubCategory(addNewExpenseRequest.subCategoryId()))
                .thenReturn(expenseFixture.getUserExpenseSubCategory());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            expenseService.addNewExpense(addNewExpenseRequest);
        });

        assertEquals(
                ErrorCode.INVALID_USER_RELATIONSHIP,
                exception.getErrorCode()
        );

        verify(expenseRepository, never())
                .save(any());
    }

    @Test
    void shouldAddNewExpenseWhenDomainObjectsAreConsistent() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();

        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUser().getId(),
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        //mocks behavior definition
        when(userProfileService.getUserProfile(addNewExpenseRequest.userId()))
                .thenReturn(expenseFixture.getUser());

        when(userPaymentMethodService.getUserPaymentMethod(addNewExpenseRequest.userPaymentMethodId()))
                .thenReturn(expenseFixture.getUserPaymentMethod());

        when(userExpenseCategoryService.getUserExpenseCategory(addNewExpenseRequest.categoryId()))
                .thenReturn(expenseFixture.getUserExpenseCategory());

        when(userExpenseSubCategoryService.getUserExpenseSubCategory(addNewExpenseRequest.subCategoryId()))
                .thenReturn(expenseFixture.getUserExpenseSubCategory());

        expenseService.addNewExpense(addNewExpenseRequest);

        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void shouldThrowExceptionWhenSubCategoryIsNotAssignedToProperCategory() {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        UserExpenseCategory anotherCategory = new UserExpenseCategoryBuilder()
                .id(2L)
                .userProfile(expenseFixture.getUser())
                .build();


        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUser().getId(),
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "Test"
        );

        //mocks behavior definition
        when(userProfileService.getUserProfile(addNewExpenseRequest.userId()))
                .thenReturn(expenseFixture.getUser());

        when(userPaymentMethodService.getUserPaymentMethod(addNewExpenseRequest.userPaymentMethodId()))
                .thenReturn(expenseFixture.getUserPaymentMethod());

        //wrong userProfile category
        when(userExpenseCategoryService.getUserExpenseCategory(addNewExpenseRequest.categoryId()))
                .thenReturn(anotherCategory);

        when(userExpenseSubCategoryService.getUserExpenseSubCategory(addNewExpenseRequest.subCategoryId()))
                .thenReturn(expenseFixture.getUserExpenseSubCategory());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            expenseService.addNewExpense(addNewExpenseRequest);
        });

        assertEquals(
                ErrorCode.SUBCATEGORY_CATEGORY_MISMATCH,
                exception.getErrorCode()
        );

        verify(expenseRepository, never())
                .save(any());
    }
}
