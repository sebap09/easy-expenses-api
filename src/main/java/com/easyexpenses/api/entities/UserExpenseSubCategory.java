package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user_expense_sub_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserExpenseSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="userExpenseCategoryId", nullable=false)
    private UserExpenseCategory userExpenseCategory;

    @OneToMany(mappedBy="userExpenseSubCategory")
    @Setter(AccessLevel.NONE)
    private Set<Expense> expensesRelatedWithThisSubCategory = new HashSet<>();

    public void addNewExpenseRelatedWithThisSubCategory(Expense expense){
        this.expensesRelatedWithThisSubCategory.add(expense);
        expense.setUserExpenseSubCategory(this);
    }
}
