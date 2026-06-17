package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user_payment_method")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    private User user;

    @OneToMany(mappedBy="userPaymentMethod")
    @Setter(AccessLevel.NONE)
    private Set<Expense> expensesRelatedWithThisUserPaymentMethod = new HashSet<>();

    public void addNewExpenseRelatedWithThisUserPaymentMethod(Expense expense){
        this.expensesRelatedWithThisUserPaymentMethod.add(expense);
        expense.setUserPaymentMethod(this);
    }
}
