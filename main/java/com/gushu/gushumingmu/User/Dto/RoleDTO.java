package com.gushu.gushumingmu.User.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class RoleDTO {
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    private String roleDesc;
    private Set<String> permCodes; // 权限标识集合
}