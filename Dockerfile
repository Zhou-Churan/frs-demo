# ===== 阶段1: 构建前端 =====
FROM node:20-alpine AS frontend-builder

WORKDIR /build/frs-frontend
COPY frs-frontend/package.json frs-frontend/package-lock.json ./
RUN npm ci
COPY frs-frontend/ ./
RUN npm run build

# ===== 阶段2: 构建后端 =====
FROM maven:3.8-openjdk-8 AS backend-builder

WORKDIR /build/frs-server
COPY frs-server/pom.xml ./
RUN mvn dependency:go-offline -B
COPY frs-server/src ./src
RUN mvn package -DskipTests -B

# ===== 阶段3: 运行时 =====
FROM openjdk:8-jdk

# 安装nginx
RUN apt-get update && \
    apt-get install -y --no-install-recommends nginx && \
    rm -rf /var/lib/apt/lists/*

# 复制后端jar
COPY --from=backend-builder /build/frs-server/target/frs-server-1.0-SNAPSHOT.jar /app/frs-server.jar

# 复制前端构建产物到nginx目录
COPY --from=frontend-builder /build/frs-frontend/dist /var/www/frs-frontend

# 配置nginx
COPY nginx/frs.conf /etc/nginx/sites-available/frs
RUN rm -f /etc/nginx/sites-enabled/default && \
    ln -s /etc/nginx/sites-available/frs /etc/nginx/sites-enabled/frs

# 启动脚本
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

# 初始化SQL（供外部导入到MySQL）
COPY frs-server/src/main/resources/db/init.sql /app/init.sql

EXPOSE 80

CMD ["/app/start.sh"]