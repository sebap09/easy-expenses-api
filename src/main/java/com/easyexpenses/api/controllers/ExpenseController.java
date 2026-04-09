package com.easyexpenses.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/expenses")
public class ExpenseController {

    // GET http://localhost:8080/api/v1/expenses
    @GetMapping
    public ResponseEntity<String> getAllExpenses() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}
