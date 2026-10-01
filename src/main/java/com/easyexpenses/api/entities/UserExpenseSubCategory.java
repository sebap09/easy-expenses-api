package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "user_expense_sub_categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "name", "user_expense_category_id"}
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserExpenseSubCategory {
    @Id
    @SequenceGenerator(
            name = "user_expense_sub_categories_seq",
            sequenceName = "user_expense_sub_categories_seq",
            allocationSize = 50
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_expense_sub_categories_seq"
    )
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @PrePersist
    @PreUpdate
    private void normalize() {
        name = name.toUpperCase();
    }

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name="user_expense_category_id", nullable=false)
    private UserExpenseCategory userExpenseCategory;

    @OneToMany(mappedBy="userExpenseSubCategory")
    @Setter(AccessLevel.NONE)
    private Set<Expense> expensesRelatedWithThisSubCategory = new HashSet<>();

    public void addNewExpenseRelatedWithThisSubCategory(Expense expense){
        this.expensesRelatedWithThisSubCategory.add(expense);
        expense.setUserExpenseSubCategory(this);
    }
}
