package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.repositories.UserExpenseSubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserExpenseSubCategoryService {
    private final UserExpenseSubCategoryRepository userExpenseSubCategoryRepository;

    @Autowired
    public UserExpenseSubCategoryService(UserExpenseSubCategoryRepository userExpenseSubCategoryRepository) {
        this.userExpenseSubCategoryRepository = userExpenseSubCategoryRepository;
    }

    public UserExpenseSubCategory getUserExpenseSubCategory(Long id){
        return userExpenseSubCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Expense Sub Category not found: " + id));
    }

    public List<UserExpenseSubCategory> getAllUserExpenseSubCategories(Long userId) {
        return userExpenseSubCategoryRepository.findAllByUserId(userId);
    }
}
