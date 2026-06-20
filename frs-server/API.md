# 人脸识别签到系统 - 接口文档

## 通用说明

### 基础信息
- Base URL: `http://{host}:8080`
- Content-Type: `application/json`
- 认证方式: Bearer Token (JWT)，除标注"无需认证"的接口外，均需在请求头中携带 `Authorization: Bearer {token}`

### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码，200=成功，400=参数错误，401=未认证，500=服务异常 |
| message | string | 响应消息 |
| data | object | 响应数据 |

---

## 一、认证模块 `/api/auth`

### 1.1 用户注册

- **URL**: `POST /api/auth/register`
- **认证**: 无需认证

**请求参数**:
```json
{
  "username": "zhangsan",
  "password": "123456",
  "phone": "13800138000",
  "faceImageBase64": "base64编码的人脸图片数据"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名，不可重复 |
| password | string | 是 | 密码 |
| phone | string | 否 | 手机号 |
| faceImageBase64 | string | 是 | 人脸照片Base64编码（需包含唯一清晰人脸） |

**成功响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "zhangsan"
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| data.token | string | JWT令牌，后续请求需携带 |
| data.userId | long | 用户ID |
| data.username | string | 用户名 |

**失败场景**:
- 用户名已存在
| 未检测到人脸 / 检测到多张人脸
| 该人脸已被注册

---

### 1.2 用户登录

- **URL**: `POST /api/auth/login`
- **认证**: 无需认证

**请求参数**:
```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**成功响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "zhangsan"
  }
}
```

**失败场景**:
- 用户名或密码错误
| 账号已被禁用

---

## 二、用户模块 `/api/user`

### 2.1 查看人脸信息

- **URL**: `GET /api/user/face`
- **认证**: 需要登录

**请求头**:
```
Authorization: Bearer {token}
```

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "faceId": "xxxxxxxx",
    "externalImageId": "user_1",
    "hasFace": true
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| data.faceId | string | 人脸库中的人脸ID，未注册则为空字符串 |
| data.externalImageId | string | 外部镜像ID（格式: user_{userId}） |
| data.hasFace | boolean | 是否已录入人脸 |

---

### 2.2 更新人脸照片

- **URL**: `PUT /api/user/face`
- **认证**: 需要登录

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "faceImageBase64": "base64编码的新人脸图片数据"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| faceImageBase64 | string | 是 | 新人脸照片Base64编码 |

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "message": "人脸更新成功",
    "faceId": "yyyyyyyy"
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| data.message | string | 操作结果消息 |
| data.faceId | string | 更新后的人脸ID |

**失败场景**:
- 人脸照片不能为空
| 未检测到人脸
| 用户不存在

---

## 三、签到模块 `/api/attendance`

### 3.1 刷脸签到

- **URL**: `POST /api/attendance/sign`
- **认证**: 无需认证（刷脸即可签到）

**请求参数**:
```json
{
  "faceImageBase64": "base64编码的人脸图片数据",
  "deviceType": "MOBILE"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| faceImageBase64 | string | 是 | 人脸照片Base64编码 |
| deviceType | string | 否 | 设备类型，如 MOBILE / PC |

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "message": "签到成功",
    "username": "zhangsan",
    "similarity": 0.98,
    "signTime": "2024-01-15T09:30:00"
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| data.message | string | 签到结果消息 |
| data.username | string | 匹配到的用户名 |
| data.similarity | double | 人脸相似度（0~1，阈值0.93） |
| data.signTime | string | 签到时间 |

**失败场景**:
- 未检测到人脸，请正对摄像头
| 未识别到匹配用户，请先注册
| 用户信息异常，请联系管理员
| 今日已签到，请勿重复签到

---

### 3.2 查看签到记录

- **URL**: `GET /api/attendance/records`
- **认证**: 需要登录

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页条数 |

**请求示例**:
```
GET /api/attendance/records?page=1&size=10
```

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "signDate": "2024-01-15",
        "signTime": "2024-01-15T09:30:00",
        "similarity": 0.98,
        "deviceType": "MOBILE",
        "ipAddress": "192.168.1.100",
        "createdAt": "2024-01-15T09:30:00"
      }
    ],
    "total": 30,
    "size": 10,
    "current": 1,
    "pages": 3
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| data.records | array | 签到记录列表 |
| data.records[].id | long | 记录ID |
| data.records[].userId | long | 用户ID |
| data.records[].signDate | string | 签到日期 |
| data.records[].signTime | string | 签到时间 |
| data.records[].similarity | double | 人脸相似度 |
| data.records[].deviceType | string | 设备类型 |
| data.records[].ipAddress | string | 签到IP地址 |
| data.total | long | 总记录数 |
| data.size | int | 每页条数 |
| data.current | int | 当前页码 |
| data.pages | int | 总页数 |

---

### 3.3 查看今日签到状态

- **URL**: `GET /api/attendance/today`
- **认证**: 需要登录

**请求头**:
```
Authorization: Bearer {token}
```

**成功响应（已签到）**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "signed": true,
    "date": "2024-01-15",
    "signTime": "2024-01-15T09:30:00"
  }
}
```

**成功响应（未签到）**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "signed": false,
    "date": "2024-01-15",
    "signTime": ""
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| data.signed | boolean | 今日是否已签到 |
| data.date | string | 今日日期 |
| data.signTime | string | 签到时间，未签到则为空字符串 |

---

## 四、错误码汇总

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证/Token过期 |
| 403 | 无权限 |
| 500 | 服务内部异常 |

## 五、业务规则

| 规则 | 说明 |
|------|------|
| 人脸相似度阈值 | 0.93（93%），低于此值视为未匹配 |
| 签到防重复 | 每人每天仅可签到一次，Redis缓存+数据库唯一索引双重保障 |
| 人脸注册防重复 | 注册时搜索人脸库，若已存在相似人脸则拒绝注册 |
| 人脸照片要求 | 需包含唯一清晰人脸，不支持多人脸照片 |
| JWT有效期 | 24小时（86400000ms） |
| 人脸库 | face-set-demo |