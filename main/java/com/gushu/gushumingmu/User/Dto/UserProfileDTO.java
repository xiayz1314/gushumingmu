package com.gushu.gushumingmu.User.Dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserProfileDTO {
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String fullName; // 新增
    private String phone; // 新增
    private Long registerTime;
    private Boolean enabled;

    public UserProfileDTO(String username, String email, String fullName, String phone, Long registerTime , Boolean enabled) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.registerTime = registerTime;
        this.enabled = enabled;
    }
}