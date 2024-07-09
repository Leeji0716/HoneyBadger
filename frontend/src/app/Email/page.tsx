"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import { getEmail, getUser } from "../API/UserAPI";
import { getSocket, Subscribe } from "../API/SocketAPI";



export default function Email() {

    interface EmailResponseDTO {
        id: number,
        title: string,
        content: string,
        senderId: number,
        senderName: string,
        receiverIds: string[]
    }
        
    const [open, setOpen] = useState(false);
    const [open1, setOpen1] = useState(false);
    const [user, setUser] = useState(null as any);
    const [socket, setSocket] = useState(null as any);
    const [emailList, setEmailList] = useState([] as any[]);
    const [email, setEmail] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => setUser(r)).catch(e => console.log(e));

        const sub = [] as Subscribe[];
        sub.push({ location: "/api/sub/message/1", active: (r) => { console.log("actived", r) } })
        setSocket(getSocket(sub, () => console.log("test")));

    }, [ACCESS_TOKEN])
    useEffect(() => {
        getEmail().then(r => {
            console.log("---------------.");
            console.log(r);
            setEmailList(r)
        }).catch(e => console.log(e))

    }, [])
    //console.log(user);
    function MailBox({ email }: { email: EmailResponseDTO }) {
        return <div className="w-11/12 h-[70px] ml-2 mt-4 flex hover:bg-gray-300" onClick={() => setEmail(email)}>
            <div className="h-full w-[6px] official-color mr-2"></div>
            <div className="w-[60px] h-full flex items-center">
                <img className="rounded-full w-[40px] h-[40px]" src="/hui.jpg" alt="후잉~" />
            </div>
            <div className="w-full h-full ml-4 flex flex-col justify-between">
                <div className="flex w-full justify-between h-1/3">
                    <p className="text-sm font-bold">{email?.senderName}</p>
                    <p className="">파일</p>
                </div>
                <div className="flex flex-row w-full h-1/3">
                    <p className="text-blue-400 font-bold text-base">{email?.title}</p>
                    <p className="flex ml-auto text-gray-500 text-sm">2024.07.08 09:39</p>
                </div>
                <div className="h-1/3" >
                    <p className="text-sm"  dangerouslySetInnerHTML={{ __html: email.content }}></p>
                </div>
            </div>
        </div>
    }

    function MailDetail() {

        return <div>
            <div className="w-full h-[18%] border-t-2 pt-2">
                <h2 className="text-2xl font-semibold">{email?.title}</h2>
                <div className="flex justify-start items-center gap-5 mt-2 mb-2 pt-2">
                    <p className="text-sm">{email?.senderName}</p>
                    <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm">
                        gkstjddjs08@naver.com
                    </button>
                </div>
                <div className="flex justify-start items-center mb-2 gap-5">
                    <p className="text-sm">{user?.nickname}</p>
                    <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm">
                        {user?.name}
                    </button>
                </div>
                <p className="text-sm">2024.07.05 오후 2:21</p>
            </div>
            <div className="w-full h-[60%] border-t-2 flex justify-center mt-2 p-4">
                <p className="w-[80%] font-bold" dangerouslySetInnerHTML={{ __html: email.content }}>
              
                </p>
            </div>
        </div>
    }

    return <Main>
        <button className="btn btn-xl" onClick={() => { socket.publish({ destination: "/api/pub/message/1", body: JSON.stringify({ username: user?.username, message: "test" }) }); }}>test</button>
        <div className="w-4/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow p-2">
                <div className="w-full h-30 flex flex-row gap-20 ">
                    <button id="button1" onClick={() => setOpen(!open)}>받은 메일</button>
                    <DropDown open={open} onClose={() => setOpen(false)} className="bg-white overflow-y-scroll" defaultDriection={Direcion.DOWN} width={200} height={100} button="button1">
                        <button className="bg-white">중요</button>
                        <button className="bg-white">태그</button>
                        <button className="bg-white">태그</button>
                    </DropDown>
                    <button id="button2" className="" onClick={() => setOpen1(!open1)}>받은 메일</button>
                    <DropDown open={open1} onClose={() => setOpen1(false)} className="bg-white overflow-y-scroll" defaultDriection={Direcion.DOWN} width={200} height={100} button="button2">
                        <button>중요</button>
                        <button>태그</button>
                        <button>태그</button>
                    </DropDown>
                </div>
                <div className="h-[88%] overflow-y-scroll">
                    {emailList?.map((email: EmailResponseDTO, index: number) => <MailBox key={index} email={email} />)}
                </div>

            </div>
        </div>
        <div className="w-8/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow p-4">
                {email != null ? <MailDetail /> : <></>}
            </div>
        </div>
        <div>
        </div>
    </Main>
}

