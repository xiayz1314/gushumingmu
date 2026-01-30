package com.gushu.gushumingmu.User.Service;

import com.gushu.gushumingmu.User.Entity.Permission;
import java.util.List;

public interface PermissionService {
    Permission createPermission(String permCode, String permName);
    List<Permission> findAllPermissions();
    void deletePermission(Long permId);
}