export default function Footer() {
  return (
    <footer className="bg-gray-800 text-white py-8 mt-auto">
      <div className="container mx-auto px-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div>
            <h3 className="text-lg font-semibold mb-3">
              한밭대학교 웹개발 실무
            </h3>
            <p className="text-gray-300 text-sm">
              Next.js와 React를 활용한
              <br />웹 애플리케이션 개발 실습 프로젝트
            </p>
          </div>
          <div>
            <h4 className="font-semibold mb-3">빠른 링크</h4>
            <ul className="space-y-2 text-sm">
              <li>
                <a
                  href="#"
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  실습 가이드
                </a>
              </li>
              <li>
                <a
                  href="#"
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  소스 코드
                </a>
              </li>
              <li>
                <a
                  href="#"
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  과제 제출
                </a>
              </li>
              <li>
                <a
                  href="#"
                  className="text-gray-300 hover:text-white transition-colors"
                >
                  Q&A
                </a>
              </li>
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
