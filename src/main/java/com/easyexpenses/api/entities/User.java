package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(
            mappedBy="user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<Expense> expenses = new HashSet<>();

    @OneToMany(
            mappedBy="user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<UserPaymentMethod> userPaymentMethods = new HashSet<>();

    @OneToMany(
            mappedBy="user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<UserExpenseCategory> userExpenseCategories = new HashSet<>();

    @OneToMany(
            mappedBy="user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private Set<UserExpenseSubCategory> userExpenseSubCategories = new HashSet<>();

    public void addNewExpense(Expense expense){
        this.expenses.add(expense);
        expense.setUser(this);
    }

    public void addNewUserPaymentMethod(UserPaymentMethod userPaymentMethod){
        this.userPaymentMethods.add(userPaymentMethod);
        userPaymentMethod.setUser(this);
    }

    public void addNewUserCategory(UserExpenseCategory userExpenseCategory){
        this.userExpenseCategories.add(userExpenseCategory);
        userExpenseCategory.setUser(this);
    }

    public void addNewUserSubCategory(UserExpenseSubCategory userExpenseSubCategory){
        this.userExpenseSubCategories.add(userExpenseSubCategory);
        userExpenseSubCategory.setUser(this);
    }
}
