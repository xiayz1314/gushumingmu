package com.gushu.gushumingmu.User.Dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度不能少于8位")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email; // 变为非必填

    @NotBlank(message = "姓名不能为空")
    private String fullName; //

    @Size(min = 11, max = 11, message = "手机号格式不正确")
    private String phone; // 新增
}