package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.UserPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPaymentMethodRepository extends JpaRepository<UserPaymentMethod, Long> {
    Optional<UserPaymentMethod> findByIdAndUserProfileUserId(Long id, Long userId);
}
