package com.gushu.gushumingmu.User.Controller;

import com.gushu.gushumingmu.User.Dto.PasswordDTO;
import com.gushu.gushumingmu.User.Dto.UserProfileDTO;
import com.gushu.gushumingmu.User.Dto.UserDTO;
import com.gushu.gushumingmu.User.Dto.UserUpdateDTO;
import com.gushu.gushumingmu.User.Dto.UserStatusDTO;
import com.gushu.gushumingmu.User.Entity.User;
import com.gushu.gushumingmu.User.Util.ResultUtil;
import com.gushu.gushumingmu.User.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // 新增：用户更新接口（处理PUT /users/{id}请求）

    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可操作
    @PutMapping("/users/{id}")
    public ResultUtil<User> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO updateDTO) {
        // 调用服务层更新用户
        User updatedUser = userService.updateUser(id, updateDTO);
        return ResultUtil.success(updatedUser);
    }
    @GetMapping("/user/profile")
    public ResultUtil<UserProfileDTO> getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 简单日志，检查 principal 与 authorities
        System.out.println("当前认证 principal=" + auth.getPrincipal());
        System.out.println("当前 authorities=" + auth.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(",")));
        User user = userService.getCurrentUser();
        UserProfileDTO profileDTO = new UserProfileDTO(
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRegisterTime() != null ? user.getRegisterTime().toEpochMilli() : 0,
                user.getEnabled()
        );
        return ResultUtil.success(profileDTO);
    }

    @PutMapping("/user/profile")
    public ResultUtil updateUserProfile(@RequestBody UserProfileDTO profileDTO) {
        userService.updateProfile(profileDTO);
        return ResultUtil.success("个人信息更新成功");
    }

    @PutMapping("/user/password")
    public ResultUtil<Void> updatePassword(@RequestBody PasswordDTO passwordDTO) {
        userService.updatePassword(passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return ResultUtil.success("密码修改成功");
    }

    // 管理员功能：用户列表查询
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResultUtil<Page<UserDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username) {
        return ResultUtil.success(userService.findUsersByUsername(username, page, size));
    }

    // 管理员功能：重置用户密码
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/password")
    public ResultUtil<Void> resetUserPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return ResultUtil.success("密码重置成功");
    }

    // 管理员功能：修改用户状态（启用/禁用）
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/status")
    public ResultUtil<Void> updateUserStatus(
            @PathVariable Long userId,
           @Valid @RequestBody UserStatusDTO statusDTO) {
        // 从请求体接收参数
        // 校验用户ID有效性
        if (userId == null || userId <= 0) {
            return ResultUtil.error("无效的用户ID");
        }
        userService. updateUserStatus(userId, statusDTO.isEnabled());
        return ResultUtil.success();
    }


    // 管理员功能：分配角色给用户
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{userId}/roles/{roleId}")
    public ResultUtil<Void> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        userService.assignRole(userId, roleId);
        return ResultUtil.success("角色分配成功");
    }

    // 管理员功能：移除用户角色
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResultUtil<Void> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        userService.removeRole(userId, roleId);
        return ResultUtil.success("角色已移除");
    }
}