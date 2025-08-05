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
  - 요청 파라미터는 `raw` 탭에서 `JSON` 형식으로 전송합니다.
    ```json
    {
        "title": "제목",
        "content": "1234567890"
    }
    ```
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
  - 요청 파라미터는 `raw` 탭에서 `JSON` 형식으로 전송합니다.
    ```json
    {
        "no": 21,
        "title": "제목x",
        "content": "1234567890x"
    }
    ```
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

- `/board/index.html` 파일을 생성하고 다음 코드를 작성합니다:
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 목록</title>
    </head>
    <body>
        <div class="container">
            <h1>게시글 목록</h1>
            
            <div id="loading">
                게시글을 불러오는 중...
            </div>
            
            <div id="error" style="display: none;">
                게시글을 불러오는 중 오류가 발생했습니다.
            </div>
            
            <table id="boardTable" border="1" style="display: none;">
                <thead>
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>작성일</th>
                        <th>조회수</th>
                    </tr>
                </thead>
                <tbody id="boardTableBody">
                    <!-- 게시글 목록이 여기에 동적으로 추가됩니다 -->
                </tbody>
            </table>
            
            <div class="btn-container">
                <a href="/board/form.html" class="btn">새 게시글</a>
                <a href="/" class="btn btn-secondary">홈으로</a>
            </div>
        </div>

        <script>
            // 날짜 포맷팅 함수
            function formatDate(dateString) {
                const date = new Date(dateString);
                return date.toISOString().split('T')[0];
            }

            // 게시글 목록 로드
            async function loadBoards() {
                try {
                    const response = await fetch('http://localhost:9999/board/list');
                    if (!response.ok) {
                        throw new Error('서버 응답 오류');
                    }
                    
                    const jsonResult = await response.json();
                    if (jsonResult.status !== 'success') {
                        throw new Error('요청 처리 오류');
                    }

                    const boards = jsonResult.content;

                    // 로딩 메시지 숨기기
                    document.getElementById('loading').style.display = 'none';
                    
                    if (boards.length === 0) {
                        // 게시글이 없는 경우
                        document.getElementById('boardTableBody').innerHTML = `
                            <tr>
                                <td colspan="4" class="no-data">게시글이 없습니다.</td>
                            </tr>
                        `;
                    } else {
                        // 게시글 목록 렌더링
                        document.getElementById('boardTableBody').innerHTML = boards.map(board => `
                            <tr>
                                <td>${board.no}</td>
                                <td><a href="/board/view.html?no=${board.no}">${board.title}</a></td>
                                <td>${formatDate(board.createdDate)}</td>
                                <td>${board.viewCount}</td>
                            </tr>
                        `).join('');
                    }
                    
                    // 테이블 보이기
                    document.getElementById('boardTable').style.display = 'table';
                    
                } catch (error) {
                    console.error('게시글 로드 오류:', error);
                    
                    // 로딩 메시지 숨기기
                    document.getElementById('loading').style.display = 'none';
                    
                    // 오류 메시지 표시
                    document.getElementById('error').style.display = 'block';
                    
                    // 빈 테이블 표시
                    document.getElementById('boardTableBody').innerHTML = `
                        <tr>
                            <td colspan="4" class="no-data">게시글을 불러올 수 없습니다.</td>
                        </tr>
                    `;
                    document.getElementById('boardTable').style.display = 'table';
                }
            }

            // 페이지 로드 시 게시글 목록 로드
            document.addEventListener('DOMContentLoaded', function() {
                // 페이지 로드 애니메이션
                const container = document.querySelector('.container');
                container.style.opacity = '0';
                container.style.transform = 'translateY(20px)';
                
                setTimeout(() => {
                    container.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
                    container.style.opacity = '1';
                    container.style.transform = 'translateY(0)';
                }, 1000);
                
                // 게시글 목록 로드
                loadBoards();
            });
        </script>
    </body>
  </html>
  ```
 
#### 2.7 게시글 등록

- `/board/form.html` 파일을 생성하고 다음 코드를 작성합니다:
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 작성</title>
        <style>
            #saving {
                display: none;
            }
            .error {
                color: red;
                font-size: 0.9em;
                display: none;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>게시글 작성</h1>
            
            <form id="boardForm">
                <div>
                    <label for="title">제목 *</label>
                    <input type="text" id="title" name="title" required placeholder="게시글 제목을 입력하세요">
                    <div class="error" id="titleError"></div>
                </div>

                <div>
                    <label for="content">내용 *</label>
                    <textarea id="content" name="content" rows="8" required placeholder="게시글 내용을 입력하세요"></textarea>
                    <div class="error" id="contentError"></div>
                </div>

                <div id="saving">
                    게시글을 저장하는 중...
                </div>

                <div>
                    <button type="submit" id="submitBtn">
                        저장
                    </button>
                    <a href="/board">목록으로</a>
                </div>
            </form>
        </div>

        <script>
            // 폼 유효성 검사
            function validateForm() {
                let isValid = true;
                
                // 에러 메시지 초기화
                document.querySelectorAll('.error').forEach(error => {
                    error.style.display = 'none';
                    error.textContent = '';
                });

                // 제목 검사
                const title = document.getElementById('title').value.trim();
                if (!title) {
                    document.getElementById('titleError').textContent = '제목을 입력해주세요.';
                    document.getElementById('titleError').style.display = 'block';
                    isValid = false;
                } else if (title.length < 2 || title.length > 100) {
                    document.getElementById('titleError').textContent = '제목은 2자 이상 100자 이내로 입력해주세요.';
                    document.getElementById('titleError').style.display = 'block';
                    isValid = false;
                }

                // 내용 검사
                const content = document.getElementById('content').value.trim();
                if (!content) {
                    document.getElementById('contentError').textContent = '내용을 입력해주세요.';
                    document.getElementById('contentError').style.display = 'block';
                    isValid = false;
                } else if (content.length < 10 || content.length > 5000) {
                    document.getElementById('contentError').textContent = '내용은 10자 이상 5000자 이내로 입력해주세요.';
                    document.getElementById('contentError').style.display = 'block';
                    isValid = false;
                }

                return isValid;
            }

            // 게시글 저장
            async function saveBoard(formData) {
                const submitBtn = document.getElementById('submitBtn');
                const saving = document.getElementById('saving');
                
                try {
                    // UI 상태 변경
                    submitBtn.disabled = true;
                    saving.style.display = 'block';
                    
                    const response = await fetch('http://localhost:9999/board/add', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            title: formData.get('title'),
                            content: formData.get('content')
                        })
                    });

                    if (!response.ok) {
                        const errorData = await response.json();
                        throw new Error(errorData.content || '서버 응답 오류!');
                    }

                    const jsonResult = await response.json();
                    if (jsonResult.status !== 'success') {
                        throw new Error(jsonResult.content || '요청 처리 오류');
                    }

                    // 0.1초 후 목록 페이지로 이동
                    setTimeout(() => {
                        window.location.href = '/board';
                    }, 100);

                } catch (error) {
                    console.error('게시글 저장 오류:', error);
                    alert(error.message);
                } finally {
                    // UI 상태 복원
                    submitBtn.disabled = false;
                    saving.style.display = 'none';
                }
            }

            // 이벤트 리스너 설정
            document.addEventListener('DOMContentLoaded', function() {
                // 페이지 로드 애니메이션
                const container = document.querySelector('.container');
                container.style.opacity = '0';
                container.style.transform = 'translateY(20px)';
                
                setTimeout(() => {
                    container.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
                    container.style.opacity = '1';
                    container.style.transform = 'translateY(0)';
                }, 1000);

                // 폼 제출 이벤트
                document.getElementById('boardForm').addEventListener('submit', function(e) {
                    e.preventDefault();
                    
                    if (validateForm()) {
                        const formData = new FormData(this);
                        saveBoard(formData);
                    }
                });
            });
        </script>
    </body>
  </html>
  ```

#### 2.8 게시글 상세 조회

- `/board/view.html` 파일을 생성하고 다음 코드를 작성합니다:
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 조회</title>
        <style>
            #loading {
                display: none;
            }
            .error {
                color: red;
                font-size: 0.9em;
                display: none;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>게시글 조회</h1>
            
            <div id="loading">
                게시글을 불러오는 중...
            </div>
            
            <div id="error" style="display: none;">
                게시글을 불러오는 중 오류가 발생했습니다.
            </div>
            
            <form id="boardForm" border="1" style="display: none;">
                <div>
                    <label for="no">번호:</label>
                    <input type="text" id="no" name="no" readonly>
                </div>

                <div>
                    <label for="title">제목:</label>
                    <input type="text" id="title" name="title" required>
                    <div class="error" id="titleError"></div>
                </div>

                <div>
                    <label for="content">내용:</label>
                    <textarea id="content" name="content" rows="6" required></textarea>
                    <div class="error" id="contentError"></div>
                </div>

                <div>
                    <div>
                        <label for="created-date">작성일:</label>
                        <input type="text" id="created-date" readonly>
                    </div>
                    <div>
                        <label for="view-count">조회수:</label>
                        <input type="text" id="view-count" readonly>
                    </div>
                </div>

                <div>
                    <button type="submit" class="btn btn-primary">변경</button>
                    <button type="button" id="deleteBtn" class="btn btn-danger">삭제</button>
                    <a href="/board" class="btn btn-secondary">목록으로</a>
                </div>
            </form>
        </div>

        <script>
            let currentBoardNo = null;

            // URL에서 게시글 번호 추출
            function getBoardNoFromUrl() {
                const urlParams = new URLSearchParams(window.location.search);
                return urlParams.get('no');
            }

            // 날짜 포맷팅 함수
            function formatDate(dateString) {
                const date = new Date(dateString);
                return date.toLocaleDateString('ko-KR', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit'
                });
            }

            // 게시글 상세 정보 로드
            async function loadBoard() {
                const boardNo = getBoardNoFromUrl();
                
                if (!boardNo) {
                    document.getElementById('loading').style.display = 'none';
                    document.getElementById('error').style.display = 'block';
                    document.getElementById('error').textContent = '게시글 번호가 필요합니다.';
                    return;
                }

                try {
                    const response = await fetch(`http://localhost:9999/board/view?no=${boardNo}`);
                    
                    if (!response.ok) {
                        throw new Error('게시글을 찾을 수 없습니다.');
                    }
                    
                    const jsonResult = await response.json();
                    if (jsonResult.status !== 'success') {
                        throw new Error('요청 처리 오류');
                    }

                    const board = jsonResult.content;

                    currentBoardNo = board.no;
                    
                    // 폼에 데이터 채우기
                    document.getElementById('no').value = board.no;
                    document.getElementById('title').value = board.title;
                    document.getElementById('content').value = board.content;
                    document.getElementById('created-date').value = formatDate(board.createdDate);
                    document.getElementById('view-count').value = board.viewCount;
                    
                    // 로딩 숨기고 폼 보이기
                    document.getElementById('loading').style.display = 'none';
                    document.getElementById('boardForm').style.display = 'block';
                    
                } catch (error) {
                    console.error('게시글 로드 오류:', error);
                    document.getElementById('loading').style.display = 'none';
                    document.getElementById('error').style.display = 'block';
                    document.getElementById('error').textContent = error.message;
                }
            }

            // 게시글 수정
            async function updateBoard(formData) {
                try {
                    const response = await fetch(`http://localhost:9999/board/update`, {
                        method: 'PATCH',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            no: formData.get('no'),
                            title: formData.get('title'),
                            content: formData.get('content')
                        })
                    });

                    if (!response.ok) {
                        const errorData = await response.json();
                        throw new Error(errorData.message || '수정에 실패했습니다.');
                    }

                    alert('게시글이 수정되었습니다.');
                    // 페이지 새로고침하여 최신 데이터 표시
                    window.location.reload();

                } catch (error) {
                    console.error('게시글 수정 오류:', error);
                    alert(error.message);
                }
            }

            // 게시글 삭제
            async function deleteBoard() {
                if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
                    return;
                }

                try {
                    const response = await fetch(`http://localhost:9999/board/delete?no=${currentBoardNo}`, {
                        method: 'DELETE'
                    });
                    if (!response.ok) {
                        throw new Error('삭제에 실패했습니다.');
                    }

                    const jsonResult = await response.json();
                    if (jsonResult.status !== 'success') {
                        throw new Error('요청 처리 오류');
                    }

                    window.location.href = '/board';

                } catch (error) {
                    console.error('게시글 삭제 오류:', error);
                    alert(error.message);
                }
            }

            // 폼 유효성 검사
            function validateForm(formData) {
                let isValid = true;
                
                // 에러 메시지 초기화
                document.querySelectorAll('.error').forEach(error => {
                    error.style.display = 'none';
                    error.textContent = '';
                });

                // 제목 검사
                const title = formData.get('title').trim();
                if (!title) {
                    document.getElementById('titleError').textContent = '제목을 입력해주세요.';
                    document.getElementById('titleError').style.display = 'block';
                    isValid = false;
                } else if (title.length < 2 || title.length > 100) {
                    document.getElementById('titleError').textContent = '제목은 2자 이상 100자 이내로 입력해주세요.';
                    document.getElementById('titleError').style.display = 'block';
                    isValid = false;
                }

                // 내용 검사
                const content = formData.get('content').trim();
                if (!content) {
                    document.getElementById('contentError').textContent = '내용을 입력해주세요.';
                    document.getElementById('contentError').style.display = 'block';
                    isValid = false;
                } else if (content.length < 10 || content.length > 5000) {
                    document.getElementById('contentError').textContent = '내용은 10자 이상 5000자 이내로 입력해주세요.';
                    document.getElementById('contentError').style.display = 'block';
                    isValid = false;
                }

                return isValid;
            }

            // 이벤트 리스너 설정
            document.addEventListener('DOMContentLoaded', function() {
                // 페이지 로드 애니메이션
                const container = document.querySelector('.container');
                container.style.opacity = '0';
                container.style.transform = 'translateY(20px)';
                
                setTimeout(() => {
                    container.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
                    container.style.opacity = '1';
                    container.style.transform = 'translateY(0)';
                }, 100);

                // 게시글 로드
                loadBoard();

                // 폼 제출 이벤트
                document.getElementById('boardForm').addEventListener('submit', function(e) {
                    e.preventDefault();
                    
                    const formData = new FormData(this);
                    
                    if (validateForm(formData)) {
                        updateBoard(formData);
                    }
                });

                // 삭제 버튼 이벤트
                document.getElementById('deleteBtn').addEventListener('click', deleteBoard);
            });
        </script>
    </body>
  </html>
  ```
