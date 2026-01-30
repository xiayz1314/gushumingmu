package com.gushu.gushumingmu.User.Controller;

import com.gushu.gushumingmu.User.Dto.PermissionDTO;
import com.gushu.gushumingmu.User.Entity.Permission;
import com.gushu.gushumingmu.User.Service.PermissionService;
import com.gushu.gushumingmu.User.Util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResultUtil<Permission> createPermission(@RequestBody PermissionDTO dto) {
        Permission permission = permissionService.createPermission(dto.getPermCode(), dto.getPermName());
        return ResultUtil.success(permission);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResultUtil<List<Permission>> getAllPermissions() {
        return ResultUtil.success(permissionService.findAllPermissions());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{permId}")
    public ResultUtil<Void> deletePermission(@PathVariable Long permId) {
        permissionService.deletePermission(permId);
        return ResultUtil.success("权限已删除");
    }
}