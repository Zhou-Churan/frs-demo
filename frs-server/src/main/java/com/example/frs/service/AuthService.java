package com.example.frs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.frs.dto.LoginRequest;
import com.example.frs.dto.RegisterRequest;
import com.example.frs.entity.User;
import com.example.frs.mapper.UserMapper;
import com.example.frs.security.JwtTokenProvider;
import com.huaweicloud.sdk.frs.v2.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final FrsService frsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {
        // 1. 检查用户名是否已存在
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 人脸检测
        DetectFaceByBase64Response detectResponse = frsService.detectFace(request.getFaceImageBase64());
        List<DetectFace> faces = detectResponse.getFaces();
        if (faces == null || faces.isEmpty()) {
            throw new RuntimeException("未检测到人脸，请上传包含清晰人脸的照片");
        }
        if (faces.size() > 1) {
            throw new RuntimeException("检测到多张人脸，请上传仅包含一张人脸的照片");
        }

        // 3. 搜索是否已注册过该人脸
        try {
            SearchFaceByBase64Response searchResponse = frsService.searchFace(request.getFaceImageBase64(), 0.93, 1);
            List<SearchFace> searchFaces = searchResponse.getFaces();
            if (searchFaces != null && !searchFaces.isEmpty()) {
                throw new RuntimeException("该人脸已被注册");
            }
        } catch (Exception e) {
            if (e.getMessage().contains("该人脸已被注册")) throw e;
            log.info("人脸库为空或搜索异常，允许继续注册: {}", e.getMessage());
        }

        // 4. 保存用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setStatus(1);
        userMapper.insert(user);

        // 5. 添加人脸到人脸库
        String externalImageId = "user_" + user.getId();
        AddFacesByBase64Response addResponse = frsService.addFace(request.getFaceImageBase64(), externalImageId);
        List<FaceSetFace> addedFaces = addResponse.getFaces();
        if (addedFaces != null && !addedFaces.isEmpty()) {
            user.setFaceId(addedFaces.get(0).getFaceId());
            user.setExternalImageId(externalImageId);
            userMapper.updateById(user);
        }

        // 6. 生成Token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    public Map<String, Object> login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        Map<String, Object> loginResult = new HashMap<>();
        loginResult.put("token", token);
        loginResult.put("userId", user.getId());
        loginResult.put("username", user.getUsername());
        return loginResult;
    }
}