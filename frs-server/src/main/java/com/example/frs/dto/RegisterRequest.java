package com.example.frs.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String phone;

    @NotBlank(message = "人脸照片不能为空")
    private String faceImageBase64;
}