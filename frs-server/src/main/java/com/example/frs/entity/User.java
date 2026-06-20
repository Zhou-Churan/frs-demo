package com.example.frs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String phone;

    private String faceId;

    private String externalImageId;

    private String faceImageUrl;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}