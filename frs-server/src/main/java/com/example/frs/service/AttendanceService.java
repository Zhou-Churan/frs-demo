package com.example.frs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.frs.dto.SignRequest;
import com.example.frs.entity.Attendance;
import com.example.frs.entity.User;
import com.example.frs.mapper.AttendanceMapper;
import com.example.frs.mapper.UserMapper;
import com.huaweicloud.sdk.frs.v2.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final UserMapper userMapper;
    private final FrsService frsService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final double SIMILARITY_THRESHOLD = 0.93;
    private static final String ATTENDANCE_KEY_PREFIX = "attendance:";

    /**
     * 刷脸签到
     */
    public Map<String, Object> signByFace(SignRequest request, String ipAddress) {
        // 1. 人脸检测
        DetectFaceByBase64Response detectResponse = frsService.detectFace(request.getFaceImageBase64());
        if (detectResponse.getFaces() == null || detectResponse.getFaces().isEmpty()) {
            throw new RuntimeException("未检测到人脸，请正对摄像头");
        }

        // 2. 人脸搜索
        SearchFaceByBase64Response searchResponse = frsService.searchFace(
                request.getFaceImageBase64(), SIMILARITY_THRESHOLD, 1);
        List<SearchFace> faces = searchResponse.getFaces();
        if (faces == null || faces.isEmpty()) {
            throw new RuntimeException("未识别到匹配用户，请先注册");
        }

        SearchFace matchedFace = faces.get(0);
        String externalImageId = matchedFace.getExternalImageId();
        double similarity = matchedFace.getSimilarity();

        // 3. 通过externalImageId找到用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getExternalImageId, externalImageId));
        if (user == null) {
            throw new RuntimeException("用户信息异常，请联系管理员");
        }

        // 4. Redis检查今日是否已签到
        LocalDate today = LocalDate.now();
        String redisKey = ATTENDANCE_KEY_PREFIX + user.getId() + ":" + today;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new RuntimeException("今日已签到，请勿重复签到");
        }

        // 5. 写入签到记录
        Attendance attendance = new Attendance();
        attendance.setUserId(user.getId());
        attendance.setSignDate(today);
        attendance.setSignTime(LocalDateTime.now());
        attendance.setSimilarity(similarity);
        attendance.setDeviceType(request.getDeviceType());
        attendance.setIpAddress(ipAddress);

        try {
            attendanceMapper.insert(attendance);
        } catch (Exception e) {
            throw new RuntimeException("今日已签到，请勿重复签到");
        }

        // 6. 设置Redis缓存（TTL到当天结束）
        Duration ttl = Duration.between(LocalDateTime.now(), LocalDateTime.of(today.plusDays(1), LocalTime.MIDNIGHT));
        redisTemplate.opsForValue().set(redisKey, "1", ttl);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "签到成功");
        result.put("username", user.getUsername());
        result.put("similarity", similarity);
        result.put("signTime", attendance.getSignTime().toString());
        return result;
    }

    /**
     * 查看签到记录
     */
    public Page<Attendance> getRecords(Long userId, int page, int size) {
        Page<Attendance> pageParam = new Page<>(page, size);
        return attendanceMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getUserId, userId)
                        .orderByDesc(Attendance::getSignDate));
    }

    /**
     * 查看今日签到状态
     */
    public Map<String, Object> getTodayStatus(Long userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceMapper.selectOne(
                new LambdaQueryWrapper<Attendance>()
                        .eq(Attendance::getUserId, userId)
                        .eq(Attendance::getSignDate, today));
        boolean signed = attendance != null;
        Map<String, Object> result = new HashMap<>();
        result.put("signed", signed);
        result.put("date", today.toString());
        result.put("signTime", signed ? attendance.getSignTime().toString() : "");
        return result;
    }
}