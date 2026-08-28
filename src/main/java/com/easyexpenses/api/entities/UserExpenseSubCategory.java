package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "user_expense_sub_category",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"userId", "name"}
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserExpenseSubCategory {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @PrePersist
    @PreUpdate
    private void normalize() {
        name = name.toUpperCase();
    }

    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    private UserProfile userProfile;

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
