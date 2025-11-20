package com.example.CST438_P3.repo;

import com.example.CST438_P3.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByActivityType(String activityType);

    List<Group> findByZipCode(String zipCode);

    List<Group> findByCreatorId(Long creatorId);
}
