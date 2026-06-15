package com.easyexpenses.api.services;

import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserPaymentMethod;
import com.easyexpenses.api.mappers.ExpenseMapper;
import com.easyexpenses.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final UserPaymentMethodService userPaymentMethodService;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, UserService userService, UserPaymentMethodService userPaymentMethodService, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.userPaymentMethodService = userPaymentMethodService;
        this.expenseMapper = expenseMapper;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public ExpenseResponse addNewExpense(AddNewExpenseRequest addNewExpenseRequest) {
        //those should be already existing in DB upon new Expense creation
        userService.mockUser();
        User user = userService.getUser(addNewExpenseRequest.userId());
        userPaymentMethodService.mockUserPaymentMethod(user);
        //userCategory
        //userSubCategory



        UserPaymentMethod userPaymentMethod = userPaymentMethodService.getUserPaymentMethod(addNewExpenseRequest.userPaymentMethodId());
        //userCategory
        //userSubCategory

        Expense expense = new Expense();
        expense.setDate(addNewExpenseRequest.date());
        expense.setValue(addNewExpenseRequest.value());
        expense.setComment(addNewExpenseRequest.comment());

        expense.setCategoryId(addNewExpenseRequest.categoryId());
        expense.setSubcategoryId(addNewExpenseRequest.subCategoryId());


        //relationships mappings
        //User <-> Expense
        user.addNewExpense(expense);
        expense.setUser(user);
        //UserPaymentMethod <-> Expense
        userPaymentMethod.addNewExpense(expense);
        expense.setUserPaymentMethod(userPaymentMethod);

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }
}
