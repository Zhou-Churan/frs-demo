package com.example.frs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.frs.dto.ApiResponse;
import com.example.frs.dto.SignRequest;
import com.example.frs.entity.Attendance;
import com.example.frs.service.AttendanceService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/sign")
    public ApiResponse<Map<String, Object>> sign(@Valid @RequestBody SignRequest request,
                                                  HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        return ApiResponse.success(attendanceService.signByFace(request, ipAddress));
    }

    @GetMapping("/records")
    public ApiResponse<Page<Attendance>> getRecords(Authentication authentication,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(attendanceService.getRecords(userId, page, size));
    }

    @GetMapping("/today")
    public ApiResponse<Map<String, Object>> getTodayStatus(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(attendanceService.getTodayStatus(userId));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}