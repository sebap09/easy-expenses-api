package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.services.ExpenseService;
import com.easyexpenses.api.services.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final UserProfileService userProfileService;

    @Autowired
    public ExpenseController(ExpenseService expenseService, UserProfileService userProfileService) {
        this.expenseService = expenseService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(@AuthenticationPrincipal Jwt jwt) {
        UserProfile userProfile = userProfileService.findOrCreateUser(jwt);
        List<ExpenseResponse> expenseResponse = expenseService.getAllExpenses(userProfile);
        return new ResponseEntity<>(expenseResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addNewExpense(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AddNewExpenseRequest addNewExpenseRequest) {
        UserProfile userProfile = userProfileService.findOrCreateUser(jwt);
        ExpenseResponse expenseResponse = expenseService.addNewExpense(userProfile, addNewExpenseRequest);
        return new ResponseEntity<>(expenseResponse, HttpStatus.CREATED);
    }
}
