package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.repositories.UserExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserExpenseCategoryService {
    private final UserExpenseCategoryRepository userExpenseCategoryRepository;

    @Autowired
    public UserExpenseCategoryService(UserExpenseCategoryRepository userExpenseCategoryRepository) {
        this.userExpenseCategoryRepository = userExpenseCategoryRepository;
    }

    public Set<UserExpenseCategory> getAllWithUserSubExpenseCategoriesByUserId(Long userId) {
        return userExpenseCategoryRepository.joinFetchCategoriesAndSubcategoriesAndFilterByUserId(userId);
    }

    public List<UserExpenseCategory> saveAll(List<UserExpenseCategory> categories) {
        return userExpenseCategoryRepository.saveAll(categories);
    }
}
