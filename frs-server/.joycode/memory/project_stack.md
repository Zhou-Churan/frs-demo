---
name: project_stack
description: FRS人脸签到系统技术栈和关键约束
type: project
---

- 技术栈: Spring Boot 2.7.18 + Java 1.8 (从3.2/17降级)
- 降级原因: 用户本地环境为JDK 1.8，用户选择降级而非升级JDK
- 关键依赖: MyBatis-Plus 3.5.7 (boot-starter), jjwt 0.11.5, Knife4j 3.0.3 (Swagger2), 华为云FRS SDK 3.1.12
- FRS SDK注意: SearchFaceByBase64的body类是FaceSearchBase64Req(不是SearchFaceByBase64Req)
- 人脸库: face-set-demo
- Spring Boot 2.7适配: javax.servlet(非jakarta), authorizeRequests+antMatchers(非authorizeHttpRequests+requestMatchers), jjwt用parserBuilder+parseClaimsJws(非parser+parseSignedClaims)
- API文档: Knife4j 3.0.3 + @EnableSwagger2WebMvc, 需spring.mvc.pathmatch.matching-strategy=ant_path_matcher
- application.yml中Redis配置路径: spring.redis (非spring.data.redis)