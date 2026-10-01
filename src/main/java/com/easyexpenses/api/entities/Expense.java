package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name="expenses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @Id
    @SequenceGenerator(
            name = "expenses_seq",
            sequenceName = "expenses_seq",
            allocationSize = 50
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "expenses_seq"
    )
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name="user_payment_method_id", nullable=false)
    private UserPaymentMethod userPaymentMethod;

    @ManyToOne
    @JoinColumn(name="user_expense_category_id", nullable=false)
    private UserExpenseCategory userExpenseCategory;

    @ManyToOne
    @JoinColumn(name="user_expense_sub_category_id", nullable=false)
    private UserExpenseSubCategory userExpenseSubCategory;

    private Date date;
    private Double value;
    private String comment;
}
