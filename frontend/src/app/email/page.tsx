"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import { getEmail, getUser, mailCancel, mailDelete, readEmail } from "../API/UserAPI";
import { useRouter } from "next/navigation";
import { getDateTime } from "../Global/Method";




export default function Email() {

    interface MailFile {
        key: string,
        original_name: string,
        value: string
    }

    interface EmailResponseDTO {
        id: number,
        title: string,
        content: string,
        senderId: string, // 나중에 변경예정
        senderName: string,
        senderTime: number,
        receiverIds: string[],
        files: MailFile[],
        status: boolean
    }

    const [open, setOpen] = useState(false);
    const [open1, setOpen1] = useState(false);
    const [user, setUser] = useState(null as any);
    const [emailList, setEmailList] = useState([] as any[]);
    const [acceptEmailList, setAcceptEmailList] = useState([] as any[]);
    const [email, setEmail] = useState(null as any);
    const [open3, setOpen3] = useState(null as any);
    const [discernment, setDiscernment] = useState(false);
    const [status,setStatus] = useState(0);
    const [sort, setSort] = useState(0);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const maxLength = 30;
    const router = useRouter();

    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                getEmail(1).then(r => {
                    console.log("---------------.");
                    console.log(r);
                    setEmailList(r);
                    setSort(1);
                }).catch(e => console.log(e))
            }).catch(e => console.log(e));
    }, [ACCESS_TOKEN])


    const truncateText = ({ text, maxLength }: { text: string, maxLength: number }) => {
        if (text?.length <= maxLength) {
            return text;
        }
        return text?.substring(0, maxLength) + '...';
    };
    
    useEffect(() => {
        console.log(emailList);
    }, [emailList])

    const sliceText = (text:string) => {
        const slice:string[] = text.split(".");
        const extension:string = slice[slice.length - 1];
        
        return extension;
    }

    function MailBox({ email }: { email: EmailResponseDTO }) {
        return <div className="w-11/12 h-[70px] ml-2 mt-4 flex hover:bg-gray-300" onClick={() => { setEmail(email); if(email.status == false){readEmail({ emailId: email.id, readerId: user.username }).then(r => getEmail(sort).then(r => setEmailList(r)).catch(e => console.log(e))).catch(e => console.log(e))}}}>
            {email.status == true ? <div className="h-full w-[6px] official-color mr-2"></div> : <></>}
            <div className="w-[60px] h-full flex items-center">
                <img className="rounded-full w-[40px] h-[40px]" src="/hui.jpg" alt="후잉~" />
            </div>
            <div className="w-full h-full ml-4 flex flex-col justify-between">
                <div className="flex w-full justify-between h-1/3">
                    <p className="text-sm font-bold">{email?.senderName == user?.username ? email?.receiverIds[0] : email?.senderName}</p>
                    <button id={"burger" + email?.id} className="mail-hamburger pl-6" onClick={() => { setOpen3(open3?.id == email?.id ? null : email) }}>
                        <span></span>
                        <span></span>
                        <span></span>
                    </button>
                </div>
                <div className="flex flex-row w-full h-1/3">
                    <p className="text-blue-400 font-bold text-base">{truncateText({ text: email?.title, maxLength: maxLength })}</p>
                    <p className="flex ml-auto text-gray-500 text-sm">{getDateTime(email.senderTime)}</p>
                </div>
                <div className="h-1/3" >
                    <p className="text-sm" dangerouslySetInnerHTML={{ __html: truncateText({ text: email.content, maxLength: maxLength }) }}></p>
                </div>
            </div>
        </div>
    }

    function MailDetail() {
        return <div>
            <div className="w-full h-[18%] border-t-2 pt-2">
                <h2 className="text-2xl font-semibold">{email?.title}</h2>
                <div className="flex justify-start items-center gap-5 mt-2 mb-2 pt-2">
                    <p className="text-sm">받는사람</p>
                    <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm">
                        {email?.senderName == user?.username ? email?.receiverIds[0] : email?.senderName}
                    </button>
                </div>
                <div className="flex justify-start items-center mb-2 gap-5">
                    <p className="text-sm">보낸사람</p>
                    <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm">
                        {user?.username}
                    </button>
                </div>
                <p className="text-sm">{getDateTime(email.senderTime)}</p>
            </div>
            <div className="w-full h-[60%] border-t-2 flex flex-col justify-center mt-2 p-4">
                <ul>
                    {email.files.length != 0 ?
                        email.files.map((f: MailFile, index: number) => <li key={index}>
                            <div className="flex mb-4 border-solid border-2 border-gray-200 p-4 gap-6">
                                <img src={"/" + sliceText(f.original_name)+".PNG"} alt="" />
                                <a href={f.value}>{f.original_name}</a>
                            </div>
                        </li>)
                        :
                        <></>
                    }
                </ul>
                <p className="w-[80%] font-bold" dangerouslySetInnerHTML={{ __html: email.content }}>

                </p>
            </div>
        </div>
    }

    return <Main user={user}>
        <div className="w-4/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow p-2">
                <div className="w-full h-30 flex flex-row gap-20 ">
                    <button id="button1" onClick={() => { open1 == false ? "" : setOpen1(!open1); setStatus(1); getEmail(1).then(r =>{setSort(1); setEmailList(r)}).catch(e => console.log(e)); setOpen(!open) }}>받은 메일</button>
                    <DropDown open={open} onClose={() => setOpen(false)} className="bg-white overflow-y-scroll" defaultDriection={Direcion.DOWN} width={200} height={100} button="button1">
                        <button className="bg-white">중요</button>
                        <button className="bg-white">태그</button>
                        <button className="bg-white">태그</button>
                    </DropDown>
                    <button id="button2" className="" onClick={() => { open == false ? "" : setOpen(!open); setStatus(0); getEmail(0).then(r => {setSort(0), setEmailList(r)}).catch(e => console.log(e)); setOpen1(!open1) }}>보낸 메일</button>
                    <DropDown open={open1} onClose={() => setOpen1(false)} className="bg-white overflow-y-scroll" defaultDriection={Direcion.DOWN} width={200} height={100} button="button2">
                        <button>중요</button>
                        <button>태그</button>
                        <button>태그</button>
                    </DropDown>
                    <button className="" onClick={() => { open == false ? "" : setOpen(!open); open1 == false ? "" : setOpen1(!open1); setStatus(2); getEmail(2).then(r =>{setSort(2); setEmailList(r)}).catch(e => console.log(e)) }}>예약 메일</button>
                </div>
                <div className="h-[88%] overflow-y-scroll">
                    {emailList?.map((email: EmailResponseDTO, index: number) => <MailBox key={index} email={email} />)}
                    <DropDown open={open3 != null && open3?.id == email?.id} onClose={() => setOpen1(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button={"burger" + open3?.id}>
                        {status != 2?
                            <>
                                <button onClick={() => { mailDelete(open3.id) }}>삭제</button>
                            </>
                            :
                            <>
                                <button onClick={() => { router.push(`/email/EmailForm`); localStorage.setItem("email", JSON.stringify(email)) }}>수정</button>
                                <button onClick={() => { mailCancel(open3.id) }}>삭제</button>
                            </>

                        }
                    </DropDown>
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

