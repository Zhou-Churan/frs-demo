package com.example.frs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_attendance")
public class Attendance {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate signDate;

    private LocalDateTime signTime;

    private Double similarity;

    private String deviceType;

    private String ipAddress;

    private LocalDateTime createdAt;
}