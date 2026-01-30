package com.gushu.gushumingmu.User.Service.impl;


import com.gushu.gushumingmu.User.Entity.Permission;
import com.gushu.gushumingmu.User.Exception.BusinessException;
import com.gushu.gushumingmu.User.Repository.PermissionRepository;
import com.gushu.gushumingmu.User.Service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    // 创建权限
    @Override
    @Transactional
    public Permission createPermission(String permCode, String permName) {
        if (permissionRepository.findByPermCode(permCode).isPresent()) {
            throw new BusinessException("权限已存在");
        }
        Permission permission = new Permission();
        permission.setPermCode(permCode);
        permission.setPermName(permName);
        return permissionRepository.save(permission);
    }

    @Override
    public List<Permission> findAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public void deletePermission(Long permId) {
    if(!permissionRepository.existsById(permId)) {
        throw new BusinessException("权限不存在");
    }
    permissionRepository.deleteById(permId);
    }
}