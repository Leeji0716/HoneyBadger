"use client";
import { getUser } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import Modal from "../Global/Modal";
import { useEffect, useState } from "react";


export default function Approval() {

    interface messageResponseDTO {
        id?: number,
        username: string,
        message: string,
        messageType: number,
        sendTime: number,
        name: string,
        readUsers?: number
    }


    const [open, setOpen] = useState(false);
    const [filter, setFilter] = useState(false);
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [isModalOpen2, setIsModalOpen2] = useState(false);
    const [chatroomName, setChatroomName] = useState('');
    const [userList, setUserList] = useState([] as any[])
    const [selectedUsers, setSelectedUsers] = useState(new Set<string>());
    const [isClientLoading, setClientLoading] = useState(true);
    const [keyword, setKeyword] = useState('');

    function handleOpen2Modal() {
        setIsModalOpen2(true);
    }

    function handleClose2Modal() {
        setIsModalOpen2(false);
    }


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
        <div className="w-4/12 flex flex-col items-center justify-center pt-10 pb-4">
            {/* 검색 인풋 */}
            <div className="flex items-center border-2 border-gray rounded-full w-11/12 h-[50px] mb-5 shadow">
                <img src="/searchg.png" className="w-[30px] h-[30px] m-2" alt="검색 사진" />
                <input
                    type="text"
                    placeholder="결재 제목 검색"
                    className="bolder-0 outline-none bg-white text-black w-[90%]"
                    value={keyword}
                    onChange={e => setKeyword(e.target.value)}
                />
                <button className="text-gray-300 whitespace-nowrap w-[50px] h-[50px] m-2">
                    검색
                </button>
            </div>

            <div className="w-11/12 h-full bg-white shadow">
                <div className="flex justify-start text-xl ml-5 mr-5 mt-5 mb-5 text-black">
                    <button className="font-bold" id="button1" onClick={() => { setOpen(!open), setFilter(!filter) }}>채팅{open ? '▴' : '▾'}</button>
                    <DropDown open={open} onClose={() => setOpen(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button="button1">
                        <button>개인</button>
                        <button>단체</button>
                    </DropDown>
                </div>
                <button onClick={handleOpen2Modal} className="fixed bottom-5 left-10 w-[50px] h-[50px] rounded-full bg-blue-300 text-xl font-bold text-white">
                    +
                </button>
                <Modal open={isModalOpen2} onClose={handleClose2Modal} escClose={true} outlineClose={true}>
                    <div className="overflow-auto w-full">
                        <p className="font-bold text-3xl m-3 mb-8 flex justify-center">채팅방 만들기</p>
                        <div className="flex flex-row border-2 border-gray-300 rounded-md w-[400px] h-[40px] m-2">
                            <input
                                type="text"
                                placeholder="채팅방 이름을 입력해주세요"
                                className="bolder-0 outline-none bg-white text-black"
                                value={chatroomName}
                                onChange={e => setChatroomName(e.target.value)}
                            />
                        </div>
                        <ul className="m-3">
                            {userList.map((user, index) => (
                                <li key={index} className="flex justify-between items-center mb-5">
                                    <span className="w-[50px] h-[50px]"><img src="/pin.png" alt="" /></span>
                                    <span className="font-bold text-md m-3">{user.name}</span>
                                    <span className=" text-md m-3">부서</span>
                                    <span className="text-md m-3">역할</span>
                                    {/* 체크박스 */}
                                    {/* <input
                                        type="checkbox"
                                        checked={selectedUsers.has(user.username)}
                                        onChange={() => handleCheckboxChange(user.username)}
                                    /> */}
                                </li>
                            ))}
                        </ul>
                        <div className="w-full flex justify-center">
                            {/* <button onClick={handleCreateChatroom} className="login-button flex items-center m-2">
                                채팅방 생성
                            </button> */}
                        </div>
                    </div>
                </Modal>


                <div className="flex flex-col items-center">
                    <div className="flex justify-items-center flex-row border-2 border-gray rounded-full w-[90%] h-[50px] mb-5">
                        <img src="/searchg.png" className="w-[30px] h-[30px] m-2" alt="검색 사진" />
                        <input
                            type="text"
                            placeholder="대화방, 참여자 검색"
                            className="bolder-0 outline-none bg-white text-black w-[80%]"
                            value={keyword}
                            onChange={e => setKeyword(e.target.value)}
                        />
                        {/* <button className="text-gray-300 whitespace-nowrap"
                            onClick={handleSearch} >
                            검색
                        </button> */}
                    </div>
                    <div className="justify-start w-full">
                        <p className="font-bold ml-3 text-gray-300">
                            내 프로필
                        </p>
                        <div className="flex hover:bg-gray-400 text-white rounded-md">
                            <img src="/pin.png" className="m-2 w-[80px] h-[80px] rounded-full" />
                            <div className="w-full m-2 flex flex-col">
                                <div className="flex justify-between">
                                    <p className="text-black font-bold">{user?.name}</p>

                                </div>
                                <div className="flex flex-col mt-2">
                                    <p className="text-black">{user?.username}</p>
                                    <p className="text-black text-sm">:)</p>
                                </div>
                            </div>
                            <div className="w-3/12 h-full flex flex-col justify-end items-end mr-4">
                            </div>
                        </div>
                        <p className="font-bold ml-3 text-gray-300 mt-3">
                            대화 목록
                        </p>
                    </div>
                    <div className="w-full justify-end h-[550px] overflow-x-hidden overflow-y-scroll">
                        {/* {chatrooms?.map((chatroom: chatroomResponseDTO, index: number) => <ChatList key={index} Chatroom={chatroom} ChatDetail={chatDetail} innerRef={chatBoxRef} />)} */}
                    </div>
                </div>
            </div>
        </div>

        {/* 오른쪽 부분 */}
        <div className="w-8/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-11/12 w-11/12 bg-white h-full flex flex-col shadow">
                {/* {chatroom != null ? <ChatDetail Chatroom={chatroom} messageList={messageList} innerRef={chatBoxRef} currentScrollLocation={currentScrollLocation} /> : <></>} */}
            </div>
        </div>
    </Main>

}