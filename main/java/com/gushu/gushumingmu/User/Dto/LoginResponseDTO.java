package com.gushu.gushumingmu.User.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;  // JWT令牌
    private String username;  // 用户名
    private Set<String> roles;  // 角色列表
//    private Set<String> permissions;  // 权限列表
    private String fullName;

}
