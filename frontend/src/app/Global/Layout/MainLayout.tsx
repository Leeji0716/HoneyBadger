'use client'

import { useState } from "react";
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
    <main id='main' className={"flex relative flex-col layout-fix " + (props.classname ? props.classname : '')}>
      <div className={"absolute text-8xl font-bold flex flex-col items-center justify-center layout-fix bg-white z-[1000]" + (props?.isClientLoading ? '' : ' hidden')}>
        <img src="/logo.png" />
      </div>
      <Side open={open} onClose={() => setOpen(false)} escClose={true} outlineClose={true} className="w-[13%] h-full">
        <div className="flex flex-col p-4">
          <div className="flex justify-between items-center">
            <img src='/logo_text.png' className="w-[100px] mb-4" />
            <img src="/x.png" className="w-[24px] h-[24px] cursor-pointer" onClick={() => setOpen(false)}></img>
          </div>
          <a href="/main" className="flex mb-4"><img src='/main.png' className="w-[24px] h-[24px] mr-2" />메인</a>
          <a href="/chat" className="flex mb-4"><img src='/chat.png' className="w-[24px] h-[24px] mr-2" />채팅</a>
          <a href="/email" className="flex mb-4"><img src='/mailb.png' className="w-[24px] h-[24px] mr-2" />메일</a>
          <a href="/approval" className="flex mb-4"><img src='/approval.png' className="w-[24px] h-[24px] mr-2" />결재</a>
          <a href="/cycle" className="flex mb-4"><img src='/calander.png' className="w-[24px] h-[24px] mr-2" />일정</a>
          <a href="/storage" className="flex mb-4"><img src='/storage.png' className="w-[24px] h-[24px] mr-2" />저장소</a>
          {user?.role == 13 || user?.department?.role == 1 ? <a href="/hr" className="flex mb-4"><img src='/hr.png' className="w-[24px] h-[24px] mr-2" />인사관리</a> : <></>}
          {user?.role == 13 || user?.department?.role == 1 ? <a href="/inquire" className="flex mb-4"><img src='/inquire.png' className="w-[24px] h-[24px] mr-2" />문의관리</a> : <></>}
        </div>
      </Side>
      <div className="official-color flex items-center h-[50px] w-full ">
        {/* 네비바 */}
        <button className="hamburger" onClick={() => setOpen(true)}>
          <span></span>
          <span></span>
          <span></span>
        </button>
        <a href="/main" className="text-white m-3 cursor-pointer">HoneyBadger</a>
        {/* <div className="flex flex-row border-2 border-white rounded-md ml-60">
          <img src="/search.png" className="w-[30px] h-[30px] m-1 cursor-pointer" alt="검색 사진" />
          <input type="text" placeholder="search" className="bolder-0 px-2 outline-none bg-white text-black" />
        </div> */}
        <div className="m-1 ml-auto flex items-center">
          <div className="relative">
            <img id="setting" src="/setting.png" alt="세팅" className="w-[20px] h-[20px] cursor-pointer" onClick={() => setSettingOpen(!isSettingOpen)} />
            <div className={"absolute w-[100px] h-[40px] top-7 -left-4 bg-white flex flex-col p-2" + (isSettingOpen ? '' : ' hidden')} >
              <label className="font-bold hover:underline text-red-500 cursor-pointer" onClick={() => { localStorage.clear(); location.href = "/" }}>로그아웃</label>
            </div>
          </div>
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
// 정말..?
