package com.gushu.gushumingmu.User.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "role", schema = "gushumingmu")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "role_desc", length = 100)
    private String roleDesc;
    // 多对多：一个角色关联多个权限
    @ManyToMany(fetch = FetchType.EAGER) // 立即加载权限
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "perm_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public String getCode() {
        return getCode();
    }

    public Object getName() {
        return roleName;
    }
}