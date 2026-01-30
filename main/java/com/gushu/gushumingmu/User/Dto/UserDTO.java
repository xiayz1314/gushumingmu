package com.gushu.gushumingmu.User.Dto;

import lombok.Data;
import java.time.Instant;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Instant createTime;
    private Set<String> roles; // 角色名称集合
    private String fullName;
    private String phone;
    private String password;
    private String confirmPassword;
    private Boolean enabled;
}