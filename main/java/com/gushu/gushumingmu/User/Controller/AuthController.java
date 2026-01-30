package com.gushu.gushumingmu.User.Controller;

import com.gushu.gushumingmu.User.Dto.LoginResponseDTO;
import com.gushu.gushumingmu.User.Dto.LoginDTO;
import com.gushu.gushumingmu.User.Dto.RegisterDTO;
import com.gushu.gushumingmu.User.Entity.User;
import com.gushu.gushumingmu.User.Service.UserService;
import com.gushu.gushumingmu.User.Util.JwtUtils;
import com.gushu.gushumingmu.User.Util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    // 注册
    @PostMapping("/register")
    public ResultUtil<Void> register(@Validated @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return ResultUtil.success();
    }

    @PostMapping("/login")
    public ResultUtil<LoginResponseDTO> login(@RequestBody LoginDTO dto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getUsername(),
                                dto.getPassword()
                        )
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        // 生成 token
        String token = jwtUtils.generateToken(userDetails.getUsername());

        // 如果你 CustomUserDetails 里能拿到 User 实体
        User user = userService.findByUsername(userDetails.getUsername());

        LoginResponseDTO response = new LoginResponseDTO(
                token,
                user.getUsername(),
                Collections.singleton(user.getFullName()),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()).toString()

        );

        return ResultUtil.success(response);
    }


    // 退出
    @PostMapping("/logout")
    public ResultUtil<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResultUtil.success("退出登录成功");
    }
}