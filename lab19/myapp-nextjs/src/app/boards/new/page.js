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
        `${process.env.NEXT_PUBLIC_REST_API_URL}/boards`,
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
