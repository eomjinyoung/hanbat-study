# Lab12 - 웹 애플리케이션 아키텍처를 백엔드와 프론트엔드로 전환하기

## 개요
이번 실습에서는 웹 애플리케이션 아키텍처를 백엔드와 프론트엔드로 전환하는 방법을 배웁니다.

## 목표

- 백엔드와 프론트엔드 아키텍처의 특징
- 백엔드에서 Rest API를 구축하는 방법
- 프론트엔드에서 AJAX를 이용하여 Rest API를 호출하는 방법

## 실습

### 1. 백엔드에서 Rest API 구축하기

#### 1.1 Rest API 테스트 도구 준비

- Postman 도구를 설치합니다.

#### 1.2 Lombok 라이브러리 설치

- `build.gradle` 파일에 다음 의존성을 추가합니다:
  ```groovy
  dependencies {
      implementation 'org.projectlombok:lombok:1.18.38'
      annotationProcessor 'org.projectlombok:lombok:1.18.38'
  }
  ```
- Intellij IDEA에서 Lombok 플러그인을 설치합니다.
  - Intellij IDEA에서 `Preferences > Plugins`로 이동하여 `Lombok` 플러그인을 검색하고 설치합니다.
- Intellij IDEA에서 `Preferences > Build, Execution, Deployment > Compiler > Annotation Processors`로 이동하여 `Enable annotation processing`을 체크합니다.

#### 1.3 Rest API 응답 결과를 담을 DTO 클래스 생성

- `src/main/java/org/example/myapp/controller/JsonResult.java` 파일을 생성하고 다음 코드를 작성합니다:
  ```java
  @Data
  @Builder
  public class JsonResult {
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    private final String status;
    private final Object content;
  }
  ```

#### 1.4 Rest API 컨트롤러로 변경
- `BoardController` 클래스를 @RestController로 변경합니다.
  ```java
  @RestController
  @RequestMapping("/board")
  public class BoardController {
    ...
  }
- `list()` 메서드를 다음과 같이 변경합니다.
  ```java
  @GetMapping("/list")
  public JsonResult list(Model model) {
    List<BoardSummaryDto> boards = boardService.getAllBoards();
    return JsonResult.builder()
        .status(JsonResult.SUCCESS)
        .content(boards.stream().map(BoardResponse::from).toList())
        .build();
  }
  ```
  - Postman에서 `http://localhost:8080/board/list` URL로 GET 요청을 보내고 응답 결과를 확인합니다.
- `view()` 메서드를 다음과 같이 변경합니다.
  ```java
  @GetMapping("/view")
  public JsonResult view(@RequestParam("no") Long no, Model model) {
    BoardDetailDto boardDetailDto = boardService.getBoardByNoWithViewCount(no);
    return JsonResult.builder()
        .status(JsonResult.SUCCESS)
        .content(BoardResponse.from(boardDetailDto))
        .build();
  }
  ```
  - Postman에서 `http://localhost:8080/board/view` URL로 GET 요청을 보내고 응답 결과를 확인합니다.
  - 요청 파라미터는 `Params` 탭에서 `no`를 입력합니다.
- `add()`메서드를 다음과 같이 변경합니다:
  ```java
    @PostMapping("/add")
    public JsonResult add(
        @Valid @ModelAttribute BoardCreateRequest request, BindingResult bindingResult) {
      if (bindingResult.hasErrors()) {
        return JsonResult.builder()
            .status(JsonResult.FAILURE)
            .content(bindingResult.getFieldErrors().getFirst().getDefaultMessage())
            .build();
      }
      boardService.addBoard(request);
      return JsonResult.builder().status(JsonResult.SUCCESS).build();
    }
  ```
  - Postman에서 `http://localhost:8080/board/add` URL로 POST 요청을 보내고 응답 결과를 확인합니다.
  - 요청 파라미터는 `Body` 탭에서 `x-www-form-urlencoded` 형식으로 전송합니다.
    - title: 게시글 제목
    - content: 게시글 내용
  - 제목과 내용을 최소 길이 이하 또는 이상으로 입력하여 검증 실패를 유도하여 에러 메시지를 확인합니다.
- `update()` 메서드를 다음과 같이 변경합니다:  
  ```java
  @PatchMapping("/update")
  public JsonResult update(
      @Valid @ModelAttribute BoardUpdateRequest request, BindingResult bindingResult, Model model) {

    // 검증 실패 시 에러 응답
    if (bindingResult.hasErrors()) {
      return JsonResult.builder()
          .status(JsonResult.FAILURE)
          .content(bindingResult.getFieldErrors().getFirst().getDefaultMessage())
          .build();
    }

    boardService.updateBoard(request);
    return JsonResult.builder().status(JsonResult.SUCCESS).build();
  }
  ```
  - Postman에서 `http://localhost:8080/board/update` URL로 PATCH 요청을 보내고 응답 결과를 확인합니다.
  - 요청 파라미터는 `Body` 탭에서 `x-www-form-urlencoded` 형식으로 전송합니다.
    - no: 게시글 번호
    - title: 게시글 제목
    - content: 게시글 내용
  - 제목과 내용을 최소 길이 이하 또는 이상으로 입력하여 검증 실패를 유도하여 에러 메시지를 확인합니다.
- `delete()` 메서드를 다음과 같이 변경합니다:  
  ```java
    @DeleteMapping("/delete")
    public JsonResult delete(@RequestParam("no") Long no) {
      boardService.deleteBoard(no);
      return JsonResult.builder().status(JsonResult.SUCCESS).build();
    }
  }
  ```
  - Postman에서 `http://localhost:8080/board/delete` URL로 DELETE 요청을 보내고 응답 결과를 확인합니다.
  - 요청 파라미터는 `Params` 탭에서 `no`를 입력합니다.

### 2. 프론트엔드에서 AJAX 호출하기

#### 2.1 Node.js + Express 설치

- `Node.js`가 설치되어 있는지 확인합니다.
  ```bash
  node --version
  npm --version
  ```
- 프로젝트 준비
  ```bash
  # 프로젝트 폴더 생성 및 이동
  mkdir myapp-ui
  cd myapp-ui

  # package.json 파일 생성
  npm init -y
  ```
- **Express** 설치
  ```bash
  # Express 프레임워크 설치
  npm install express
  ```
#### 2.2 서버 실행 스크립트 생성

- `server.js` 파일을 생성하고 다음 코드를 작성합니다:
  ```javascript
  const express = require('express');
  const path = require('path');
  const app = express();
  const PORT = process.env.PORT || 3000;

  // 미들웨어 설정
  app.use(express.json());
  app.use(express.urlencoded({ extended: true }));

  // 정적 파일 서빙 (index.html 자동 서빙 포함)
  app.use(express.static(path.join(__dirname, 'public')));

  // 서버 시작
  app.listen(PORT, () => {
    console.log(`서버가 http://localhost:${PORT} 에서 실행 중입니다.`);
  });
  ```
- `public` 폴더를 생성하고, 그 안에 `index.html` 파일을 생성합니다:
  ```bash
  <!DOCTYPE html>
  <html lang="ko">
  <head>
      <meta charset="UTF-8">
      <title>웹 애플리케이션 실습</title>
  </head>
  <body>
      <h1>웹 애플리케이션 실습</h1>
      <p>Express.js를 활용한 웹 서버 실습 프로젝트</p>
  </body>
  </html>
  ```

#### 2.3 서버 실행

- `server.js` 파일을 실행합니다:
  ```bash
  node server.js
  ```
  - 브라우저에서 `http://localhost:3000` URL로 접속하여 기본 페이지의 화면을 확인합니다.

#### 2.4 npm 으로 서버 실행

- `package.json` 파일에 다음 스크립트를 추가합니다:
  ```json
  "scripts": {
    "start": "node server.js"
  }
  ```
- 다음 명령어로 서버를 실행합니다:
  ```bash
  npm start
  ```

#### 2.5 Nodemon 설치

- Nodemon을 설치하여 파일 변경 시 자동으로 서버를 재시작할 수 있습니다
  ```bash
  # Nodemon 설치
  npm install --save-dev nodemon
  ```
  - `--save-dev` 옵션은 개발 환경에서만 필요한 의존성을 설치합니다.
    - `devDependencies` 에 설치되어 프로덕션 빌드 시 제외됨
    - 배포 용량을 줄이고 성능을 최적화할 수 있음
- 직접 실행 
  ```bash
  npx nodemon server.js
  ```
- npm 으로 실행
  -  `package.json` 파일에 다음 스크립트를 추가합니다:
    ```json
    "scripts": {
      "dev": "nodemon server.js"
    }
    ```
  - 다음 명령어로 Nodemon을 사용하여 서버를 실행합니다:
    ```bash
    npm run dev
    ```
#### 2.6 게시글 목록 조회

- `/board/list.html` 파일을 생성하고 다음 코드를 작성합니다:
```html

```
 

- XMLHttpRequest를 이용하여 Rest API를 호출하는 방법을 배웁니다.
- Fetch API를 이용하여 Rest API를 호출하는 방법을 배웁니다.
- jQuery를 이용하여 Rest API를 호출하는 방법을 배웁니다.
- Axios를 이용하여 Rest API를 호출하는 방법을 배웁니다.