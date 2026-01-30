package com.gushu.gushumingmu.User.Service;

import com.gushu.gushumingmu.User.Entity.Role;
import java.util.List;
import java.util.Set;

public interface RoleService {
    Role createRole(String roleName, String roleDesc);
    List<Role> findAllRoles();
    Role findRoleById(Long roleId);
    Role updateRole(Long roleId, String roleName, String roleDesc);
    void deleteRole(Long roleId);
    void assignPermission(Long roleId, Long permId);
    void assignPermissionsBatch(Long roleId, Set<Long> permIds);
    void removePermission(Long roleId, Long permId);
}