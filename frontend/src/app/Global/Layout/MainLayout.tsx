'use client'

import {  useState } from "react";
import Side from "../Side";
import DropDown, { Direcion } from "../DropDown";

interface pageInterface {
  children: React.ReactNode,
  classname?: string,
  user: any;
  isClientLoading: boolean; 
}

export default function Main(props: Readonly<pageInterface>) {
  const [open, setOpen] = useState(false);
  const [isSettingOpen, setSettingOpen] = useState(false);
  const user = props.user;
  return (
    <main id='main' className={"flex relative flex-col h-[953px] w-[1920px] " + (props.classname ? props.classname : '')}>
      <div className={"absolute text-8xl font-bold flex flex-col items-center justify-center h-[953px] w-[1920px] bg-white z-[1000]" + (props?.isClientLoading ? '' : ' hidden')}>
        <img src="/logo.png" />
      </div>
      <Side open={open} onClose={() => setOpen(false)} escClose={true} outlineClose={true} className="w-[250px] h-full">
        <div className="flex flex-col">
          <a href="/chat">채팅</a>
          <a href="/email">메일</a>
          <a href="/email/EmailForm" onClick={() => localStorage.removeItem('email')}>메일쓰기</a>
          <a href="/approval">결재</a>
          <a href="/cycle">일정</a>
          {user?.role == 13 || user?.department?.role == 1 ? <a href="/hr">인사관리</a> : <></>}
          {user?.role == 13 || user?.department?.role == 1 ? <a href="/inquire">문의관리</a> : <></>}
        </div>
      </Side>
      <div className="official-color flex items-center h-[50px] min-h-[50px] w-full ">
        {/* 네비바 */}
        <button className="hamburger" onClick={() => setOpen(true)}>
          <span></span>
          <span></span>
          <span></span>
        </button>
        <a href="/main" className="text-white m-3 cursor-pointer">HoneyBadger</a>
        <div className="flex flex-row border-2 border-white rounded-md ml-60">
          <img src="/search.png" className="w-[30px] h-[30px] m-1 cursor-pointer" alt="검색 사진" />
          <input type="text" placeholder="search" className="bolder-0 px-2 outline-none bg-white text-black" />
        </div>
        <div className="m-1 ml-auto flex items-center">
          <img id="setting" src="/setting.png" alt="세팅" className="w-[20px] h-[20px] cursor-pointer" onClick={() => setSettingOpen(!isSettingOpen)} />
          <DropDown open={isSettingOpen} onClose={() => setSettingOpen(false)} className="bg-white flex flex-col" button="setting" defaultDriection={Direcion.DOWN} height={40} width={80} y={-5}>
            <label className="font-bold hover:underline text-red-500 cursor-pointer" onClick={() => { localStorage.clear(); location.href = "/" }}>로그아웃</label>
          </DropDown>
          <img src="/bell.png" alt="알림" className="w-[20px] h-[20px] ml-3" />
          <img src="/MyPage.png" alt="마이페이지" className="w-[20px] h-[20px] mx-3 cursor-pointer" onClick={() => location.href = "/profile"} />

        </div>
      </div>
      <div className="flex bg-gray-200 h-full">
        {props?.children}
      </div>
    </main>
  );
}

// 화이팅!!
// 다들 힘내야 돼 우리는 할 수 있어 !!

