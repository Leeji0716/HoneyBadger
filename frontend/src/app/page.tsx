"use client";
import { useState, useEffect } from "react";
import { Login } from "./API/AuthAPI";
import { getUser } from "./API/UserAPI";
import { redirect, RedirectType } from "next/navigation";

// { params, searchParam }: { params: any, searchParam: any }
export default function Home() {
  const [username, setUsername] = useState<string>();
  const [password, setPassword] = useState<string>();
  const [error, setError] = useState('');
  const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
  const [isClientLoading, setClientLoading] = useState(true);
  useEffect(() => {
    const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
  }, []);
  useEffect(() => {
    if (ACCESS_TOKEN)
      getUser().then(() => location.href = "/main").catch(e => console.log(e));
  }, [ACCESS_TOKEN])

  function Sumbit() {
    if (!username)
      return setError("아이디를 입력해주세요.");
    if (!password)
      return setError("비밀번호를 입력해주세요.");
    Login({ username: username, password: password }).then(r => {
      localStorage.clear();
      localStorage.setItem('tokenType', r.tokenType);
      localStorage.setItem('accessToken', r.accessToken);
      localStorage.setItem('refreshToken', r.refreshToken);
      window.location.href = "/main";
    }).catch(e => {
      if (e.response.status == 404)
        if (e.response.data.message == "entity not found")
          return setError('없는 아이디입니다.');
        else if (e.response.data == "password")
          return setError('잘못된 비밀번호입니다.');
        else if (e.response.data == "disabled")
          return setError('비활성화된 계정, 문의 부탁드립니다.');
        else
          console.log(e)
    })
  }
  return <main className="flex justify-center items-center flex-col h-[953px] w-[1920px]">
    <div className={"absolute text-8xl font-bold flex flex-col items-center justify-center h-[953px] w-[1920px] bg-white z-[1000]" + (isClientLoading ? '' : ' hidden')}>
      <img src="/logo.png" />
    </div>

    {/* 로고 */}
    <img src="/logo.png" className="w-[200px] h-[200px] mb-8" alt="로고" />

    {/* 로그인 */}
    <div className="text-5xl mb-6 font-bold">
      <p>L O G I N</p>
    </div>
    <label className="h-[24px] text-red-500">{error}</label>
    {/* 이메일 */}
    <div className="flex flex-row border-2 border-gray-300 rounded-md w-[400px] h-[40px] mb-2">
      <img src="/mail.png" className="w-[30px] h-[30px] m-1" alt="메일 사진" />
      <input type="text" placeholder="id" className="bolder-0 outline-none bg-white text-black" onChange={e => setUsername(e.target.value)} onKeyDown={e => { if (e.key == "Enter") document.getElementById('login')?.click() }} autoFocus />
    </div>

    {/* 비밀번호 */}
    <div className="flex flex-row border-2 border-gray-300 rounded-md w-[400px] h-[40px] mb-8" >
      <img src="/password.png" className="w-[30px] h-[30px] m-1" alt="비밀번호 사진" />
      <input type="password" placeholder="password" className="bolder-0 outline-none bg-white text-black" onChange={e => setPassword(e.target.value)}

        // 엔터로 로그인 할 수 있게 하는 부분
        onKeyDown={e => {
          if (e.key == "Enter")
            document.getElementById('login')?.click();
        }} />
    </div>

    {/* 버튼 */}
    <button id="login" className="login-button w-[400px] h-[40px] mb-5 font-semibold" type="submit" onClick={() => Sumbit()}
    >L O G I N</button>

    {/* 찾기 */}
    {/* <div className="flex flex-row">
        <a className="mr-5 text-gray-500 text-sm font-semibold" href="">아이디 찾기</a>
        <p className="text-gray-500 text-sm"> | </p>
        <a className="ml-5 mb-5 text-gray-500 text-sm font-semibold" href="">비밀번호 찾기</a>
      </div> */}
    <div className="flex flex-row">
      <a className="mr-5 text-gray-500 text-sm font-semibold" href="/inquire">문의하기</a>
    </div>
  </main>
}
