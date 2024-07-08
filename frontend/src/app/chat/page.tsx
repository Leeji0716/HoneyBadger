"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import ReactTooltip, { Tooltip } from 'react-tooltip';
import Link from "next/link";
import { getChat, getUser } from "../API/UserAPI";
import { getDateTimeFormat } from "../Global/Method";

export default function Chat() {
    interface messageRespnseDTO {
        username: string,
        message: string,
        type: number,
        date: number
    }
    interface chatroomResponseDTO {
        id: number,
        name: string,
        users: string[]
    }

    const [open, setOpen] = useState(false);
    const [open1, setOpen1] = useState(false);
    const [filter, setFilter] = useState(false);
    const [drop, setDrop] = useState(false);
    // const [chat, setChat] = useState("채팅▾");
    const [chatrooms, setChatrooms] = useState([] as any);
    const [chatroom, setChatroom] = useState(null as any);
    const [user, setUser] = useState(null as any);
    const [test, setTest] = useState<messageRespnseDTO[]>([]);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => setUser(r)).catch(e => console.log(e));
        const test = [] as messageRespnseDTO[];
        test.push({ username: "admin", message: "test", type: 0, date: 0 });
        test.push({ username: "admin2", message: "test3", type: 0, date: 0 });
        test.push({ username: "admin", message: "tes4t", type: 0, date: 0 });
        test.push({ username: "admin", message: "test5", type: 0, date: 0 });
        setTest(test);
    }, [ACCESS_TOKEN])


    useEffect(() => {
        getChat().then(r => {
            console.log(r);
            setChatrooms(r);
        }).catch(e => console.log(e))
    }, [])

    function ChatList(chatroom: { chatroom: chatroomResponseDTO }) {
        const joinMembers: number = chatroom.chatroom.users.length;
        function getValue(confirm: number) {
            switch (joinMembers) {
                case 1: return <img src="/pin.png" className="m-2 w-[80px] h-[80px] rounded-full" />;
                case 2: return <div className="m-2 w-[80px] h-[80px] flex flex-col justify-center items-center ">
                    <div className="w-[80px] h-[40px] flex">
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full ml-2 mt-2" />
                    </div>
                    <div className="w-[80px] h-[40px] flex justify-end">
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full mr-2 mb-2" />
                    </div>
                </div>
                case 3: return <div className="m-2 w-[80px] h-[80px] flex flex-col justify-center items-center ">
                    <div className="w-[80px] h-[40px] flex justify-center">
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                    </div>
                    <div className="w-[80px] h-[40px] flex">
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                    </div>
                </div>
                default:
                    <div className="m-2 w-[80px] h-[80px] flex flex-col justify-center items-center ">
                        <div className="w-[80px] h-[40px] flex">
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                        </div>
                        <div className="w-[80px] h-[40px] flex">
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                        </div>
                    </div>
            }
        }
        return <div className="flex hover:bg-gray-400 text-white rounded-md cursor-pointer" onClick={() => { console.log(chatroom?.chatroom?.id) }}>
            {getValue(joinMembers)}

            <div className="w-full m-2 flex flex-col">
                <div className="flex justify-between">
                    <p className="text-black font-bold">{chatroom?.chatroom.name}</p>
                </div>
                <div className="flex justify-between mt-2">
                    <p className="text-black text-sm">안녕하세요 저는 이렇게 어떻게 하아 이거 맞냐? 어디까지 길어지는 거예요?</p>
                </div>
            </div>
            <div className="w-3/12 h-full flex flex-col justify-end items-end mr-4">
                <div>
                    <p className="text-gray-300 whitespace-nowrap">오후 1:03</p>
                </div>
                <div className="bg-red-500 rounded-full w-[20px] h-[20px] flex justify-center items-center mt-2">
                    <p className="text-white text-sm">1</p>
                </div>
            </div>
        </div>
    }

    return <Main>
        <div className="w-4/12 flex items-center justify-center h-screen">
            {/* 왼 쪽 부분 */}
            <div className=" h-11/12 w-11/12 mt-10 bg-white h-[95%] shadow">
                <div className="flex justify-start text-xl ml-5 mr-5 mt-5 mb-5 text-black">
                    <button className="font-bold" id="button1" onClick={() => { setOpen(!open), setFilter(!filter) }}>채팅{open ? '▴' : '▾'}</button>
                    <DropDown open={open} onClose={() => setOpen(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button="button1">
                        <button>개인</button>
                        <button>단체</button>
                    </DropDown>
                </div>
                <button className="fixed bottom-5 left-10 w-[50px] h-[50px] rounded-full bg-blue-300 text-xl font-bold text-white">
                    +
                </button>


                <div className="flex flex-col items-center">
                    <div className="flex justify-items-center flex-row border-2 border-gray rounded-full w-[90%] h-[50px] mb-5">
                        <img src="/searchg.png" className="w-[30px] h-[30px] m-2" alt="검색 사진" />
                        <input type="text" placeholder="대화방, 참여자 검색" className="bolder-0 outline-none bg-white text-black w-[80%] " />
                        <button className="text-gray-300 whitespace-nowrap">
                            검색
                        </button>
                    </div>
                    <div className="justify-start w-full">
                        <p className="font-bold ml-3 text-gray-300">
                            내 프로필
                        </p>
                        <div className="flex hover:bg-gray-400 text-white rounded-md">
                            <img src="/pin.png" className="m-2 w-[80px] h-[80px] rounded-full" />
                            <div className="w-full m-2 flex flex-col">
                                <div className="flex justify-between">
                                    <p className="text-black font-bold">PAYCO</p>

                                </div>
                                <div className="flex flex-col mt-2">
                                    <p className="text-black">paycio@gmail.com</p>
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
                    <div className="w-full justify-end h-[590px] overflow-x-hidden overflow-y-scroll">
                        {chatrooms?.map((chatroom: chatroomResponseDTO, index: number) => <ChatList key={index} chatroom={chatroom} />)}
                    </div>
                </div>
            </div>
        </div>

        {/* 오른쪽 부분 */}
        <div className="w-8/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-[95%] flex flex-col shadow">

                {/* 상단바 */}
                <div className="flex w-full justify-between border-b-2">
                    <div className="text-black flex w-[50%]">
                        <img src="/pig.png" className="m-2 w-[70px] h-[70px] rounded-full" />
                        <div className="flex flex-col justify-center">
                            <p className="text-black font-bold text-3xl mb-1">PAYCO</p>
                            <div className="flex items-center gap-1">
                                <button>
                                    <img src="/people.png" className="w-[30px] h-[30px]" />
                                </button>
                                <p className="flex items-end text-xl w-[30px] h-[30px] text-official-color">2</p>
                            </div>
                        </div>
                    </div>
                    <div className="mr-5 w-[50%] flex justify-end items-center">
                        <button className="hamburger1" id="burger" onClick={() => { setOpen1(!open1), setDrop(!drop) }}>
                            <span></span>
                            <span></span>
                            <span></span>
                        </button>
                        <DropDown open={open1} onClose={() => setOpen1(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button="burger">
                            {/* <button onClick={() => setColor(1)}>단체</button>
                        <button onClick={() => setColor(2)}>개인</button> */}
                            <button>나가기</button>
                            <button>사진/동영상</button>
                            <button>파일</button>
                            <button></button>
                        </DropDown>
                    </div>
                </div>

                <div className="h-[700px] w-[100%] overflow-x-hidden overflow-y-scroll">
                    {/* 날짜 */}
                    <div className="flex justify-center">
                        <div className="inline-flex bg-gray-400 rounded-full text-white font-bold px-4 py-2 text-sm justify-center mt-2 bg-opacity-55">
                            2024년 07월 05일 금요일
                        </div>
                    </div>
                    {/* 채팅 */}
                    {test?.map((t, index) => <div key={index} className="w-full flex flex-col items-start m-1">
                        {
                            t.username == user?.username ?
                                <div className="flex w-full justify-end">
                                    <div className="w-6/12 flex justify-end mr-2">
                                        <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getDateTimeFormat(t?.date)}</p>
                                        <div className="inline-flex rounded-2xl text-sm text-white justify-center m-2 official-color">
                                            <div className="mt-2 mb-2 ml-3 mr-3">
                                                {t.type == 0 ? t?.message : ''}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                :
                                <div className="flex w-6/12 ml-2">
                                    <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                                    <div className="flex flex-col ml-2">
                                        <p className="text-black font-bold ml-2">
                                            {t.username}
                                        </p>
                                        <div className="w-full flex">
                                            <p className="text-black ml-2">
                                                {t?.message}
                                            </p>
                                            <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getDateTimeFormat(t?.date)}</p>
                                        </div>
                                    </div>
                                </div>


                        }

                    </div>)

                    }



                    <div className="w-full h-[70%] flex flex-col items-start m-1">
                        <div className="flex w-6/12 ml-2">
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                            <div className="flex flex-col ml-2">
                                <p className="text-black font-bold ml-2">
                                    PAYCO
                                </p>
                                <div className="w-full flex">
                                    <p className="text-black ml-2">
                                        네가 나한테 보내는 메시지 왜 이러지? 진짜 이해가 안 가네 어쩌라는 걸까
                                    </p>
                                    <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">오전 9:32</p>
                                </div>
                            </div>
                        </div>

                        <div className="flex w-full justify-end">
                            <div className="w-6/12 flex justify-end mr-2">
                                <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">오전 9:32</p>
                                <div className="inline-flex rounded-2xl text-sm text-white justify-center m-2 official-color">
                                    <div className="mt-2 mb-2 ml-3 mr-3">
                                        내가 보내는 메시지는 얼마나 길어질지 한번 얼마나 길고 넓어지고 그렇게 되는지 한 번 확인을 해보겠습니다
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="flex flex-col border-2 border-gray-300 rounded-md w-[100%] h-[20%] items-start">
                    <div className="h-[80%] m-2 w-[98%]">
                        <textarea placeholder="내용을 입력하세요" className="bolder-0 outline-none bg-white text-black w-full h-full" />
                    </div>
                    <div className="flex w-[98%] justify-between font-bold text-gray-500">
                        <div className="flex">

                            <button id="emoticon">
                                <img src="/emoticon.png" className="w-[25px] h-[25px] items-center justify-center m-1" />
                            </button>
                            <Tooltip anchorSelect="#emoticon" clickable>
                                <button>이모티콘</button>
                            </Tooltip>

                            <button id="book">
                                <img src="/book.png" className="w-[25px] h-[25px] items-center justify-center m-1" />
                            </button>
                            <Tooltip anchorSelect="#book" clickable>
                                <button>예약 전송</button>
                            </Tooltip>

                            <button id="file">
                                <img src="/file.png" data-tip="파일" className="file w-[25px] h-[25px] items-center justify-center m-1" />
                            </button>
                            <Tooltip anchorSelect="#file" clickable>
                                <button>파일 전송</button>
                            </Tooltip>

                        </div>
                        <button>
                            <img src="/send.png" className="send w-[40px] h-[40px] items-center justify-center m-1" />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </Main>
}

