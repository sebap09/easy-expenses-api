package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Set<Expense> expenses = new HashSet<>();

    public void addNewExpense(Expense expense){
        this.expenses.add(expense);
    }
}
