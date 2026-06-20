package com.example.frs.controller;

import com.example.frs.dto.ApiResponse;
import com.example.frs.service.UserService;
import com.example.frs.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/face")
    public ApiResponse<Map<String, Object>> getFaceInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(userService.getFaceInfo(userId));
    }

    @PutMapping("/face")
    public ApiResponse<Map<String, Object>> updateFace(Authentication authentication,
                                                        @RequestBody Map<String, String> body) {
        Long userId = (Long) authentication.getPrincipal();
        String faceImageBase64 = body.get("faceImageBase64");
        if (StringUtil.isBlank(faceImageBase64)) {
            return ApiResponse.error(400, "人脸照片不能为空");
        }
        return ApiResponse.success(userService.updateFace(userId, faceImageBase64));
    }
}