package com.gushu.gushumingmu.User.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user", schema = "gushumingmu")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 50 , unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", length = 100)
    private String email; // 变为非必填

    @Column(name = "full_name", length = 100)
    private String fullName; // 新增：用户全名

    @Column(name = "phone", length = 20)
    private String phone; // 新增：电话号码

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "enabled")
    private Boolean enabled = true; // 新增：用户状态（启用/禁用）

    // 多对多：一个用户关联多个角色
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public Instant getRegisterTime() {
        return createTime;
    }

    public boolean isEnabled() {
        return enabled;
    }
}