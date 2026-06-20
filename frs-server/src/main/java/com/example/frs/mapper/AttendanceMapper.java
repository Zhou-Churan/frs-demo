package com.example.frs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.frs.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
}