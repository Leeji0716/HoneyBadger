"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import Link from "next/link";

export default function Chat() {
    const [open, setOpen] = useState(false);
    const [filter, setFilter] = useState(false);
    const [chat, setChat] = useState("채팅▾");

    function Filter(filter: boolean) {
        if (filter) {
            setFilter(false);
        } else {
            setFilter(true);
        }
    }

    useEffect(() => {
        if (filter) {
            setChat("채팅▴");
        } else {
            setChat("채팅▾");
        }
    }, [filter])
    // const [color, setColor] = useState<number>(0);
    // function Test(color: number) {
    //     switch (color) {
    //         case 1:
    //             return 'bg-red-500';
    //         case 2:
    //             return 'bg-blue-500';
    //         default:
    //             return 'bg-black';
    //     }
    // }
    // interface a123{
    //     color?:number
    // }
    // function Test2(props: a123) {
    //     if(!props?.color)
    //         return <div>무색</div>

    //     switch (props.color) {
    //         case 1:
    //             return <div>빨강</div>
    //         case 2:
    //             return <div>파랑</div>
    //         default:
    //             return <div>검정</div>
    //     }

    // }
    return <Main>
        {/* <div className={`w-4/12 flex items-center justify-center ${Test(color)}`}> */}
        <div className="w-4/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow">
                <div className="flex justify-start text-xl m-10 text-black">
                    <button className="font-bold" id="button1" onClick={() => { setOpen(!open), setFilter(!filter) }}>{chat}</button>
                    {/* <Test2 /> */}
                    <DropDown open={open} onClose={() => setOpen(false)} className="bg-white" defaultDriection={Direcion.DOWN} width={100} height={100} button="button1">
                        {/* <button onClick={() => setColor(1)}>단체</button>
                        <button onClick={() => setColor(2)}>개인</button> */}
                        <button>개인</button>
                        <button>단체</button>
                    </DropDown>
                </div>
                <div className="flex flex-col">
                    {/* 한 명 버전 */}
                    <div className="flex hover:bg-gray-400 text-white rounded-md">
                        <img src="/pin.png" className="m-2 w-[80px] h-[80px] rounded-full" />
                        <div className="w-full m-2 flex flex-col">
                            <div className="flex justify-between">
                                <p className="text-black font-bold">PAYCO</p>

                            </div>
                            <div className="flex justify-between mt-2">
                                <p className="text-black text-sm">안녕하세요 저는 이렇게 어떻게 하아 이거 맞냐? 어디까지 길어지는 거예요?</p>
                            </div>
                        </div>
                        <div className="w-3/12 h-full flex flex-col justify-end items-end mr-2">
                            <div>
                                <p className="text-gray-300">오후 1:03</p>
                            </div>
                            <div className="bg-red-500 rounded-full w-[20px] h-[20px] flex justify-center items-center mt-2">
                                <p className="text-white text-sm">1</p>
                            </div>
                        </div>
                    </div>

                    {/* 두 명 버전 */}
                    <div className="flex hover:bg-gray-400 text-white rounded-md">
                        <div className="m-2 w-[80px] h-[80px] flex flex-col justify-center items-center ">
                            <div className="w-[80px] h-[40px] flex">
                                <img src="/pigp.png" className="w-[50px] h-[50px] rounded-full" />
                            </div>
                            <div className="w-[80px] h-[40px] flex justify-end">
                                <img src="/pigp.png" className="w-[50px] h-[50px] rounded-full" />
                            </div>
                        </div>
                        <div className="w-full m-2 flex flex-col">
                            <div className="flex justify-between">
                                <p className="text-black font-bold">PAYCO</p>

                            </div>
                            <div className="flex justify-between mt-2">
                                <p className="text-black text-sm">안녕하세요 저는 이렇게 어떻게 하아 이거 맞냐? 어디까지 길어지는 거예요?</p>
                            </div>
                        </div>
                        <div className="w-3/12 h-full flex flex-col justify-end items-end mr-2">
                            <div>
                                <p className="text-gray-300">오후 1:03</p>
                            </div>
                            <div className="bg-red-500 rounded-full w-[20px] h-[20px] flex justify-center items-center mt-2">
                                <p className="text-white text-sm">1</p>
                            </div>
                        </div>
                    </div>

                    {/* 세 명 버전 */}
                    <div className="flex hover:bg-gray-400 text-white rounded-md">
                        <div className="m-2 w-[80px] h-[80px] flex flex-col justify-center items-center ">
                            <div className="w-[80px] h-[40px] flex justify-center">
                                <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                            </div>
                            <div className="w-[80px] h-[40px] flex">
                                <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                                <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                            </div>
                        </div>
                        <div className="w-full m-2 flex flex-col">
                            <div className="flex justify-between">
                                <p className="text-black font-bold">PAYCO</p>

                            </div>
                            <div className="flex justify-between mt-2">
                                <p className="text-black text-sm">안녕하세요 저는 이렇게 어떻게 하아 이거 맞냐? 어디까지 길어지는 거예요? 어디까지 길어지고 언제까지 커지는지 그게 궁금해ㅑ서 말이죠 저는 그게 진짜 궁금해요</p>
                            </div>
                        </div>
                        <div className="w-3/12 h-full flex flex-col justify-end items-end mr-2">
                            <div>
                                <p className="text-gray-300">오후 1:03</p>
                            </div>
                            <div className="bg-red-500 rounded-full w-[20px] h-[20px] flex justify-center items-center mt-2">
                                <p className="text-white text-sm">1</p>
                            </div>
                        </div>
                    </div>

                    {/* 네 명 버전 */}
                    <div className="flex hover:bg-gray-400 text-white rounded-md">
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
                        <div className="w-full m-2 flex flex-col">
                            <div className="flex justify-between">
                                <p className="text-black font-bold">PAYCO</p>

                            </div>
                            <div className="flex justify-between mt-2">
                                <p className="text-black text-sm">안녕하세요 저는 이렇게 어떻게 하아 이거 맞냐? 어디까지 길어지는 거예요? </p>
                            </div>
                        </div>
                        <div className="w-3/12 h-full flex flex-col justify-end items-end mr-2">
                            <div>
                                <p className="text-gray-300">오후 1:03</p>
                            </div>
                            <div className="bg-red-500 rounded-full w-[20px] h-[20px] flex justify-center items-center mt-2">
                                <p className="text-white text-sm">1</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        {/* 오른쪽 부분 */}
        <div className="w-8/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-[95%] flex flex-col shadow">

                {/* 상단바 */}
                <div className="text-black flex">
                    <img src="/pig.png" className="m-2 w-[70px] h-[70px] rounded-full" />
                    <div className="flex flex-col justify-center">
                        <p className="text-black font-bold text-3xl mb-1">PAYCO</p>
                        <div className="flex items-center gap-1">
                            <img src="/people.png" className="w-[15px] h-[15px]" />
                            <p className="w-[20px] h-[20px]">2</p>
                        </div>
                    </div>
                </div>
                {/* 날짜 */}
                <div className="flex justify-center">
                    <div className="inline-flex bg-gray-400  rounded-full text-black font-bold px-4 py-2 text-sm justify-center mt-2 bg-opacity-55">
                        2024년 07월 05일 금요일
                    </div>
                </div>
                {/* 채팅 */}
                <div className="w-full h-[70%] flex flex-col items-start m-1">
                    <div className="flex">

                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                        <div className="flex flex-col ml-2">
                            <p className="text-black">
                                이름
                            </p>
                            <p className="text-black ml-2 justify-items-end">
                                네가 나한테 보내는 메시지
                            </p>
                        </div>
                    </div>

                    <div className="w-full flex flex-col items-end mr-35">
                        <div className="bubble-1 right">
                            내가 보내는 메시지
                        </div>
                    </div>
                </div>
                <div className="flex flex-row border-2 border-gray-300 rounded-md w-[100%] h-[20%] mb-2 items-start">
                    <input type="text" placeholder="내용을 입력하세요" className="bolder-0 outline-none bg-white text-black" />
                </div>
            </div>
        </div>
    </Main>
}

