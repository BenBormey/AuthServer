package com.example.AuthService.service;

import com.example.AuthService.dto.RoleRequest;
import com.example.AuthService.model.Role;
import com.example.AuthService.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Role getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));
    }

    public Role create(RoleRequest request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException(
                    "Role already exists");
        }

        Role role = Role.builder()
                .name(request.getName())
                .build();

        return roleRepository.save(role);
    }

    public Role update(
            Long id,
            RoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        role.setName(request.getName());

        return roleRepository.save(role);
    }

    public void delete(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        roleRepository.delete(role);
    }
}