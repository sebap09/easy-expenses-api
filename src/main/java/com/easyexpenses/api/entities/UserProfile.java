package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user_profile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    @Id
    @Column(unique = true, nullable = false)
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy="userProfile")
    @Setter(AccessLevel.NONE)
    private Set<Expense> expenses = new HashSet<>();

    @OneToMany(mappedBy="userProfile")
    @Setter(AccessLevel.NONE)
    private Set<UserPaymentMethod> userPaymentMethods = new HashSet<>();

    @OneToMany(mappedBy="userProfile")
    @Setter(AccessLevel.NONE)
    private Set<UserExpenseCategory> userExpenseCategories = new HashSet<>();

    @OneToMany(mappedBy="userProfile")
    @Setter(AccessLevel.NONE)
    private Set<UserExpenseSubCategory> userExpenseSubCategories = new HashSet<>();

    public void addNewExpense(Expense expense){
        this.expenses.add(expense);
        expense.setUserProfile(this);
    }

    public void addNewUserPaymentMethod(UserPaymentMethod userPaymentMethod){
        this.userPaymentMethods.add(userPaymentMethod);
        userPaymentMethod.setUserProfile(this);
    }

    public void addNewUserCategory(UserExpenseCategory userExpenseCategory){
        this.userExpenseCategories.add(userExpenseCategory);
        userExpenseCategory.setUserProfile(this);
    }

    public void addNewUserSubCategory(UserExpenseSubCategory userExpenseSubCategory){
        this.userExpenseSubCategories.add(userExpenseSubCategory);
        userExpenseSubCategory.setUserProfile(this);
    }
}
