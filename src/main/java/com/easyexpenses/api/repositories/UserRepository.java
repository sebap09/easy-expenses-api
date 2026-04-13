package com.easyexpenses.api.repositories;

import com.easyexpenses.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
