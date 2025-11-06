package com.example.CST438_P3.repo;

import com.example.CST438_P3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
