package com.gushu.gushumingmu.User.Service.impl;

import com.gushu.gushumingmu.User.Entity.Permission;
import com.gushu.gushumingmu.User.Entity.Role;
import com.gushu.gushumingmu.User.Exception.BusinessException;
import com.gushu.gushumingmu.User.Repository.PermissionRepository;
import com.gushu.gushumingmu.User.Repository.RoleRepository;
import com.gushu.gushumingmu.User.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public Role createRole(String roleName, String roleDesc) {
        if (roleRepository.findByRoleName(roleName).isPresent()) {
            throw new BusinessException("角色已存在");
        }
        Role role = new Role();
        role.setRoleName(roleName);
        role.setRoleDesc(roleDesc);
        return roleRepository.save(role);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role findRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("角色不存在"));
    }

    @Override
    @Transactional
    public Role updateRole(Long roleId, String roleName, String roleDesc) {
        Role role = findRoleById(roleId);

        if (roleRepository.findByRoleName(roleName)
                .filter(r -> !r.getId().equals(roleId))
                .isPresent()) {
            throw new BusinessException("角色名已存在");
        }

        role.setRoleName(roleName);
        role.setRoleDesc(roleDesc);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new BusinessException("角色不存在");
        }
        roleRepository.deleteById(roleId);
    }

    @Override
    @Transactional
    public void assignPermission(Long roleId, Long permId) {
        Role role = findRoleById(roleId);
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new BusinessException("权限不存在"));

        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void assignPermissionsBatch(Long roleId, Set<Long> permIds) {
        Role role = findRoleById(roleId);
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permIds));

        if (permissions.size() != permIds.size()) {
            throw new BusinessException("部分权限不存在");
        }

        role.getPermissions().addAll(permissions);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void removePermission(Long roleId, Long permId) {
        Role role = findRoleById(roleId);
        Permission permission = permissionRepository.findById(permId)
                .orElseThrow(() -> new BusinessException("权限不存在"));

        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }
}