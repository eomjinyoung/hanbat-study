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
