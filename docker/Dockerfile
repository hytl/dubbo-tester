FROM openjdk:21-slim
WORKDIR /app
COPY ./dubbo-tester-bs-1.0-SNAPSHOT.jar ./app.jar

# 环境变量
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "-XX:+UseZGC", "-XX:+ZGenerational", "app.jar""]