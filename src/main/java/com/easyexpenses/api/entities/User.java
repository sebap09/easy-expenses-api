package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String username;
    private String password;

    @OneToMany(mappedBy="user")
    private Set<Expense> expenses = new HashSet<>();;

    public void addNewExpense(Expense expense){
        this.expenses.add(expense);
    }
}
