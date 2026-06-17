package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.repositories.UserExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserExpenseCategoryService {
    private final UserExpenseCategoryRepository userExpenseCategoryRepository;

    @Autowired
    public UserExpenseCategoryService(UserExpenseCategoryRepository userExpenseCategoryRepository) {
        this.userExpenseCategoryRepository = userExpenseCategoryRepository;
    }

    public UserExpenseCategory getUserExpenseCategory(Long id){
        return userExpenseCategoryRepository.findById(id).orElseThrow();
    }
}
