# Lab13 - jQuery, Handlebars 사용하기

## 개요
이번 실습에서는 jQuery와 Handlebars를 사용하여 동적인 웹 페이지를 구현하는 방법을 배웁니다. jQuery는 HTML 문서 탐색, 이벤트 처리, 애니메이션 및 Ajax 상호 작용을 쉽게 할 수 있도록 도와주는 JavaScript 라이브러리입니다. Handlebars는 템플릿 엔진으로, HTML을 동적으로 생성하는 데 사용됩니다.

## 목표

- jQuery 사용법
- Handlebars 사용법

## 실습

### 1. jQuery 및 Handlebars 자바스크립트 라이브러리 적용하기

- jQuery와 Handlebars 라이브러리를 사용하는 페이지에 다음을 추가합니다.
  ```html
  <!-- CDN으로 라이브러리 로드 -->
  <head>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/handlebars@latest/dist/handlebars.min.js"></script>
  </head>
  ```
    
### 2. 게시글 목록 변경 하기
- `board/index.html` 파일을 다음과 같이 변경합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 목록</title>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/handlebars@latest/dist/handlebars.min.js"></script>
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
            <h1>게시글 목록</h1>
            
            <div id="loading">
                게시글을 불러오는 중...
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
                    <tr id="noDataRow" style="display: none;">
                        <td colspan="4" class="no-data">게시글이 없습니다.</td>
                    </tr>
                    <tr id="errorRow" class="error" style="display: none;">
                        <td colspan="4" class="no-data">게시글을 불러올 수 없습니다.</td>
                    </tr>
                </tbody>
            </table>
            
            <div class="btn-container">
                <a href="/board/form.html" class="btn">새 게시글</a>
                <a href="/" class="btn btn-secondary">홈으로</a>
            </div>
        </div>

        <script id="boards-template" type="text/x-handlebars-template">
            {{#each boards}}
                <tr>
                    <td>{{no}}</td>
                    <td><a href="/board/view.html?no={{no}}">{{title}}</a></td>
                    <td>{{formatDate createdDate}}</td>
                    <td>{{viewCount}}</td>
                </tr>
            {{/each}}
        </script>

        <script>
            // Handlebars 헬퍼 함수 등록
            Handlebars.registerHelper('formatDate', function(dateString) {
                const date = new Date(dateString);
                return date.toISOString().split('T')[0];
            });

            // 게시글 목록 로드
            function loadBoards() {
                $.ajax({
                    url: 'http://localhost:9999/board/list',
                    type: 'GET',
                    dataType: 'json',
                    success: function(jsonResult) {
                        if (jsonResult.status !== 'success') {
                            throw new Error('요청 처리 오류');
                        }

                        const boards = jsonResult.content;

                        // 로딩 메시지 숨기기
                        $('#loading').hide();
                        
                        if (boards.length === 0) {
                            $('#noDataRow').show();

                        } else {
                            // Handlebars 템플릿을 사용하여 게시글 목록 렌더링
                            const source = $('#boards-template').html();
                            const template = Handlebars.compile(source);
                            const html = template({ boards: boards });
                            $('#boardTableBody').html(html);
                        }
                        // 테이블 보이기
                        $('#boardTable').show();
                    },
                    error: function(xhr, status, error) {
                        console.error('게시글 로드 오류:', error);
                        
                        // 로딩 메시지 숨기기
                        $('#loading').hide();
                        
                        // 빈 테이블 표시
                        $('#errorRow').show();
                        $('#boardTable').show();
                    }
                });
            }

            // 페이지 로드 시 게시글 목록 로드
            $(document).ready(function() {
                // 페이지 로드 애니메이션
                const $container = $('.container');
                $container.css({
                    'opacity': '0',
                    'transform': 'translateY(20px)'
                });
                
                setTimeout(() => {
                    $container.css({
                        'transition': 'opacity 0.6s ease, transform 0.6s ease',
                        'opacity': '1',
                        'transform': 'translateY(0)'
                    });
                }, 100);
                
                // 게시글 목록 로드
                loadBoards();
            });
        </script>
    </body>
  </html>
  ```

### 3. 게시글 상세보기 페이지 변경하기
- `board/view.html` 파일을 다음과 같이 변경합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 조회</title>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
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
            function loadBoard() {
                const boardNo = getBoardNoFromUrl();
                
                if (!boardNo) {
                    $('#loading').hide();
                    $('#error').show().text('게시글 번호가 필요합니다.');
                    return;
                }

                $.ajax({
                    url: `http://localhost:9999/board/view?no=${boardNo}`,
                    type: 'GET',
                    dataType: 'json',
                    success: function(jsonResult) {
                        if (jsonResult.status !== 'success') {
                            throw new Error('요청 처리 오류');
                        }

                        const board = jsonResult.content;
                        currentBoardNo = board.no;
                        
                        // 폼에 데이터 채우기
                        $('#no').val(board.no);
                        $('#title').val(board.title);
                        $('#content').val(board.content);
                        $('#created-date').val(formatDate(board.createdDate));
                        $('#view-count').val(board.viewCount);
                        
                        // 로딩 숨기고 폼 보이기
                        $('#loading').hide();
                        $('#boardForm').show();
                    },
                    error: function(xhr, status, error) {
                        console.error('게시글 로드 오류:', error);
                        
                        let errorMessage = '게시글을 찾을 수 없습니다.';
                        if (xhr.status === 404) {
                            errorMessage = '게시글을 찾을 수 없습니다.';
                        } else if (xhr.responseJSON && xhr.responseJSON.message) {
                            errorMessage = xhr.responseJSON.message;
                        }
                        
                        $('#loading').hide();
                        $('#error').show().text(errorMessage);
                    }
                });
            }

            // 게시글 수정
            function updateBoard(formData) {
                $.ajax({
                    url: 'http://localhost:9999/board/update',
                    type: 'PATCH',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        no: formData.get('no'),
                        title: formData.get('title'),
                        content: formData.get('content')
                    }),
                    success: function(result) {
                        alert('게시글이 수정되었습니다.');
                        // 페이지 새로고침하여 최신 데이터 표시
                        window.location.reload();
                    },
                    error: function(xhr, status, error) {
                        console.error('게시글 수정 오류:', error);
                        
                        let errorMessage = '수정에 실패했습니다.';
                        if (xhr.responseJSON && xhr.responseJSON.message) {
                            errorMessage = xhr.responseJSON.message;
                        }
                        
                        alert(errorMessage);
                    }
                });
            }

            // 게시글 삭제
            function deleteBoard() {
                if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
                    return;
                }

                $.ajax({
                    url: `http://localhost:9999/board/delete?no=${currentBoardNo}`,
                    type: 'DELETE',
                    success: function(jsonResult) {
                        if (jsonResult.status !== 'success') {
                            throw new Error('요청 처리 오류');
                        }
                        window.location.href = '/board';
                    },
                    error: function(xhr, status, error) {
                        console.error('게시글 삭제 오류:', error);
                        
                        let errorMessage = '삭제에 실패했습니다.';
                        if (xhr.responseJSON && xhr.responseJSON.message) {
                            errorMessage = xhr.responseJSON.message;
                        }
                        
                        alert(errorMessage);
                    }
                });
            }

            // 폼 유효성 검사
            function validateForm(formData) {
                let isValid = true;
                
                // 에러 메시지 초기화
                $('.error').hide().text('');

                // 제목 검사
                const title = formData.get('title').trim();
                if (!title) {
                    $('#titleError').text('제목을 입력해주세요.').show();
                    isValid = false;
                } else if (title.length < 2 || title.length > 100) {
                    $('#titleError').text('제목은 2자 이상 100자 이내로 입력해주세요.').show();
                    isValid = false;
                }

                // 내용 검사
                const content = formData.get('content').trim();
                if (!content) {
                    $('#contentError').text('내용을 입력해주세요.').show();
                    isValid = false;
                } else if (content.length < 10 || content.length > 5000) {
                    $('#contentError').text('내용은 10자 이상 5000자 이내로 입력해주세요.').show();
                    isValid = false;
                }

                return isValid;
            }

            // 이벤트 리스너 설정
            $(document).ready(function() {
                // 페이지 로드 애니메이션
                const $container = $('.container');
                $container.css({
                    'opacity': '0',
                    'transform': 'translateY(20px)'
                });
                
                setTimeout(() => {
                    $container.css({
                        'transition': 'opacity 0.6s ease, transform 0.6s ease',
                        'opacity': '1',
                        'transform': 'translateY(0)'
                    });
                }, 100);

                // 게시글 로드
                loadBoard();

                // 폼 제출 이벤트
                $('#boardForm').on('submit', function(e) {
                    e.preventDefault();
                    
                    const formData = new FormData(this);
                    
                    if (validateForm(formData)) {
                        updateBoard(formData);
                    }
                });

                // 삭제 버튼 이벤트
                $('#deleteBtn').on('click', deleteBoard);
            });
        </script>
    </body>
  </html>
  ```

### 4. 게시글 작성 페이지 변경하기
- `board/form.html` 파일을 다음과 같이 변경합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 작성</title>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
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
                $('.error').hide().text('');

                // 제목 검사
                const title = $('#title').val().trim();
                if (!title) {
                    $('#titleError').text('제목을 입력해주세요.').show();
                    isValid = false;
                } else if (title.length < 2 || title.length > 100) {
                    $('#titleError').text('제목은 2자 이상 100자 이내로 입력해주세요.').show();
                    isValid = false;
                }

                // 내용 검사
                const content = $('#content').val().trim();
                if (!content) {
                    $('#contentError').text('내용을 입력해주세요.').show();
                    isValid = false;
                } else if (content.length < 10 || content.length > 5000) {
                    $('#contentError').text('내용은 10자 이상 5000자 이내로 입력해주세요.').show();
                    isValid = false;
                }

                return isValid;
            }

            // 게시글 저장
            function saveBoard(formData) {
                const $submitBtn = $('#submitBtn');
                const $saving = $('#saving');
                
                // UI 상태 변경
                $submitBtn.prop('disabled', true);
                $saving.show();
                
                $.ajax({
                    url: 'http://localhost:9999/board/add',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        title: formData.get('title'),
                        content: formData.get('content')
                    }),
                    success: function(jsonResult) {
                        if (jsonResult.status !== 'success') {
                            throw new Error(jsonResult.content || '요청 처리 오류');
                        }

                        // 0.1초 후 목록 페이지로 이동
                        setTimeout(() => {
                            window.location.href = '/board';
                        }, 100);
                    },
                    error: function(xhr, status, error) {
                        console.error('게시글 저장 오류:', error);
                        
                        let errorMessage = '서버 응답 오류!';
                        if (xhr.responseJSON && xhr.responseJSON.content) {
                            errorMessage = xhr.responseJSON.content;
                        }
                        
                        alert(errorMessage);
                    },
                    complete: function() {
                        // UI 상태 복원
                        $submitBtn.prop('disabled', false);
                        $saving.hide();
                    }
                });
            }

            // 이벤트 리스너 설정
            $(document).ready(function() {
                // 페이지 로드 애니메이션
                const $container = $('.container');
                $container.css({
                    'opacity': '0',
                    'transform': 'translateY(20px)'
                });
                
                setTimeout(() => {
                    $container.css({
                        'transition': 'opacity 0.6s ease, transform 0.6s ease',
                        'opacity': '1',
                        'transform': 'translateY(0)'
                    });
                }, 100);

                // 폼 제출 이벤트
                $('#boardForm').on('submit', function(e) {
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