package com.gushu.gushumingmu.User.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "perm_code", unique = true, nullable = false)
    private String permCode; // 权限标识（如user:delete）

    @Column(name = "perm_name")
    private String permName; // 权限名称（如"删除用户"）
}