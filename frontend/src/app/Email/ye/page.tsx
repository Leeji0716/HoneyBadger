'use client';
import { getSocket, Subscribe } from "@/app/API/SocketAPI";
import { getEmail, getUser, mailCancel, readEmail } from "@/app/API/UserAPI";
import DropDown, { Direcion } from "@/app/Global/DropDown";
import Main from "@/app/Global/Layout/MainLayout";
import { kMaxLength } from "buffer";
import { useEffect, useState } from "react";
export default function Ye() {

    interface EmailResponseDTO {
        id: number,
        title: string,
        content: string,
        senderId: number,
        senderName: string,
        receiverIds: string[]
    }

    const [email, setEmail] = useState(null as any);
    const [emailList, setEmailList] = useState([] as any);
    const [user, setUser] = useState(null as any);
    const [socket, setSocket] = useState(null as any);
    const [open1, setOpen1] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const maxLength = 30;
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => setUser(r)).catch(e => console.log(e));
        const sub = [] as Subscribe[];
        sub.push({ location: "/api/sub/message/1", active: (r) => { console.log("actived", r) } })
        setSocket(getSocket(sub, () => console.log("test")));

    }, [ACCESS_TOKEN])

    useEffect(() => {
        getEmail().then(r => setEmailList(r)).catch(e => console.log(e))
    }, [])
    const truncateText = ({ text, maxLength }: { text: string, maxLength: number }) => {
        if (text.length <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + '...';
    };

    function MailBox({ email }: { email: EmailResponseDTO }) {
        return <div className="w-11/12 h-[70px] ml-2 mt-4 flex hover:bg-gray-300" onClick={() => { setEmail(email), readEmail({ emailId: email.id, readerId: user.username }).then(r => console.log(r)).catch(e => console.log(e)) }}>
            <div className="h-full w-[6px] official-color mr-2"></div>
            <div className="w-[60px] h-full flex items-center">
                <img className="rounded-full w-[40px] h-[40px]" src="/hui.jpg" alt="후잉~" />
            </div>
            <div className="w-full h-full ml-4 flex flex-col justify-between">
                <div className="flex w-full justify-between h-1/3">
                    <p className="text-sm font-bold">{email?.senderName}</p>
                    <button id={"burger" + email?.id} className="mail-hamburger" onClick={() => { setOpen1(open1?.id == email?.id ? null : email) }}>
                        <span></span>
                        <span></span>
                        <span></span>
                    </button>
                </div>
                <div className="flex flex-row w-full h-1/3">
                    <p className="text-blue-400 font-bold text-base">{truncateText({ text: email?.title, maxLength: maxLength })}</p>
                    <p className="flex ml-auto text-gray-500 text-sm">2024.07.08 09:39</p>
                </div>
                <div className="h-1/3" >
                    <p className="text-sm" dangerouslySetInnerHTML={{ __html: truncateText({ text: email.content, maxLength: maxLength }) }}></p>
                </div>
            </div>
        </div>
    }
    return <Main>
        <div className="w-4/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow p-2">
                <div className="h-[88%] overflow-y-scroll">
                    {emailList?.map((email: EmailResponseDTO, index: number) => <MailBox key={index} email={email} />)}
                    <DropDown open={open1 != null && open1?.id == email?.id} onClose={() => setOpen1(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button={"burger"+open1?.id}>
                        <button onClick={() => console.log(open1?.id)}>수정</button>
                        <button onClick={() => { mailCancel(open1.id)}}>삭제</button>
                    </DropDown>
                </div>

            </div>
        </div>
    </Main>
}