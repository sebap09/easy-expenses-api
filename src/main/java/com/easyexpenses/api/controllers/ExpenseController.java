package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // GET http://localhost:8080/api/v1/expenses
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        return new ResponseEntity<>(expenseService.getAllExpenses(), HttpStatus.OK);
    }

    // POST http://localhost:8080/api/v1/expenses
    @PostMapping
    public ResponseEntity<ExpenseResponse> addNewExpense(@RequestBody AddNewExpenseRequest addNewExpenseRequest) {
        ExpenseResponse expenseResponse = expenseService.addNewExpense(addNewExpenseRequest);
        return new ResponseEntity<>(expenseResponse, HttpStatus.CREATED);
    }
}
