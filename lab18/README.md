# Lab18 - 프로젝트를 개발 모드와 배포 모드로 실행하기

## 개요
이번 실습에서는 백엔드 및 프론트엔드 프로젝트를 개발 모드와 배포 모드로 실행하는 방법을 학습합니다.

## 목표
- 백엔드를 개발 모드와 배포 모드로 실행하는 방법을 이해합니다.
- 프론트엔드를 개발 모드와 배포 모드로 실행하는 방법을 이해합니다.

## 실습

### 1. 백엔드를 개발 모드와 배포 모드로 실행하기

- `application.properties` 파일을 두 개의 프로파일로 분리합니다.
    - `application-dev.properties`: 개발 모드에서 사용할 설정
    - `application-prod.properties`: 배포 모드에서 사용할 설정
- `application-dev.properties` 파일을 다음과 같이 작성합니다:
    ```properties
    server:
        port: 9999 # 서버 포트 번호

    spring:
    application:
        name: myapp # 애플리케이션 이름
    datasource:
        url: jdbc:mysql://localhost:3306/studydb # DB URL
        username: study # DB 사용자 이름
        password: study # DB 비밀번호
        driver-class-name: com.mysql.cj.jdbc.Driver # MySQL 드라이버 클래스
    jpa:
        hibernate:
        ddl-auto: update
        show-sql: true
        database-platform: org.hibernate.dialect.MySQLDialect
    web:
        resources:
        static-locations: file:src/main/resources/static/ # 정적 리소스 경로
    thymeleaf:
        prefix: file:src/main/resources/templates/ # 템플릿 파일 경로
        suffix: .html # 템플릿 파일 확장자
    ```
- `application-prod.properties` 파일을 다음과 같이 작성합니다:
    ```properties
    ```
- 개발 모드로 실행하기:
  - 환경변수를 통해 설정
    - 예) `$ export SPRING_PROFILES_ACTIVE=dev`
    - 예) `$ gradle bootRun`
    - 예) `$ java -jar hello-api.jar`
  - JVM 아규먼트: `-Dspring.profiles.active=dev`
    - 예) `$ java -Dspring.profiles.active=prod -jar hello-api.jar`
  - 프로그램 아규먼트: `--spring.profiles.active=dev`
    - 예) `$ java -jar hello-api.jar --spring.profiles.active=prod`
    - 예) `$ gradle bootRun --args='--spring.profiles.active=dev'`
  - IntelliJ : 환경변수를 통해 설정한다.
    - bootRun -> 구성 -> 편집: spring.profiles.active=dev

### 2. 프론트엔드를 개발 모드와 배포 모드로 실행하기

- `.env.development` 파일에 REST API 서버 주소를 등록한다.
    ```properties
    NEXT_PUBLIC_REST_API_URL=http://localhost:9999
    ```
    - `npm run dev` 명령어로 개발 모드로 실행할 때는 이 파일의 환경변수를 사용한다.
- `.env.production` 파일에 REST API 서버 주소를 등록한다.
    ```properties
    NEXT_PUBLIC_REST_API_URL=http://localhost:8080
    ```
    - `npm run build` 명령어로 배포 파일을 만들면 이 파일의 환경변수를 사용한다.
- 만약 개발 모드와 배포 모드의 포트 번호를 변경하고 싶다면, `package.json` 파일을 다음과 같이 수정합니다.
    ```json
    {
        "scripts": {
            "dev": "next dev --turbopack -p 3030",
            "build": "next build",
            "start": "next start -p 3000",
            "lint": "next lint"
        }
    }
    ```

### 3. 프론트엔드 REST API 서버 주소 변경하기

- `src/app/boards/page.js` 파일에서 REST API 서버 주소를 다음과 같이 변경합니다.
    ```javascript
    const response = await axios.get(
        `${process.env.NEXT_PUBLIC_REST_API_URL}/boards`,
        {
          headers: {
            Accept: 'application/json',
          },
        },
      );
    ```
- `src/app/boards/[no]/page.js` 파일에서 REST API 서버 주소를 다음과 같이 변경합니다.
    ```javascript
    // 게시글 상세 조회
    const response = await axios.get(
        `${process.env.NEXT_PUBLIC_REST_API_URL}/boards/${boardNo}`,
        {
          headers: {
            Accept: 'application/json',
          },
        },
      );
    ```
    ```javascript
    // 게시글 변경
    const response = await axios.patch(
        `${process.env.NEXT_PUBLIC_REST_API_URL}/boards/${boardNo}`,
        {
            title: formData.title,
            content: formData.content,
        },
        {
            headers: {
            'Content-Type': 'application/json',
            },
        },
    );
    ```
    ```javascript
    // 게시글 삭제
    const response = await axios.delete(
        `${process.env.NEXT_PUBLIC_REST_API_URL}/boards/${boardNo}`,
    );
    ```
- `src/app/boards/new/page.js` 파일에서 게시글 수정 API 호출 부분을 다음과 같이 변경합니다.
    ```javascript
    const response = await axios.post(
        `${process.env.NEXT_PUBLIC_REST_API_URL}/boards`,
        formData,
        {
            headers: {
            'Content-Type': 'application/json',
            },
        },
    );
    ```