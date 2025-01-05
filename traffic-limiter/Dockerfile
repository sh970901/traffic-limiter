# 1. Gradle 빌드를 위한 베이스 이미지 설정 (JDK 23)
FROM gradle:jdk23-jammy AS build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle 빌드에 필요한 소스 복사
# 프로젝트의 전체 소스를 Docker 컨테이너로 복사
COPY . .

# 4. Gradle 빌드 실행
RUN gradle clean build -x test

# 5. 실행 환경용 이미지 설정 (JDK 23 런타임만 포함)
FROM gradle:jdk23-jammy

# 6. 작업 디렉토리 설정
WORKDIR /app

# 7. 빌드에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/blog4j.limiter-api-0.0.1-SNAPSHOT.jar app.jar

# 8. 애플리케이션 실행 명령
#CMD ["java", "-jar", "app.jar"]
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=dev -jar app.jar"]