package com.easyexpenses.api;

import com.easyexpenses.api.entities.Expense;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.repositories.ExpenseRepository;
import com.easyexpenses.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        // given
        Expense expense = new Expense();
        User user = new User();

        expense.setDate(new Date());
        expense.setComment("comment");
        expense.setValue(100.99);
        expense.setCategoryId(1L);
        expense.setSubcategoryId(1L);
//        expense.setPaymentTypeId(1L);

        user.setUsername("user1");
        user.setPassword("user1");
        Set<Expense> expenses = new HashSet<>();
        expenses.add(expense);
        expense.setUser(user);

        userRepository.save(user);

        User savedUser = userRepository.findById(user.getId()).orElse(null);
        System.out.println(savedUser.getPassword());
        assertNotNull(savedUser);
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getPassword(), savedUser.getPassword());
        System.out.println(System.getProperty("user.dir"));
        // when
//        Optional<User> result = userRepository.findByUsername("john");
//
//        // then
//        assertTrue(result.isPresent());
//        assertEquals("john", result.get().getUsername());
    }
}
