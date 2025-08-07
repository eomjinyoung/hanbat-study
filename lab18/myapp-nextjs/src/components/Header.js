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
            <li>
              <a href="/" className="hover:text-blue-200 transition-colors">
                홈
              </a>
            </li>
            <li>
              <a
                href="/boards"
                className="hover:text-blue-200 transition-colors"
              >
                게시판
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  );
}
