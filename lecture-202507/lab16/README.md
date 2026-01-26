# Lab16 - UI 프레임워크 `React` 도입

## 개요
이번 실습에서는 UI 프레임워크인 `React`를 도입하여 게시판 UI를 구성하는 방법을 배웁니다.

## 목표
- React 기본 개념 이해
- 게시판 UI 구성 요소 구현

## 실습

### 1. 게시글 목록 조회

- `board/index.html` 파일을 수정하여 게시글 목록을 조회하는 UI를 구현합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 목록</title>
        <script crossorigin src="https://unpkg.com/react@18/umd/react.development.js"></script>
        <script crossorigin src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
        <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
        <style>
            .container {
                max-width: 800px;
                margin: 0 auto;
                padding: 20px;
            }
            
            .loading {
                text-align: center;
                font-size: 1.1em;
                color: #666;
                margin: 20px 0;
            }
            
            .error {
                color: red;
                font-size: 0.9em;
                text-align: center;
                margin: 20px 0;
            }
            
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 20px 0;
            }
            
            th, td {
                padding: 10px;
                text-align: left;
                border: 1px solid #ddd;
            }
            
            th {
                background-color: #f5f5f5;
                font-weight: bold;
            }
            
            .no-data {
                text-align: center;
                color: #666;
                font-style: italic;
            }
    
        </style>
    </head>
    <body>
        <div id="root"></div>

        <script type="text/babel">
            const { useState, useEffect, useCallback } = React;
            
            // 날짜 포맷팅 함수
            const formatDate = (dateString) => {
                const date = new Date(dateString);
                return date.toISOString().split('T')[0];
            };

            // BoardList 컴포넌트
            const BoardList = () => {
                const [boards, setBoards] = useState([]);
                const [loading, setLoading] = useState(true);
                const [error, setError] = useState(null);
                
                // useCallback으로 함수 메모이제이션 - 의존성이 변하지 않으면 같은 함수 참조 유지
                const loadBoards = useCallback(async () => {
                    try {
                        setLoading(true);
                        setError(null);
                        
                        const response = await axios.get('http://localhost:9999/boards');
                        const jsonResult = response.data;
                        
                        if (jsonResult.status !== 'success') {
                            throw new Error('요청 처리 오류');
                        }

                        setBoards(jsonResult.content);
                    } catch (err) {
                        console.error('게시글 로드 오류:', err);
                        setError('게시글을 불러올 수 없습니다.');
                    } finally {
                        setLoading(false);
                    }
                }, []); // 빈 의존성 배열 - 컴포넌트 생명주기 동안 함수가 변하지 않음

                // 컴포넌트 마운트 시 게시글 로드
                useEffect(() => {
                    loadBoards();
                }, []);

                return (
                    <div className="container">
                        <h1>게시글 목록</h1>
                        
                        {loading && (
                            <div className="loading">
                                게시글을 불러오는 중...
                            </div>
                        )}
                        
                        {error && (
                            <div className="error">
                                {error}
                            </div>
                        )}
                        
                        {!loading && !error && (
                            <table>
                                <thead>
                                    <tr>
                                        <th>번호</th>
                                        <th>제목</th>
                                        <th>작성일</th>
                                        <th>조회수</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {boards.length === 0 ? (
                                        <tr>
                                            <td colSpan="4" className="no-data">
                                                게시글이 없습니다.
                                            </td>
                                        </tr>
                                    ) : (
                                        boards.map(board => (
                                            <tr key={board.no}>
                                                <td>{board.no}</td>
                                                <td>
                                                    <a href={`/board/view.html?no=${board.no}`}>
                                                        {board.title}
                                                    </a>
                                                </td>
                                                <td>{formatDate(board.createdDate)}</td>
                                                <td>{board.viewCount}</td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        )}
                        
                        <div>
                            [<a href="/board/form.html">새 게시글</a>]
                            [<a href="/">홈으로</a>]
                        </div>
                    </div>
                );
            };

            // App 컴포넌트 렌더링
            const container = document.getElementById('root');
            const root = ReactDOM.createRoot(container);
            root.render(<BoardList />);

        </script>
    </body>
  </html>
  ```
  
### 2. 게시글 등록
- `board/form.html` 파일을 수정하여 게시글 작성 UI를 구현합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 작성</title>
        <script crossorigin src="https://unpkg.com/react@18/umd/react.development.js"></script>
        <script crossorigin src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
        <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
        <style>
            .container {
                max-width: 600px;
                margin: 0 auto;
                padding: 20px;
            }
            
            .form-group {
                margin: 15px 0;
            }
            
            label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }
            
            input[type="text"], textarea {
                width: 100%;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
                box-sizing: border-box;
            }
            
            textarea {
                resize: vertical;
            }
            
            .error {
                color: red;
                font-size: 0.9em;
                margin-top: 5px;
            }
            
            .saving {
                text-align: center;
                color: #666;
                margin: 20px 0;
            }
            
            .buttons {
                margin: 20px 0;
            }
            
            button {
                background-color: #007bff;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                margin-right: 10px;
            }
            
            button:disabled {
                background-color: #ccc;
                cursor: not-allowed;
            }
            
            a {
                color: #007bff;
                text-decoration: none;
            }
            
            a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <div id="root"></div>

        <script type="text/babel">
            const { useState } = React;
            
            // 컴포넌트 외부에 정의된 순수 함수들
            const validateFormData = (formData) => {
                const newErrors = {};
                
                // 제목 검사
                const title = formData.title.trim();
                if (!title) {
                    newErrors.title = '제목을 입력해주세요.';
                } else if (title.length < 2 || title.length > 100) {
                    newErrors.title = '제목은 2자 이상 100자 이내로 입력해주세요.';
                }
                
                // 내용 검사
                const content = formData.content.trim();
                if (!content) {
                    newErrors.content = '내용을 입력해주세요.';
                } else if (content.length < 10 || content.length > 5000) {
                    newErrors.content = '내용은 10자 이상 5000자 이내로 입력해주세요.';
                }
                
                return newErrors;
            };
            
            const saveBoardData = async (formData) => {
                const response = await axios.post('http://localhost:9999/boards', {
                    title: formData.title,
                    content: formData.content
                }, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                const jsonResult = response.data;
                
                if (jsonResult.status !== 'success') {
                    throw new Error(jsonResult.content || '요청 처리 오류');
                }
                
                return jsonResult;
            };
            
            // BoardForm 컴포넌트
            const BoardForm = () => {
                const [formData, setFormData] = useState({
                    title: '',
                    content: ''
                });
                const [errors, setErrors] = useState({});
                const [saving, setSaving] = useState(false);
                
                // 입력값 변경 핸들러
                const handleInputChange = (e) => {
                    const { name, value } = e.target;
                    setFormData(prev => ({
                        ...prev,
                        [name]: value
                    }));
                    
                    // 에러 메시지 초기화
                    setErrors(prev => {
                        if (prev[name]) {
                            return {
                                ...prev,
                                [name]: ''
                            };
                        }
                        return prev;
                    });
                };
                
                // 폼 유효성 검사 - 외부 함수 사용
                const validateForm = () => {
                    const newErrors = validateFormData(formData);
                    setErrors(newErrors);
                    return Object.keys(newErrors).length === 0;
                };
                
                // 게시글 저장 - 외부 함수 사용
                const saveBoard = async () => {
                    try {
                        setSaving(true);
                        
                        await saveBoardData(formData);
                        
                        // 0.1초 후 목록 페이지로 이동
                        setTimeout(() => {
                            window.location.href = '/board';
                        }, 100);
                        
                    } catch (error) {
                        console.error('게시글 저장 오류:', error);
                        
                        let errorMessage = '서버 응답 오류!';
                        if (error.response && error.response.data && error.response.data.content) {
                            errorMessage = error.response.data.content;
                        }
                        
                        alert(errorMessage);
                    } finally {
                        setSaving(false);
                    }
                };
                
                // 폼 제출 핸들러
                const handleSubmit = (e) => {
                    e.preventDefault();
                    
                    if (validateForm()) {
                        saveBoard();
                    }
                };
                
                return (
                    <div className="container">
                        <h1>게시글 작성</h1>
                        
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="title">제목 *</label>
                                <input 
                                    type="text" 
                                    id="title" 
                                    name="title" 
                                    value={formData.title}
                                    onChange={handleInputChange}
                                    placeholder="게시글 제목을 입력하세요"
                                    required 
                                />
                                {errors.title && <div className="error">{errors.title}</div>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="content">내용 *</label>
                                <textarea 
                                    id="content" 
                                    name="content" 
                                    rows="8" 
                                    value={formData.content}
                                    onChange={handleInputChange}
                                    placeholder="게시글 내용을 입력하세요"
                                    required
                                />
                                {errors.content && <div className="error">{errors.content}</div>}
                            </div>

                            {saving && (
                                <div className="saving">
                                    게시글을 저장하는 중...
                                </div>
                            )}

                            <div className="buttons">
                                <button type="submit" disabled={saving}>
                                    저장
                                </button>
                                <a href="/board">목록으로</a>
                            </div>
                        </form>
                    </div>
                );
            };

            // App 컴포넌트 렌더링
            const container = document.getElementById('root');
            const root = ReactDOM.createRoot(container);
            root.render(<BoardForm />);

        </script>
    </body>
  </html>
  ```

### 3. 게시글 상세 조회 및 변경

- `board/view.html` 파일을 수정하여 게시글 상세 조회 및 변경 기능을 구현합니다.
  ```html
  <!DOCTYPE html>
  <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>게시글 조회</title>
        <script crossorigin src="https://unpkg.com/react@18/umd/react.development.js"></script>
        <script crossorigin src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>
        <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
        <style>
            .container {
                max-width: 600px;
                margin: 0 auto;
                padding: 20px;
            }
            
            .form-group {
                margin: 15px 0;
            }
            
            .form-row {
                display: flex;
                gap: 20px;
            }
            
            .form-row .form-group {
                flex: 1;
            }
            
            label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }
            
            input[type="text"], textarea {
                width: 100%;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
                box-sizing: border-box;
            }
            
            input[readonly] {
                background-color: #f5f5f5;
                color: #666;
            }
            
            textarea {
                resize: vertical;
            }
            
            .error {
                color: red;
                font-size: 0.9em;
                margin-top: 5px;
            }
            
            .loading {
                text-align: center;
                color: #666;
                margin: 20px 0;
            }
            
            .error-message {
                color: red;
                text-align: center;
                margin: 20px 0;
            }
            
            .buttons {
                margin: 20px 0;
            }
            
            button {
                background-color: #007bff;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                margin-right: 10px;
            }
            
            button.btn-danger {
                background-color: #dc3545;
            }
            
            button:disabled {
                background-color: #ccc;
                cursor: not-allowed;
            }
            
            a {
                color: #007bff;
                text-decoration: none;
                padding: 10px 20px;
                border: 1px solid #007bff;
                border-radius: 4px;
                display: inline-block;
            }
            
            a:hover {
                background-color: #007bff;
                color: white;
            }
        </style>
    </head>
    <body>
        <div id="root"></div>

        <script type="text/babel">
            const { useState, useEffect } = React;
            
            // 컴포넌트 외부에 정의된 순수 함수들
            const getBoardNoFromUrl = () => {
                const urlParams = new URLSearchParams(window.location.search);
                return urlParams.get('no');
            };
            
            const formatDate = (dateString) => {
                const date = new Date(dateString);
                return date.toLocaleDateString('ko-KR', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit'
                });
            };
            
            const validateBoardData = (formData) => {
                const newErrors = {};
                
                // 제목 검사
                const title = formData.title.trim();
                if (!title) {
                    newErrors.title = '제목을 입력해주세요.';
                } else if (title.length < 2 || title.length > 100) {
                    newErrors.title = '제목은 2자 이상 100자 이내로 입력해주세요.';
                }
                
                // 내용 검사
                const content = formData.content.trim();
                if (!content) {
                    newErrors.content = '내용을 입력해주세요.';
                } else if (content.length < 10 || content.length > 5000) {
                    newErrors.content = '내용은 10자 이상 5000자 이내로 입력해주세요.';
                }
                
                return newErrors;
            };
            
            const loadBoardData = async (boardNo) => {
                const response = await axios.get(`http://localhost:9999/boards/${boardNo}`);
                const jsonResult = response.data;
                
                if (jsonResult.status !== 'success') {
                    throw new Error('요청 처리 오류');
                }
                
                return jsonResult.content;
            };
            
            const updateBoardData = async (boardNo, boardData) => {
                const response = await axios.patch(`http://localhost:9999/boards/${boardNo}`, {
                    title: boardData.title,
                    content: boardData.content
                }, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                return response.data;
            };
            
            const deleteBoardData = async (boardNo) => {
                const response = await axios.delete(`http://localhost:9999/boards/${boardNo}`);
                const jsonResult = response.data;
                
                if (jsonResult.status !== 'success') {
                    throw new Error('요청 처리 오류');
                }
                
                return jsonResult;
            };
            
            // BoardView 컴포넌트
            const BoardView = () => {
                const [board, setBoard] = useState(null);
                const [formData, setFormData] = useState({
                    title: '',
                    content: ''
                });
                const [errors, setErrors] = useState({});
                const [loading, setLoading] = useState(true);
                const [error, setError] = useState(null);
                const [updating, setUpdating] = useState(false);
                
                // 게시글 로드
                const loadBoard = async () => {
                    const boardNo = getBoardNoFromUrl();
                    
                    if (!boardNo) {
                        setLoading(false);
                        setError('게시글 번호가 필요합니다.');
                        return;
                    }
                    
                    try {
                        setLoading(true);
                        setError(null);
                        
                        const boardData = await loadBoardData(boardNo);
                        
                        setBoard(boardData);
                        setFormData({
                            title: boardData.title,
                            content: boardData.content
                        });
                    } catch (err) {
                        console.error('게시글 로드 오류:', err);
                        
                        let errorMessage = '게시글을 찾을 수 없습니다.';
                        if (err.response && err.response.status === 404) {
                            errorMessage = '게시글을 찾을 수 없습니다.';
                        } else if (err.response && err.response.data && err.response.data.message) {
                            errorMessage = err.response.data.message;
                        }
                        
                        setError(errorMessage);
                    } finally {
                        setLoading(false);
                    }
                };
                
                // 컴포넌트 마운트 시 게시글 로드
                useEffect(() => {
                    loadBoard();
                }, []);
                
                // 입력값 변경 핸들러
                const handleInputChange = (e) => {
                    const { name, value } = e.target;
                    setFormData(prev => ({
                        ...prev,
                        [name]: value
                    }));
                    
                    // 에러 메시지 초기화
                    if (errors[name]) {
                        setErrors(prev => ({
                            ...prev,
                            [name]: ''
                        }));
                    }
                };
                
                // 폼 유효성 검사
                const validateForm = () => {
                    const newErrors = validateBoardData(formData);
                    setErrors(newErrors);
                    return Object.keys(newErrors).length === 0;
                };
                
                // 게시글 수정
                const updateBoard = async () => {
                    try {
                        setUpdating(true);
                        
                        await updateBoardData(board.no, formData);
                        
                        alert('게시글이 수정되었습니다.');
                        // 페이지 새로고침하여 최신 데이터 표시
                        window.location.reload();
                    } catch (error) {
                        console.error('게시글 수정 오류:', error);
                        
                        let errorMessage = '수정에 실패했습니다.';
                        if (error.response && error.response.data && error.response.data.message) {
                            errorMessage = error.response.data.message;
                        }
                        
                        alert(errorMessage);
                    } finally {
                        setUpdating(false);
                    }
                };
                
                // 게시글 삭제
                const deleteBoard = async () => {
                    if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
                        return;
                    }
                    
                    try {
                        await deleteBoardData(board.no);
                        window.location.href = '/board';
                    } catch (error) {
                        console.error('게시글 삭제 오류:', error);
                        
                        let errorMessage = '삭제에 실패했습니다.';
                        if (error.response && error.response.data && error.response.data.message) {
                            errorMessage = error.response.data.message;
                        }
                        
                        alert(errorMessage);
                    }
                };
                
                // 폼 제출 핸들러
                const handleSubmit = (e) => {
                    e.preventDefault();
                    
                    if (validateForm()) {
                        updateBoard();
                    }
                };
                
                if (loading) {
                    return (
                        <div className="container">
                            <h1>게시글 조회</h1>
                            <div className="loading">
                                게시글을 불러오는 중...
                            </div>
                        </div>
                    );
                }
                
                if (error) {
                    return (
                        <div className="container">
                            <h1>게시글 조회</h1>
                            <div className="error-message">
                                {error}
                            </div>
                            <div className="buttons">
                                <a href="/board">목록으로</a>
                            </div>
                        </div>
                    );
                }
                
                return (
                    <div className="container">
                        <h1>게시글 조회</h1>
                        
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="no">번호:</label>
                                <input 
                                    type="text" 
                                    id="no" 
                                    name="no" 
                                    value={board.no}
                                    readOnly 
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="title">제목:</label>
                                <input 
                                    type="text" 
                                    id="title" 
                                    name="title" 
                                    value={formData.title}
                                    onChange={handleInputChange}
                                    required 
                                />
                                {errors.title && <div className="error">{errors.title}</div>}
                            </div>

                            <div className="form-group">
                                <label htmlFor="content">내용:</label>
                                <textarea 
                                    id="content" 
                                    name="content" 
                                    rows="6" 
                                    value={formData.content}
                                    onChange={handleInputChange}
                                    required
                                />
                                {errors.content && <div className="error">{errors.content}</div>}
                            </div>

                            <div className="form-row">
                                <div className="form-group">
                                    <label htmlFor="created-date">작성일:</label>
                                    <input 
                                        type="text" 
                                        id="created-date" 
                                        value={formatDate(board.createdDate)}
                                        readOnly 
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="view-count">조회수:</label>
                                    <input 
                                        type="text" 
                                        id="view-count" 
                                        value={board.viewCount}
                                        readOnly 
                                    />
                                </div>
                            </div>

                            <div className="buttons">
                                <button type="submit" disabled={updating}>
                                    {updating ? '수정 중...' : '변경'}
                                </button>
                                <button type="button" className="btn-danger" onClick={deleteBoard}>
                                    삭제
                                </button>
                                <a href="/board">목록으로</a>
                            </div>
                        </form>
                    </div>
                );
            };

            // App 컴포넌트 렌더링
            const container = document.getElementById('root');
            const root = ReactDOM.createRoot(container);
            root.render(<BoardView />);

        </script>
    </body>
  </html>
  ```