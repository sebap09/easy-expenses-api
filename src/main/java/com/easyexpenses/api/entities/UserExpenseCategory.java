package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "user_expense_categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "name"}
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserExpenseCategory {
    @Id
    @SequenceGenerator(
            name = "user_expense_categories_seq",
            sequenceName = "user_expense_categories_seq",
            allocationSize = 50
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_expense_categories_seq"
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

    @OneToMany(mappedBy="userExpenseCategory")
    @Setter(AccessLevel.NONE)
    private Set<Expense> expensesRelatedWithThisCategory = new HashSet<>();

    @OneToMany(mappedBy="userExpenseCategory",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @Setter(AccessLevel.NONE)
    private Set<UserExpenseSubCategory> subCategoriesRelatedWithThisCategory = new HashSet<>();

    public void addNewExpenseRelatedWithThisCategory(Expense expense){
        this.expensesRelatedWithThisCategory.add(expense);
        expense.setUserExpenseCategory(this);
    }

    public void addNewSubCategoryRelatedWithThisCategory(UserExpenseSubCategory userExpenseSubCategory){
        this.subCategoriesRelatedWithThisCategory.add(userExpenseSubCategory);
        userExpenseSubCategory.setUserExpenseCategory(this);
    }
}
