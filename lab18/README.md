# Lab17 - React 프레임워크 `Next.js`로 프론트엔드 프로젝트 구성하기

## 개요
이번 실습에서는 Next.js를 사용하여 React 기반의 게시판 UI를 구성합니다. 게시글 목록 조회, 등록, 상세 조회 및 변경 기능을 구현합니다. 

## 목표
- Next.js를 이용한 React 애플리케이션 구조 이해
- Prettier와 ESLint 설정
- VSCode에서 Prettier 포맷 설정
- 게시글 목록 조회, 등록, 상세 조회 및 변경 기능 구현

## 실습

### 1. myapp-nextjs 프로젝트 생성

- `myapp-nextjs` 디렉토리로 이동 후, Next.js 프로젝트를 생성합니다.
    ```bash
    $ cd myapp-nextjs
    $ npx create-next-app@latest .
    Need to install the following packages:
    create-next-app@15.4.6
    Ok to proceed? (y) y

    ✔ What is your project named? … myapp-nextjs
    ✔ Would you like to use TypeScript? … [No] / Yes
    ✔ Would you like to use ESLint? … No / [Yes]
    ✔ Would you like to use Tailwind CSS? … No / [Yes]
    ✔ Would you like your code inside a `src/` directory? … No / [Yes]
    ✔ Would you like to use App Router? (recommended) … No / [Yes]
    ✔ Would you like to use Turbopack for `next dev`? … No / [Yes]
    ✔ Would you like to customize the import alias (`@/*` by default)? … [No] / Yes
    Creating a new Next.js app in /Users/eomjinyoung/git/hanbat-study/myapp-nextjs/myapp-nextjs.

    Using npm.

    Initializing project with template: app-tw 


    Installing dependencies:
    - react
    - react-dom
    - next

    Installing devDependencies:
    - @tailwindcss/postcss
    - tailwindcss
    - eslint
    - eslint-config-next
    - @eslint/eslintrc


    added 396 packages, and audited 397 packages in 2m

    166 packages are looking for funding
    run `npm fund` for details

    found 0 vulnerabilities
    Success! Created myapp-nextjs at /Users/eomjinyoung/git/hanbat-study/myapp-nextjs/myapp-nextjs
    ```
- 프로젝트 디렉토리의 구조를 확인합니다.
    ```bash
    $ tree -I 'node_modules|.git|dist'
    .
    ├── eslint.config.mjs
    ├── jsconfig.json
    ├── next.config.mjs
    ├── package-lock.json
    ├── package.json
    ├── postcss.config.mjs
    ├── public
    │   ├── file.svg
    │   ├── globe.svg
    │   ├── next.svg
    │   ├── vercel.svg
    │   └── window.svg
    ├── README.md
    └── src
        └── app
            ├── favicon.ico
            ├── globals.css
            ├── layout.js
            └── page.js

    4 directories, 16 files
    ```
- Next.js 실행합니다.
    ```bash
    $ npm run dev

    > myapp-nextjs@0.1.0 dev
    > next dev

    Local: http://localhost:3000
    On Your Network: http://<your-ip-address>:3000
    ```

### 2. Prettier 설정

- Prettier를 설치합니다.
    ```bash
    $ npm install --save-dev prettier
    ```
- 프로젝트 루트에 `.prettierrc` 파일을 생성하고 다음 내용을 추가합니다.
    ```json
    {
      "semi": true,
      "singleQuote": true,
      "tabWidth": 2,
      "trailingComma": "es5",
      "printWidth": 100,
      "useTabs": false,
      "bracketSpacing": true,
      "jsxBracketSameLine": false,
      "arrowParens": "always",
      "endOfLine": "lf"     
    }
    ```
- `.prettierignore` 파일을 생성하고 다음 내용을 추가합니다.
    ```
    .next
    node_modules
    public
    build
    coverage
    out
    dist
    .vscode
    .git
    .DS_Store
    *.log
    ``` 
- VSCode에서 Prettier를 기본 포맷터로 설정합니다.
    - settings.json 파일에 다음 설정을 추가합니다.
    ```json
    {
        "[javascript]": {
            "editor.defaultFormatter": "esbenp.prettier-vscode",
            "editor.formatOnSave": true
        },
        "[javascriptreact]": {
            "editor.defaultFormatter": "esbenp.prettier-vscode",
            "editor.formatOnSave": true
        },
        "[typescript]": {
            "editor.defaultFormatter": "esbenp.prettier-vscode",
            "editor.formatOnSave": true
        },
        "[typescriptreact]": {
            "editor.defaultFormatter": "esbenp.prettier-vscode",
            "editor.formatOnSave": true
        }
    }
    ```

### 3. ESLint와 Prettier 통합
- ESLint와 Prettier를 통합하기 위해 `eslint-plugin-prettier`와 `eslint-config-prettier`를 설치합니다.
    ```bash
    $ npm install --save-dev eslint-plugin-prettier eslint-config-prettier
    ```
- `eslint.config.mjs` 파일을 수정하여 Prettier 설정을 추가합니다.
    ```javascript
    import { dirname } from "path";
    import { fileURLToPath } from "url";
    import { FlatCompat } from "@eslint/eslintrc";
    import prettierPlugin from "eslint-plugin-prettier";
    import prettierConfig from "eslint-config-prettier";

    const __filename = fileURLToPath(import.meta.url);
    const __dirname = dirname(__filename);

    const compat = new FlatCompat({
    baseDirectory: __dirname,
    });

    const eslintConfig = [
    ...compat.extends("next/core-web-vitals"),

    // Prettier plugin and config (Flat config 방식)
    {
        plugins: {
        prettier: prettierPlugin,
        },
        rules: {
        "prettier/prettier": "error", // Prettier 규칙을 ESLint 에러로 표시
        },
    },
    {
        ...prettierConfig,
    },
    ];

    export default eslintConfig;
    ```

### 4. 페이지 기본 화면 구성

- 프로젝트 구조
    ```
    myapp-nextjs/
    ├── src/
    │   ├── app/
    │   │   ├── layout.js (공통 레이아웃 - Header, Footer 포함)
    │   │   └── page.js (홈페이지 - 콘텐츠만)
    │   └── components/
    │       ├── Header.js
    │       └── Footer.js
    ```
- `Header.js` 파일을 생성하고 공통 레이아웃을 정의합니다.
    ```javascript
    export default function Header() {
        return (
            <header className="bg-blue-600 text-white p-4 shadow-md">
            <div className="container mx-auto flex justify-between items-center">
                <div className="flex items-center space-x-2">
                <h1 className="text-xl font-bold">한밭대학교</h1>
                <span className="text-blue-200">|</span>
                <span className="text-lg">웹개발 실무</span>
                </div>
                <nav>
                <ul className="flex space-x-6">
                    <li><a href="/" className="hover:text-blue-200 transition-colors">홈</a></li>
                    <li><a href="/boards" className="hover:text-blue-200 transition-colors">게시판</a></li>
                    <li>
                </ul>
                </nav>
            </div>
            </header>
        );
    }
    ```
- `Footer.js` 파일을 생성하고 공통 레이아웃을 정의합니다.
    ```javascript
    export default function Footer() {
        return (
            <footer className="bg-gray-800 text-white py-8 mt-auto">
            <div className="container mx-auto px-4">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                <div>
                    <h3 className="text-lg font-semibold mb-3">한밭대학교 웹개발 실무</h3>
                    <p className="text-gray-300 text-sm">
                    Next.js와 React를 활용한<br />
                    웹 애플리케이션 개발 실습 프로젝트
                    </p>
                </div>
                <div>
                    <h4 className="font-semibold mb-3">빠른 링크</h4>
                    <ul className="space-y-2 text-sm">
                    <li><a href="#" className="text-gray-300 hover:text-white transition-colors">실습 가이드</a></li>
                    <li><a href="#" className="text-gray-300 hover:text-white transition-colors">소스 코드</a></li>
                    <li><a href="#" className="text-gray-300 hover:text-white transition-colors">과제 제출</a></li>
                    <li><a href="#" className="text-gray-300 hover:text-white transition-colors">Q&A</a></li>
                    </ul>
                </div>
                <div>
                    <h4 className="font-semibold mb-3">연락처</h4>
                    <div className="text-sm text-gray-300 space-y-1">
                    <p>📧 support@hanbat.ac.kr</p>
                    <p>📞 042-821-1114</p>
                    <p>🏫 대전광역시 유성구 동서대로 125</p>
                    </div>
                </div>
                </div>
                <div className="border-t border-gray-700 mt-6 pt-6 text-center">
                <p className="text-gray-400 text-sm">
                    © 2025 한밭대학교. All rights reserved. | 
                    <span className="text-gray-300"> Powered by Next.js</span>
                </p>
                </div>
            </div>
            </footer>
        );
    }
    ```
- `layout.js` 파일을 수정하여 Header와 Footer를 포함한 레이아웃을 정의합니다.
    ```javascript
    import { Geist, Geist_Mono } from 'next/font/google';
    import './globals.css';
    import Header from '../components/Header';
    import Footer from '../components/Footer';

    const geistSans = Geist({
    variable: '--font-geist-sans',
    subsets: ['latin'],
    });

    const geistMono = Geist_Mono({
    variable: '--font-geist-mono',
    subsets: ['latin'],
    });

    export const metadata = {
    title: '한밭대학교 웹개발 실무',
    description: 'Next.js와 React를 활용한 웹 애플리케이션 개발 실습',
    };

    export default function RootLayout({ children }) {
        return (
            <html lang="ko">
            <body
                className={`${geistSans.variable} ${geistMono.variable} antialiased min-h-screen flex flex-col`}
            >
                <Header />
                <main className="flex-1">{children}</main>
                <Footer />
            </body>
            </html>
        );
    }
    ```
- `page.js` 파일을 수정하여 기본 콘텐츠를 추가합니다.
    ```javascript
    export default function Home() {
        return (
            <div className="py-16 px-4">
            <div className="container mx-auto max-w-4xl text-center">
                <h1 className="text-4xl md:text-5xl font-bold text-gray-800 mb-6">웹개발 실무 실습 프로젝트</h1>
                <p className="text-xl text-gray-600 leading-relaxed">React 프레임워크 Next.js를 활용하여 프론트엔드 프로젝트 만들기!</p>
            </div>
            </div>
        );
    }    
    ```

### 5. 게시글 목록 조회 페이지 구현

- `axios`를 설치하여 API 호출을 위한 HTTP 클라이언트를 추가합니다.
    ```bash
    $ npm install axios
    ```
- 게시글 페이지 구조
    ```
    myapp-nextjs/
    └── src
        ├── app
        │   ├── boards
        │   │   └── page.js
    ```
- `page.js` 파일을 생성하고 게시글 목록을 조회하는 컴포넌트를 작성합니다.
    ```javascript
    'use client';

    import { useState, useEffect, useCallback } from 'react';
    import axios from 'axios';
    import Link from 'next/link';

    export default function BoardList() {
        const [boards, setBoards] = useState([]);
        const [loading, setLoading] = useState(true);
        const [error, setError] = useState(null);

        // 날짜 포맷팅 함수
        const formatDate = (dateString) => {
            const date = new Date(dateString);
            return date.toISOString().split('T')[0];
        };

        // useCallback으로 함수 메모이제이션 - 의존성이 변하지 않으면 같은 함수 참조 유지
        const loadBoards = useCallback(async () => {
            try {
            setLoading(true);
            setError(null);

            const response = await axios.get('http://localhost:9999/boards', {
                headers: {
                Accept: 'application/json',
                },
            });
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
        }, [loadBoards]);

        return (
            <div className="py-8 px-4">
            <div className="container mx-auto max-w-6xl">
                {/* 페이지 헤더 */}
                <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-800 mb-2">게시판</h1>
                <p className="text-gray-600">웹개발 실무 실습 게시판입니다.</p>
                </div>

                {/* 검색 및 필터 영역 */}
                <div className="bg-white rounded-lg shadow-sm border p-6 mb-6">
                <div className="flex flex-col md:flex-row gap-4 items-center justify-between">
                    <div className="flex gap-2 w-full md:w-auto">
                    <select className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option>제목</option>
                        <option>작성자</option>
                        <option>내용</option>
                    </select>
                    <input
                        type="text"
                        placeholder="검색어를 입력하세요"
                        className="flex-1 md:w-64 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <button className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
                        검색
                    </button>
                    </div>
                    <Link
                    href="/boards/new"
                    className="w-full md:w-auto px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors text-center inline-block"
                    >
                    글쓰기
                    </Link>
                </div>
                </div>

                {/* 게시글 목록 테이블 */}
                <div className="bg-white rounded-lg shadow-sm border overflow-hidden">
                {/* 로딩 상태 */}
                {loading && (
                    <div className="flex justify-center items-center py-12">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    <span className="ml-2 text-gray-600">로딩 중...</span>
                    </div>
                )}

                {/* 에러 상태 */}
                {error && (
                    <div className="flex justify-center items-center py-12">
                    <div className="text-center">
                        <div className="text-red-500 text-lg mb-2">⚠️ 오류</div>
                        <p className="text-gray-600">{error}</p>
                        <button
                        onClick={loadBoards}
                        className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                        >
                        다시 시도
                        </button>
                    </div>
                    </div>
                )}

                {/* 데이터가 있고 로딩/에러가 아닐 때만 테이블 표시 */}
                {!loading && !error && (
                    <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            번호
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            제목
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            작성자
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            작성일
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            조회수
                            </th>
                        </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                        {boards.length === 0 ? (
                            <tr>
                            <td
                                colSpan="5"
                                className="px-6 py-12 text-center text-gray-500"
                            >
                                등록된 게시글이 없습니다.
                            </td>
                            </tr>
                        ) : (
                            boards.map((board) => (
                            <tr
                                key={board.no}
                                className="hover:bg-gray-50 transition-colors"
                            >
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                {board.no}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                <Link
                                    href={`/boards/${board.no}`}
                                    className="text-sm text-blue-600 hover:text-blue-800 hover:underline font-medium"
                                >
                                    {board.title}
                                </Link>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                {board.writer || '익명'}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {board?.createdDate
                                    ? formatDate(board.createdDate)
                                    : ''}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {board.viewCount || 0}
                                </td>
                            </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                    </div>
                )}
                </div>

                {/* 페이지네이션 */}
                <div className="mt-6 flex justify-center">
                <nav className="flex items-center space-x-2">
                    <button className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
                    이전
                    </button>
                    <button className="px-3 py-2 text-sm font-medium text-white bg-blue-600 border border-blue-600 rounded-md">
                    1
                    </button>
                    <button className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
                    2
                    </button>
                    <button className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
                    3
                    </button>
                    <button className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
                    다음
                    </button>
                </nav>
                </div>
            </div>
            </div>
        );
    }
    ```

### 6. 게시글 등록 페이지 구현

- 게시글 페이지 구조
    ```
    myapp-nextjs/
    └── src
        ├── app
        │   ├── boards
        │   │   └── page.js
        │   │   ├── new
        │   │   │   └── page.js
    ```
- 게시글 등록 페이지를 구현하기 위해 `boards/new` 경로를 추가합니다.
- `src/app/boards/new/page.js` 파일을 생성합니다.
    ```javascript
    'use client';

    import { useState } from 'react';
    import axios from 'axios';
    import { useRouter } from 'next/navigation';

    export default function BoardForm() {
        const router = useRouter();
        const [formData, setFormData] = useState({
            title: '',
            content: '',
        });
        const [errors, setErrors] = useState({});
        const [saving, setSaving] = useState(false);
        const [error, setError] = useState(null);

        // 유효성 검사 함수
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

        // 입력값 변경 처리
        const handleChange = (e) => {
            const { name, value } = e.target;
            setFormData((prev) => ({
            ...prev,
            [name]: value,
            }));

            // 에러 메시지 초기화 (사용자가 입력을 시작하면 해당 필드의 에러 제거)
            if (errors[name]) {
            setErrors((prev) => ({
                ...prev,
                [name]: '',
            }));
            }
        };

        // 폼 제출 처리
        const handleSubmit = async (e) => {
            e.preventDefault();

            // 유효성 검사
            const validationErrors = validateFormData(formData);
            if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
            }

            try {
            setSaving(true);
            setError(null);
            setErrors({});

            const response = await axios.post(
                'http://localhost:9999/boards',
                formData,
                {
                headers: {
                    'Content-Type': 'application/json',
                },
                },
            );
            const jsonResult = response.data;

            if (jsonResult.status !== 'success') {
                // 서버에서 전달한 오류 메시지를 error 상태에 설정
                const errorMessage =
                jsonResult.content || '게시글 등록에 실패했습니다.';
                setError(errorMessage);
                return;
            }

            alert('게시글이 성공적으로 등록되었습니다.');
            router.push('/boards'); // 게시글 목록으로 이동
            } catch (err) {
            console.error('게시글 등록 오류:', err);

            // 서버 응답에서 오류 메시지 추출하여 error 상태에 설정
            let errorMessage = '게시글 등록 중 오류가 발생했습니다.';
            if (err.response && err.response.data && err.response.data.content) {
                errorMessage = err.response.data.content;
            }

            setError(errorMessage);
            } finally {
            setSaving(false);
            }
        };

        // 취소 버튼 처리
        const handleCancel = () => {
            if (confirm('작성을 취소하시겠습니까? 입력한 내용이 사라집니다.')) {
            router.push('/boards');
            }
        };

        return (
            <div className="py-8 px-4">
            <div className="container mx-auto max-w-4xl">
                {/* 페이지 헤더 */}
                <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-800 mb-2">게시글 작성</h1>
                <p className="text-gray-600">새로운 게시글을 작성해주세요.</p>
                </div>

                {/* 에러 메시지 */}
                {error && (
                <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
                    <div className="flex justify-between items-start">
                    <div className="flex">
                        <div className="text-red-500">⚠️</div>
                        <div className="ml-2 text-red-700">{error}</div>
                    </div>
                    <button
                        onClick={() => setError(null)}
                        className="text-red-400 hover:text-red-600 ml-4"
                        title="오류 메시지 닫기"
                    >
                        ✕
                    </button>
                    </div>
                </div>
                )}

                {/* 게시글 작성 폼 */}
                <div className="bg-white rounded-lg shadow-sm border p-6">
                <form onSubmit={handleSubmit}>
                    {/* 제목 입력 */}
                    <div className="mb-6">
                    <label
                        htmlFor="title"
                        className="block text-sm font-medium text-gray-700 mb-2"
                    >
                        제목 <span className="text-red-500">*</span>
                    </label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        value={formData.title}
                        onChange={handleChange}
                        placeholder="게시글 제목을 입력하세요 (2-100자)"
                        className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                        errors.title ? 'border-red-500' : 'border-gray-300'
                        }`}
                        disabled={saving}
                    />
                    {errors.title && (
                        <p className="mt-1 text-sm text-red-600">{errors.title}</p>
                    )}
                    <p className="mt-1 text-xs text-gray-500">
                        {formData.title.length}/100자
                    </p>
                    </div>

                    {/* 내용 입력 */}
                    <div className="mb-6">
                    <label
                        htmlFor="content"
                        className="block text-sm font-medium text-gray-700 mb-2"
                    >
                        내용 <span className="text-red-500">*</span>
                    </label>
                    <textarea
                        id="content"
                        name="content"
                        value={formData.content}
                        onChange={handleChange}
                        placeholder="게시글 내용을 입력하세요 (10-5000자)"
                        rows={10}
                        className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 resize-vertical ${
                        errors.content ? 'border-red-500' : 'border-gray-300'
                        }`}
                        disabled={saving}
                    />
                    {errors.content && (
                        <p className="mt-1 text-sm text-red-600">{errors.content}</p>
                    )}
                    <p className="mt-1 text-xs text-gray-500">
                        {formData.content.length}/5000자
                    </p>
                    </div>

                    {/* 버튼 영역 */}
                    <div className="flex justify-end space-x-3">
                    <button
                        type="button"
                        onClick={handleCancel}
                        disabled={saving}
                        className="px-6 py-2 text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 transition-colors disabled:opacity-50"
                    >
                        취소
                    </button>
                    <button
                        type="submit"
                        disabled={saving}
                        className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {saving ? '등록 중...' : '등록'}
                    </button>
                    </div>
                </form>
                </div>

                {/* 안내 사항 */}
                <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-md">
                <h3 className="text-sm font-medium text-blue-800 mb-2">
                    📝 작성 안내
                </h3>
                <ul className="text-sm text-blue-700 space-y-1">
                    <li>• 제목: 2자 이상 100자 이내로 입력해주세요.</li>
                    <li>• 내용: 10자 이상 5000자 이내로 입력해주세요.</li>
                    <li>• 작성한 게시글은 목록에서 확인할 수 있습니다.</li>
                    <li>• 부적절한 내용은 관리자에 의해 삭제될 수 있습니다.</li>
                </ul>
                </div>
            </div>
            </div>
        );
    }
    ```

### 7. 게시글 상세조회/변경/삭제 페이지 구현

- 게시글 페이지 구조
    ```
    myapp-nextjs/
    └── src
        ├── app
        │   ├── boards
        │   │   ├── [no]
        │   │   │   └── page.js
        │   │   ├── new
        │   │   │   └── page.js
        │   │   └── page.js
    ```
- 게시글 상세조회/변경/삭제 페이지를 구현하기 위해 `boards/[no]` 경로를 추가합니다.
- `src/app/boards/[no]/page.js` 파일을 생성합니다.
    ```javascript
    'use client';

    import { useState, useEffect } from 'react';
    import axios from 'axios';
    import { useRouter, useParams } from 'next/navigation';
    import Link from 'next/link';

    export default function BoardView() {
        const router = useRouter();
        const params = useParams();
        const boardNo = params.no;

        const [board, setBoard] = useState(null);
        const [formData, setFormData] = useState({
            title: '',
            content: '',
        });
        const [errors, setErrors] = useState({});
        const [loading, setLoading] = useState(true);
        const [error, setError] = useState(null);
        const [updating, setUpdating] = useState(false);

        // 날짜 포맷 함수
        const formatDate = (dateString) => {
            const date = new Date(dateString);
            return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            });
        };

        // 유효성 검사 함수
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

        // 게시글 로드
        const loadBoard = async () => {
            if (!boardNo) {
            setLoading(false);
            setError('게시글 번호가 필요합니다.');
            return;
            }

            try {
            setLoading(true);
            setError(null);

            const response = await axios.get(
                `http://localhost:9999/boards/${boardNo}`,
                {
                headers: {
                    Accept: 'application/json',
                },
                },
            );
            const jsonResult = response.data;

            if (jsonResult.status !== 'success') {
                throw new Error('요청 처리 오류');
            }

            const boardData = jsonResult.content;
            setBoard(boardData);
            setFormData({
                title: boardData.title,
                content: boardData.content,
            });
            } catch (err) {
            console.error('게시글 로드 오류:', err);

            let errorMessage = '게시글을 찾을 수 없습니다.';
            if (err.response && err.response.status === 404) {
                errorMessage = '게시글을 찾을 수 없습니다.';
            } else if (
                err.response &&
                err.response.data &&
                err.response.data.content
            ) {
                errorMessage = err.response.data.content;
            }

            setError(errorMessage);
            } finally {
            setLoading(false);
            }
        };

        // 컴포넌트 마운트 시 게시글 로드
        useEffect(() => {
            loadBoard();
        }, [boardNo]);

        // 입력값 변경 핸들러
        const handleInputChange = (e) => {
            const { name, value } = e.target;
            setFormData((prev) => ({
            ...prev,
            [name]: value,
            }));

            // 에러 메시지 초기화
            if (errors[name]) {
            setErrors((prev) => ({
                ...prev,
                [name]: '',
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
            setError(null);

            const response = await axios.patch(
                `http://localhost:9999/boards/${boardNo}`,
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

            const jsonResult = response.data;
            if (jsonResult.status !== 'success') {
                setError(jsonResult.content || '수정에 실패했습니다.');
                return;
            }

            // 수정 성공 시 다시 로드
            await loadBoard();
            alert('게시글이 수정되었습니다.');
            } catch (err) {
            console.error('게시글 수정 오류:', err);

            let errorMessage = '수정에 실패했습니다.';
            if (err.response && err.response.data && err.response.data.content) {
                errorMessage = err.response.data.content;
            }

            setError(errorMessage);
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
            const response = await axios.delete(
                `http://localhost:9999/boards/${boardNo}`,
            );
            const jsonResult = response.data;

            if (jsonResult.status !== 'success') {
                throw new Error('요청 처리 오류');
            }

            router.push('/boards');
            } catch (err) {
            console.error('게시글 삭제 오류:', err);

            let errorMessage = '삭제에 실패했습니다.';
            if (err.response && err.response.data && err.response.data.content) {
                errorMessage = err.response.data.content;
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
            <div className="py-8 px-4">
                <div className="container mx-auto max-w-4xl">
                <h1 className="text-3xl font-bold text-gray-800 mb-8">게시글 조회</h1>
                <div className="flex justify-center items-center py-12">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    <span className="ml-2 text-gray-600">게시글을 불러오는 중...</span>
                </div>
                </div>
            </div>
            );
        }

        if (error) {
            return (
            <div className="py-8 px-4">
                <div className="container mx-auto max-w-4xl">
                <h1 className="text-3xl font-bold text-gray-800 mb-8">게시글 조회</h1>
                <div className="bg-red-50 border border-red-200 rounded-md p-6 text-center">
                    <div className="text-red-500 text-lg mb-2">⚠️ 오류</div>
                    <p className="text-red-700 mb-4">{error}</p>
                    <Link
                    href="/boards"
                    className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                    >
                    목록으로
                    </Link>
                </div>
                </div>
            </div>
            );
        }

        return (
            <div className="py-8 px-4">
            <div className="container mx-auto max-w-4xl">
                {/* 페이지 헤더 */}
                <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-800 mb-2">게시글 조회</h1>
                <p className="text-gray-600">게시글을 확인하고 수정할 수 있습니다.</p>
                </div>

                {/* 에러 메시지 */}
                {error && (
                <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
                    <div className="flex justify-between items-start">
                    <div className="flex">
                        <div className="text-red-500">⚠️</div>
                        <div className="ml-2 text-red-700">{error}</div>
                    </div>
                    <button
                        onClick={() => setError(null)}
                        className="text-red-400 hover:text-red-600 ml-4"
                        title="오류 메시지 닫기"
                    >
                        ✕
                    </button>
                    </div>
                </div>
                )}

                {/* 게시글 수정 폼 */}
                <div className="bg-white rounded-lg shadow-sm border p-6">
                <form onSubmit={handleSubmit}>
                    {/* 번호 (읽기 전용) */}
                    <div className="mb-6">
                    <label
                        htmlFor="no"
                        className="block text-sm font-medium text-gray-700 mb-2"
                    >
                        번호
                    </label>
                    <input
                        type="text"
                        id="no"
                        value={board?.no || ''}
                        readOnly
                        className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-600"
                    />
                    </div>

                    {/* 제목 */}
                    <div className="mb-6">
                    <label
                        htmlFor="title"
                        className="block text-sm font-medium text-gray-700 mb-2"
                    >
                        제목 <span className="text-red-500">*</span>
                    </label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        value={formData.title}
                        onChange={handleInputChange}
                        placeholder="게시글 제목을 입력하세요 (2-100자)"
                        className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                        errors.title ? 'border-red-500' : 'border-gray-300'
                        }`}
                        disabled={updating}
                    />
                    {errors.title && (
                        <p className="mt-1 text-sm text-red-600">{errors.title}</p>
                    )}
                    </div>

                    {/* 내용 */}
                    <div className="mb-6">
                    <label
                        htmlFor="content"
                        className="block text-sm font-medium text-gray-700 mb-2"
                    >
                        내용 <span className="text-red-500">*</span>
                    </label>
                    <textarea
                        id="content"
                        name="content"
                        rows={8}
                        value={formData.content}
                        onChange={handleInputChange}
                        placeholder="게시글 내용을 입력하세요 (10-5000자)"
                        className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 resize-vertical ${
                        errors.content ? 'border-red-500' : 'border-gray-300'
                        }`}
                        disabled={updating}
                    />
                    {errors.content && (
                        <p className="mt-1 text-sm text-red-600">{errors.content}</p>
                    )}
                    </div>

                    {/* 작성일과 조회수 (읽기 전용) */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                    <div>
                        <label
                        htmlFor="createdDate"
                        className="block text-sm font-medium text-gray-700 mb-2"
                        >
                        작성일
                        </label>
                        <input
                        type="text"
                        id="createdDate"
                        value={
                            board?.createdDate ? formatDate(board.createdDate) : ''
                        }
                        readOnly
                        className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-600"
                        />
                    </div>
                    <div>
                        <label
                        htmlFor="viewCount"
                        className="block text-sm font-medium text-gray-700 mb-2"
                        >
                        조회수
                        </label>
                        <input
                        type="text"
                        id="viewCount"
                        value={board?.viewCount || 0}
                        readOnly
                        className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 text-gray-600"
                        />
                    </div>
                    </div>

                    {/* 버튼 영역 */}
                    <div className="flex justify-end space-x-3">
                    <Link
                        href="/boards"
                        className="px-6 py-2 text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 transition-colors"
                    >
                        목록으로
                    </Link>
                    <button
                        type="button"
                        onClick={deleteBoard}
                        className="px-6 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors"
                    >
                        삭제
                    </button>
                    <button
                        type="submit"
                        disabled={updating}
                        className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {updating ? '수정 중...' : '변경'}
                    </button>
                    </div>
                </form>
                </div>
            </div>
            </div>
        );
    }
    ```