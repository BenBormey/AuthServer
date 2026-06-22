package com.example.AuthService.repository;

import com.example.AuthService.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser,Long> {


    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<AppUser> findByUsernameContainingIgnoreCase(String username);

    List<AppUser> findByEmailContainingIgnoreCase(String email);

    // Active User
    List<AppUser> findByEnabledTrue();

    List<AppUser> findByEnabledFalse();

    // Find by Role
    List<AppUser> findByRoleRoleName(String roleName);



}
