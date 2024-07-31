import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "HoneyBadger",
  description: "벌꿀오소리가 되는 그날까지!",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="layout-font h-screen w-screen flex items-center justify-center">
      {/* <html lang="en" className="layout-font w-full h-full"> */}
      <body className={inter.className + " layout-fix"}>
        {children}
        <div id="global-modal">
        </div>
      </body>
    </html>
  );
}
