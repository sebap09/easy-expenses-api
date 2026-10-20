package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.dtos.PageResponse;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.services.ExpenseService;
import com.easyexpenses.api.services.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/all")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(@AuthenticationPrincipal Jwt jwt) {
        UserProfile userProfile = userProfileService.findOrCreateUser(jwt);
        return ResponseEntity.ok(expenseService.getAllExpenses(userProfile));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ExpenseResponse>> getAllExpensesPaginated(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        UserProfile userProfile = userProfileService.findOrCreateUser(jwt);
        return ResponseEntity.ok(expenseService.getAllExpensesPaginated(userProfile, pageable));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addNewExpense(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AddNewExpenseRequest addNewExpenseRequest) {
        UserProfile userProfile = userProfileService.findOrCreateUser(jwt);
        ExpenseResponse expenseResponse = expenseService.addNewExpense(userProfile, addNewExpenseRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(expenseResponse);
    }
}
