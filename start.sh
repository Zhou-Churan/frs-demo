#!/bin/bash
set -e

# 启动nginx（后台）
nginx -g "daemon on;"

# 启动后端（前台，保持容器运行）
exec java -jar /app/frs-server.jar