'use client'

import { useState } from "react";
import Side from "../Side";

interface pageInterface {
  children: React.ReactNode,
  classname?: string,
}

export default function Main(props: Readonly<pageInterface>) {
  const [open, setOpen] = useState(false);

  return (
    <main id='main' className={"flex flex-col " + props.classname}>
      <Side open={open} onClose={() => setOpen(false)} escClose={true} outlineClose={true} className="w-[250px] h-screen">
        <div className="flex flex-col">
          <a href="/chat">채팅</a>
          <a href="/email">메일</a>
          <a href="/email/EmailForm" onClick={()=>localStorage.removeItem('email')}>메일쓰기</a>
          <a href="">결재</a>
        </div>
      </Side>
      <div className="official-color flex items-center h-[50px] w-full ">
        {/* 네비바 */}
        <button className="hamburger" onClick={() => setOpen(true)}>
          <span></span>
          <span></span>
          <span></span>
        </button>
        <p className="text-white m-3 cursor-pointer" onClick={()=>location.href="/main"}>HoneyBadger</p>
        <div className="flex flex-row border-2 border-white rounded-md ml-60">
          <img src="/search.png" className="w-[30px] h-[30px] m-1" alt="검색 사진" />
          <input type="text" placeholder="search" className="bolder-0 outline-none bg-white text-black" />
        </div>
        <a href="" className="m-1 ml-auto" >
          <img src="/setting.png" alt="세팅" className="w-[20px] h-[20px]" />
        </a>
        <a href="" className="m-3">
          <img src="/bell.png" alt="알림" className="w-[20px] h-[20px]" />
        </a>
        <a href="" className="m-3">
          <img src="/MyPage.png" alt="마이페이지" className="w-[20px] h-[20px] " />
        </a>
      </div>

      <div className="flex bg-gray-200 h-[screen]">
          {props?.children}
      </div>

    </main>
  );
}

// 화이팅!!
// 다들 힘내야 돼 우리는 할 수 있어 !!
