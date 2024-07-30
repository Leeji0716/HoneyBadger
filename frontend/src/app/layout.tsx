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
    <html lang="en" className="layout-fix">
      <body className={inter.className + " w-full h-full"}>
        {children}
        <div id="global-modal">
        </div>
      </body>
    </html>
  );
}
