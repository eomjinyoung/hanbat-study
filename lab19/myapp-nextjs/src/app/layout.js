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
