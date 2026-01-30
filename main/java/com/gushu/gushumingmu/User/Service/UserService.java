package com.gushu.gushumingmu.User.Service;

import com.gushu.gushumingmu.User.Dto.RegisterDTO;
import com.gushu.gushumingmu.User.Dto.UserDTO;
import com.gushu.gushumingmu.User.Dto.UserProfileDTO;
import com.gushu.gushumingmu.User.Entity.User;
import org.springframework.data.domain.Page;
import com.gushu.gushumingmu.User.Dto.UserUpdateDTO;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    void register(RegisterDTO dto);
    User findByUsername(String username);
    void assignRole(Long userId, Long roleId);
    void removeRole(Long userId, Long roleId);
    User getCurrentUser();
    void updateProfile(UserProfileDTO profileDTO);
    void updatePassword(String oldPassword, String newPassword);
    Page<UserDTO> findUsersByUsername(String username, int page, int size);

    @Transactional
    User updateUser(Long id, UserUpdateDTO updateDTO);

    void resetPassword(Long userId, String newPassword);
    void updateUserStatus(Long userId, boolean enabled);
}