package com.sourabh.diningreview.repository;

import com.sourabh.diningreview.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByDisplayName(String displayName);
}
