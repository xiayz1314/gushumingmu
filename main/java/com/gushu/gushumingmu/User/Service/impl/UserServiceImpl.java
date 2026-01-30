package com.gushu.gushumingmu.User.Service.impl;

import com.gushu.gushumingmu.User.Dto.RegisterDTO;
import com.gushu.gushumingmu.User.Dto.UserDTO;
import com.gushu.gushumingmu.User.Dto.UserProfileDTO;
import com.gushu.gushumingmu.User.Dto.UserUpdateDTO;
import com.gushu.gushumingmu.User.Entity.Role;
import com.gushu.gushumingmu.User.Entity.User;
import com.gushu.gushumingmu.User.Exception.BusinessException;
import com.gushu.gushumingmu.User.Repository.RoleRepository;
import com.gushu.gushumingmu.User.Repository.UserRepository;
import com.gushu.gushumingmu.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName()); // 新增
        user.setPhone(dto.getPhone()); // 新增
        user.setCreateTime(Instant.now());
        user.setEnabled(true); // 默认启用

        Role defaultRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new BusinessException("默认角色不存在，请先初始化角色数据"));
        user.getRoles().add(defaultRole);

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户名不存在：" + username));
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("未获取到当前登录用户");
        }
        return findByUsername(authentication.getName());
    }

    @Override
    @Transactional
    public void updateProfile(UserProfileDTO profileDTO) {
        User currentUser = getCurrentUser();
        currentUser.setEmail(profileDTO.getEmail());
        currentUser.setFullName(profileDTO.getFullName()); // 新增
        currentUser.setPhone(profileDTO.getPhone()); // 新增
        userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        User currentUser = getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findUsersByUsername(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<User> userPage;

        if (username != null && !username.isEmpty()) {
            userPage = userRepository.findByUsernameContaining(username, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setFullName(user.getFullName());
            dto.setCreateTime(user.getCreateTime());
            dto.setRoles(user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toSet()));
            dto.setEnabled(user.getEnabled());
            return dto;
        });
    }
    @Transactional
    @Override
    public User updateUser(Long id, UserUpdateDTO updateDTO) {
        // 1. 校验用户是否存在
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 2. 更新用户信息
        user.setFullName(updateDTO.getFullName());
        user.setEmail(updateDTO.getEmail());
        user.setPhone(updateDTO.getPhone());

        // 3. 保存并返回更新后的用户
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 打印日志确认接收的状态值（调试用）
        System.out.println("更新用户[" + userId + "]状态为：" + enabled);
        user.setEnabled(enabled);
        userRepository.save(user);
    }
}