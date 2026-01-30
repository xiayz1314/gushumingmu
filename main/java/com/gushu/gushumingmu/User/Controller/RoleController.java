package com.gushu.gushumingmu.User.Controller;

import com.gushu.gushumingmu.User.Dto.RoleDTO;
import com.gushu.gushumingmu.User.Entity.Role;
import com.gushu.gushumingmu.User.Service.RoleService;
import com.gushu.gushumingmu.User.Util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResultUtil<Role> createRole(@Validated @RequestBody RoleDTO dto) {
        Role role = roleService.createRole(dto.getRoleName(), dto.getRoleDesc());
        return ResultUtil.success(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResultUtil<List<Role>> getAllRoles() {
        return ResultUtil.success(roleService.findAllRoles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{roleId}")
    public ResultUtil<Role> getRoleById(@PathVariable Long roleId) {
        return ResultUtil.success(roleService.findRoleById(roleId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{roleId}")
    public ResultUtil<Role> updateRole(
            @PathVariable Long roleId,
            @Validated @RequestBody RoleDTO dto) {
        Role updatedRole = roleService.updateRole(roleId, dto.getRoleName(), dto.getRoleDesc());
        return ResultUtil.success(updatedRole);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{roleId}")
    public ResultUtil<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResultUtil.success("角色已删除");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{roleId}/permissions/{permId}")
    public ResultUtil<Void> assignPermission(
            @PathVariable Long roleId,
            @PathVariable Long permId
    ) {
        roleService.assignPermission(roleId, permId);
        return ResultUtil.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{roleId}/permissions/batch")
    public ResultUtil<Void> assignPermissionsBatch(
            @PathVariable Long roleId,
            @RequestBody Set<Long> permIds
    ) {
        roleService.assignPermissionsBatch(roleId, permIds);
        return ResultUtil.success();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{roleId}/permissions/{permId}")
    public ResultUtil<Void> removePermission(
            @PathVariable Long roleId,
            @PathVariable Long permId
    ) {
        roleService.removePermission(roleId, permId);
        return ResultUtil.success("权限已移除");
    }
}