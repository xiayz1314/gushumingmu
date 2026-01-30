package com.gushu.gushumingmu.User.Repository;

import com.gushu.gushumingmu.User.Entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermCode(String permCode); // 按权限标识查询
}