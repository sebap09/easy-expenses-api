package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name="expense")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    private UserProfile user;

    @ManyToOne
    @JoinColumn(name="userPaymentMethodId", nullable=false)
    private UserPaymentMethod userPaymentMethod;

    @ManyToOne
    @JoinColumn(name="userExpenseCategoryId", nullable=false)
    private UserExpenseCategory userExpenseCategory;

    @ManyToOne
    @JoinColumn(name="userExpenseSubCategoryId", nullable=false)
    private UserExpenseSubCategory userExpenseSubCategory;

    private Date date;
    private Double value;
    private String comment;
}
