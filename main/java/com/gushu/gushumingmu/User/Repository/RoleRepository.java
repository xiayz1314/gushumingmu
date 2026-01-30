package com.gushu.gushumingmu.User.Repository;

import com.gushu.gushumingmu.User.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName); // 按角色名查询
}
