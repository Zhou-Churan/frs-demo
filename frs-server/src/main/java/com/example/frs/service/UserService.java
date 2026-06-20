package com.example.frs.service;

import com.example.frs.entity.User;
import com.example.frs.mapper.UserMapper;
import com.huaweicloud.sdk.frs.v2.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final FrsService frsService;

    public Map<String, Object> getFaceInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("faceId", user.getFaceId() != null ? user.getFaceId() : "");
        result.put("externalImageId", user.getExternalImageId() != null ? user.getExternalImageId() : "");
        result.put("hasFace", user.getFaceId() != null);
        return result;
    }

    @Transactional
    public Map<String, Object> updateFace(Long userId, String faceImageBase64) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 人脸检测
        DetectFaceByBase64Response detectResponse = frsService.detectFace(faceImageBase64);
        if (detectResponse.getFaces() == null || detectResponse.getFaces().isEmpty()) {
            throw new RuntimeException("未检测到人脸");
        }

        // 更新人脸（删除旧的 + 添加新的）
        String externalImageId = "user_" + userId;
        AddFacesByBase64Response addResponse = frsService.updateFace(user.getFaceId(), faceImageBase64, externalImageId);
        List<FaceSetFace> faces = addResponse.getFaces();
        if (faces != null && !faces.isEmpty()) {
            user.setFaceId(faces.get(0).getFaceId());
            user.setExternalImageId(externalImageId);
            userMapper.updateById(user);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("message", "人脸更新成功");
        result.put("faceId", user.getFaceId());
        return result;
    }
}