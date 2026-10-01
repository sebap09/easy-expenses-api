package com.easyexpenses.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "user_payment_methods",
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
public class UserPaymentMethod {
    @Id
    @SequenceGenerator(
            name = "user_payment_methods_seq",
            sequenceName = "user_payment_methods_seq",
            allocationSize = 50
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_payment_methods_seq"
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

    @OneToMany(mappedBy="userPaymentMethod")
    @Setter(AccessLevel.NONE)
    private Set<Expense> expensesRelatedWithThisUserPaymentMethod = new HashSet<>();

    public void addNewExpenseRelatedWithThisUserPaymentMethod(Expense expense){
        this.expensesRelatedWithThisUserPaymentMethod.add(expense);
        expense.setUserPaymentMethod(this);
    }
}
