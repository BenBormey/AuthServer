package com.example.AuthService.controller;

import com.example.AuthService.dto.RoleRequest;
import com.example.AuthService.model.Role;
import com.example.AuthService.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<Role> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    public Role getById(@PathVariable Long id) {
        return roleService.getById(id);
    }

    @PostMapping
    public Role create(@RequestBody RoleRequest request) {
        return roleService.create(request);
    }

    @PutMapping("/{id}")
    public Role update(
            @PathVariable Long id,
            @RequestBody RoleRequest request) {

        return roleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        roleService.delete(id);

        return "Role deleted successfully";
    }
}