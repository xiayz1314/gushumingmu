package com.gushu.gushumingmu.User.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PermissionDTO {
    @NotBlank(message = "权限编码不能为空")
    private String permCode;

    @NotBlank(message = "权限名称不能为空")
    private String permName;
}