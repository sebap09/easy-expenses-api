package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.dtos.PageResponse;
import com.easyexpenses.api.services.ExpenseService;
import com.easyexpenses.api.services.UserService;
import jakarta.validation.Valid;
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
    private final UserService userService;
    private final ExpenseService expenseService;

    public ExpenseController(UserService userService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses(@AuthenticationPrincipal Jwt jwt) {
        Long userId = userService.findOrCreateUser(jwt);
        return ResponseEntity.ok(expenseService.getAllExpenses(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ExpenseResponse>> getAllExpensesPaginated(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        Long userId = userService.findOrCreateUser(jwt);
        return ResponseEntity.ok(expenseService.getAllExpensesPaginated(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addNewExpense(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AddNewExpenseRequest addNewExpenseRequest) {
        Long userId = userService.findOrCreateUser(jwt);
        ExpenseResponse expenseResponse = expenseService.addNewExpense(userId, addNewExpenseRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(expenseResponse);
    }
}
