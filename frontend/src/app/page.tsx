"use client";
import { useState, useEffect } from "react";
import { Login } from "./API/AuthAPI";

// { params, searchParam }: { params: any, searchParam: any }
export default function Home() {
  const [username, setUsername] = useState<string>();
  const [password, setPassword] = useState<string>();

  function Sumbit() {
    if (username && password)
      Login({ username: username, password: password }).then(r => {
        localStorage.clear();
        localStorage.setItem('tokenType', r.tokenType);
        localStorage.setItem('accessToken', r.accessToken);
        localStorage.setItem('refreshToken', r.refreshToken);
        window.location.href = '/main';
      }).catch(e => console.log(e))
  }
  return <main >
    <div className="flex justify-center items-center flex-col h-screen w-screen bg-white">

      {/* 로고 */}
      <img src="/logo.png" className="w-[200px] h-[200px] mb-8" alt="로고" />

      {/* 로그인 */}
      <div className="text-5xl mb-10 font-bold">
        <p>L O G I N</p>
      </div>

      {/* 이메일 */}
      <div className="flex flex-row border-2 border-gray-300 rounded-md w-[400px] h-[40px] mb-2">
        <img src="mail.png" className="w-[30px] h-[30px] m-1" alt="메일 사진" />
        <input type="text" placeholder="email" className="bolder-0 outline-none bg-white text-black" onChange={e => setUsername(e.target.value)} />
      </div>

      {/* 비밀번호 */}
      <div className="flex flex-row border-2 border-gray-300 rounded-md w-[400px] h-[40px] mb-8" >
        <img src="password.png" className="w-[30px] h-[30px] m-1" alt="비밀번호 사진" />
        <input type="password" placeholder="password" className="bolder-0 outline-none bg-white text-black" onChange={e => setPassword(e.target.value)} 
        
        // 엔터로 로그인 할 수 있게 하는 부분
        onKeyDown={e=>{
          if(e.key=="Enter")
              document.getElementById('login')?.click();
        }} />
      </div>

      {/* 버튼 */}
      <button id="login" className="login-button w-[400px] h-[40px] mb-5 font-semibold" type="submit" onClick={() => Sumbit()}
      >L O G I N</button>

      {/* 찾기 */}
      <div className="flex flex-row">
        <a className="mr-5 text-gray-500 text-sm font-semibold" href="">아이디 찾기</a>
        <p className="text-gray-500 text-sm"> | </p>
        <a className="ml-5 mb-5 text-gray-500 text-sm font-semibold" href="">비밀번호 찾기</a>
      </div>
    </div>

  </main>
}
