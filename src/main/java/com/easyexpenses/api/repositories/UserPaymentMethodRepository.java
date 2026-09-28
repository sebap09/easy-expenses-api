package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPaymentMethodRepository extends JpaRepository<UserPaymentMethod, Long> {
    @Query("""
        SELECT p
        FROM UserPaymentMethod p
        WHERE p.userProfile.userId = :userId
        ORDER BY p.id
    """)
    List<UserPaymentMethod> findAllByUserId(Long userId);
}
