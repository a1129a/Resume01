# 多阶段构建：第一阶段 - 编译
FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app
# 利用Docker缓存机制优化构建
COPY pom.xml .
COPY user-service/pom.xml user-service/
COPY common/pom.xml common/
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY common/src common/src
COPY user-service/src user-service/src
RUN mvn clean package -pl common,user-service -am -DskipTests

# 第二阶段 - 运行环境
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 设置中国时区和安全配置
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 创建非root用户运行应用
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 从构建阶段复制jar文件
COPY --from=build /app/user-service/target/*.jar app.jar

# 配置健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# 配置JVM参数和启动命令
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
