package com.easyexpenses.api.services;

import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.mappers.ExpenseMapper;
import com.easyexpenses.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, UserService userService, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.expenseMapper = expenseMapper;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public ExpenseResponse addNewExpense(AddNewExpenseRequest addNewExpenseRequest) {
        userService.mockUser();

        Expense expense = new Expense();
        User user = userService.getUser(addNewExpenseRequest.userId());

        expense.setCategoryId(addNewExpenseRequest.categoryId());
        expense.setSubcategoryId(addNewExpenseRequest.subCategoryId());
        expense.setPaymentTypeId(addNewExpenseRequest.paymentTypeId());
        expense.setDate(addNewExpenseRequest.date());
        expense.setValue(addNewExpenseRequest.value());
        expense.setComment(addNewExpenseRequest.comment());

        user.addNewExpense(expense);
        expense.setUser(user);

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }
}
