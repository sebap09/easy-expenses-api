package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="expense_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserExpenseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    private User user;

    @OneToMany(mappedBy="userExpenseCategory")
    @Setter(AccessLevel.NONE)
    private Set<Expense> expensesRelatedWithThisCategory = new HashSet<>();

    public void addNewExpenseRelatedWithThisCategory(Expense expense){
        this.expensesRelatedWithThisCategory.add(expense);
        expense.setUserExpenseCategory(this);
    }
}
