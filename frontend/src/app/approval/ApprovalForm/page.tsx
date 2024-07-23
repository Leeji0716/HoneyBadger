"use client";
import { getUser } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import { useEffect, useState } from "react";


export default function Approval() {
    interface approvalResponseDTO {
        id?: number,
        title: string,
        content: string,
        status: number,
        sender: string,
        approver: string,
        viewers: string[],
    }

    const [filter, setFilter] = useState(0); //결제 필터 (전체 + status = 총 5개 : 0~4)
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [userList, setUserList] = useState([] as any[])
    const [selectedUsers, setSelectedUsers] = useState(new Set<string>());
    const [isClientLoading, setClientLoading] = useState(true);
    const [keyword, setKeyword] = useState('');

    // 유저 토큰 확인하기
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            location.href = '/';
    }, [ACCESS_TOKEN])

    return <Main user={user} isClientLoading={isClientLoading}>
        {/* 왼쪽 부분 */}
        <div className="w-full flex items-center justify-center h-full pt-10 pb-4">
            <div className="w-11/12 h-full bg-white shadow flex flex-col justify-center items-center gap-2">
                <h1 className="font-bold text-xl">결재 작성</h1>
                <div className="w-11/12 border-2 border-gray-300 h-[90%]">
                    {/* 작성자 정보 */}
                    <div className="w-[20%] h-[300px] border-2 border-red-300 flex justify-center">
                        <div className="w-full h-[50px] flex">
                            <label htmlFor="senderName" className="mr-2 w-[50%]">기안자</label>
                            <p id="senderName w-[50%]">이지영</p>
                        </div>

                    </div>


                    <div className="w-full flex flex-row justify-center">
                        <label htmlFor="title" className="flex items-center mr-2">제목 </label>
                        <input type="text" id="title" className="input border-2 border-gray-300 w-[90%]" placeholder="제목을 입력해주세요." />
                    </div>
                    <div className="w-full flex flex-row justify-center">
                        <label htmlFor="title" className="flex items-center mr-2">제목 </label>
                        <input type="text" id="title" className="input border-2 border-gray-300 w-[90%]" placeholder="제목을 입력해주세요." />
                    </div>
                    <div className="w-full flex flex-row justify-center">
                        <label htmlFor="title" className="flex items-center mr-2">제목 </label>
                        <input type="text" id="title" className="input border-2 border-gray-300 w-[90%]" placeholder="제목을 입력해주세요." />
                    </div>

                    <div className="relative w-11/12 h-[150px] border border-gary-500 overflow-y-scroll border-2 border-gray-300">
                        <button className="btn btn-sm absolute top-[5px] right-[5px]">파일 선택</button>
                        {/* <img src="/plus.png" alt="" className="w-[30px] h-[30px] absolute top-[5px] right-[5px] cursor-pointer" ></img> */}

                    </div>
                    <div className="w-full h-[40%] flex flex-row justify-center">
                        <input type="text" id="content" className="input border-2 border-gray-300 w-[90%] h-full text-center" placeholder="내용을 입력해주세요." />
                    </div>

                </div>

            </div>
        </div>
    </Main >

}