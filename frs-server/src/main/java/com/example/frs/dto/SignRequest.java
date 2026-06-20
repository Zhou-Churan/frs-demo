package com.example.frs.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignRequest {

    @NotBlank(message = "人脸照片不能为空")
    private String faceImageBase64;

    private String deviceType;
}