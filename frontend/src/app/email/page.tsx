"use client";
import { useEffect, useRef, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import { getEmail, getUser, mailCancel, mailDelete, readEmail } from "../API/UserAPI";
import { useRouter } from "next/navigation";
import { getDateEmailTime } from "../Global/Method";

export default function Email() {

    interface Receiver {
        receiverUsername: string,
        status: boolean
    }

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
        status: boolean,
        receiverStatus: Receiver[],
        totalPage: number
    }
    const [open, setOpen] = useState(false);
    const [open1, setOpen1] = useState(false);
    const [user, setUser] = useState(null as any);
    const [emailList, setEmailList] = useState([] as any[]);
    const [acceptEmailList, setAcceptEmailList] = useState([] as any[]);
    const [email, setEmail] = useState(null as any);
    const [open3, setOpen3] = useState(null as any);
    const [discernment, setDiscernment] = useState(false);
    const [status, setStatus] = useState(0);
    const [sort, setSort] = useState(1);
    const [page, setPage] = useState(0);
    const [maxPage, setMaxPage] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const mailBoxRef = useRef<HTMLDivElement>(null);


    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const maxLength = 30;
    const router = useRouter();

    const [isClientLoading, setClientLoading] = useState(true);

    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                getEmail(1, page).then(r => {
                    setEmailList(r.content);
                    setSort(1);
                    setMaxPage(r.totalPages);
                    setPage(page + 1);
                    const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
                }).catch(e => { setClientLoading(false); console.log(e); })
            }).catch(e => console.log(e));
    }, [ACCESS_TOKEN])

    const loadPage = () => {
        const mailBox = mailBoxRef.current;

        if (mailBox != null) {
            const scrollLocation = mailBox?.scrollTop;
            const maxScroll = mailBox.scrollHeight - mailBox.clientHeight;
            if (!isLoading && scrollLocation >= maxScroll && page < maxPage - 1) {
                setIsLoading(true);
                getEmail(sort, page)
                    .then(response => {
                        if (response.size > 0) {
                            const pageEmail = [...emailList, ...response.content];
                            setEmailList(pageEmail);
                            setMaxPage(response.totalPages);
                            setPage(page + 1);
                        }
                        setIsLoading(false);
                    })
                    .catch(error => {
                        console.log(error);
                        setIsLoading(false);
                    });
            }
        }
    };

    const truncateText = ({ text, maxLength }: { text: string, maxLength: number }) => {
        const trimText = text?.replace(/<p><br><\/p>/g, '').trim();
        if (trimText?.length <= maxLength) {
            return trimText;
        }
        return trimText?.substring(0, maxLength) + '...';
    };


    const sliceText = (text: string) => {
        const slice: string[] = text.split(".");
        const extension: string = slice[slice.length - 1];
        const a = [];
        return extension;
    }

    const storageItems = (email: EmailResponseDTO, index: number) => {
        const items = {
            email: email,
            index: index
        }
        return items;
    }

    console.log(email);
    function MailBox({ email }: { email: EmailResponseDTO }) {

        return <div className="w-11/12 h-[70px] ml-2 mt-4 flex hover:bg-gray-300" onClick={() => {
            if (email.status == false && sort == 1) {
                readEmail({ emailId: email.id, readerId: user.username }).then(r => { setEmail(r); const index = emailList.findIndex(e => e.id === email.id); const pre = [...emailList]; pre[index] = r; setEmailList(pre); }).catch(e => console.log(e))
            }
            else {
                setEmail(email);
            }
        }}>
            {sort == 1 && email.status == false ? <div className="h-full w-[6px] official-color mr-2"></div> : <></>}
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
                    <p className="flex ml-auto text-gray-500 text-sm">{getDateEmailTime(email.senderTime)}</p>
                </div>
                <div className="h-[20px] overflow-hidden" >
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
                    {sort != 0 ?
                        email.receiverIds?.map((r: string, index: number) => <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm" key={index}>{r}</button>)
                        :
                        email.receiverStatus?.map((r: Receiver, index: number) => r.status == false ? <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm" key={index}>{r.receiverUsername}</button> : <button className="inline-flex bg-red-200 rounded-full text-white font-bold px-4 py-2 text-sm" key={index}>{r.receiverUsername}</button>)
                    }
                </div>
                <div className="flex justify-start items-center mb-2 gap-5">
                    <p className="text-sm">보낸사람</p>
                    <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm">
                        {sort == 2 ? user.username : email.senderName}
                    </button>
                </div>
                <p className="text-sm">{getDateEmailTime(email.senderTime)}</p>
            </div>
            <div className="w-full h-[60%] border-t-2 flex flex-col justify-center mt-2 p-4">
                <ul>
                    {email.files.length != 0 ?
                        email.files.map((f: MailFile, index: number) => <li key={index}>
                            <div className="flex mb-4 border-solid border-2 border-gray-200 p-4 gap-6">
                                <img src={"/" + sliceText(f.original_name) + ".PNG"} className="w-[26px] h-[31px]" alt="" />
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

    return <Main user={user} isClientLoading={isClientLoading}>
        <div className="w-4/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-2">
                <div className="w-full h-30 flex flex-row">
                    <div className="flex mr-20 items-center gap-2">
                        {sort == 1 ? <button className="official-color text-white" >받은 메일</button> : <button onClick={() => { open1 == true ? setOpen1(!open1) : ""; setEmail(null); setPage(0); setStatus(1); getEmail(1, 0).then(r => { setSort(1); setEmailList(r.content); setMaxPage(r.totalPages) }).catch(e => console.log(e)); }}>받은 메일</button>}
                        <img src="/plus.png" id="button1" className="w-[20px] h-[20px]" onClick={() => { open1 == false ? "" : setOpen1(!open1); setOpen(!open); }} alt="" />
                        <DropDown open={open} onClose={() => setOpen(false)} className="bg-white overflow-y-scroll" defaultDriection={Direcion.DOWN} width={200} height={100} button="button1">
                            <button className="bg-white">중요</button>
                            <button className="bg-white">태그</button>
                            <button className="bg-white">태그</button>
                        </DropDown>
                    </div>
                    <div className="flex mr-20 items-center gap-2">
                        {sort == 0 ? <button className="official-color text-white" >보낸 메일</button> : <button className="" onClick={() => { open == true ? setOpen(!open) : ""; setEmail(null); setPage(1); setStatus(0); getEmail(0, 0).then(r => { setSort(0), setEmailList(r.content); setMaxPage(r.totalPages) }).catch(e => console.log(e)); }}>보낸 메일</button>}
                        <img src="/plus.png" id="button2" className="w-[20px] h-[20px]" onClick={() => { open == false ? "" : setOpen(!open); setOpen1(!open1); }} alt="" />
                        <DropDown open={open1} onClose={() => setOpen1(false)} className="bg-white overflow-y-scroll" defaultDriection={Direcion.DOWN} width={200} height={100} button="button2">
                            <button>중요</button>
                            <button>태그</button>
                            <button>태그</button>
                        </DropDown>
                    </div>
                    {sort == 2 ? <button className="official-color text-white" >예약 메일</button> : <button className="" onClick={() => {setEmail(null); setPage(1); open == false ? "" : setOpen(!open); open1 == false ? "" : setOpen1(!open1); setStatus(2); getEmail(2, 0).then(r => { setSort(2); setEmailList(r.content); setMaxPage(r.totalPages) }).catch(e => console.log(e)) }}>예약 메일</button>}
                    <a href="/email/EmailForm" className="ml-auto mr-2" onClick={() => localStorage.removeItem('email')}>메일쓰기</a>
                </div>
                <div ref={mailBoxRef} onScroll={loadPage} id="mailBox" className="h-[800px] overflow-y-scroll">
                    {emailList?.map((email: EmailResponseDTO, index: number) => <MailBox key={index} email={email} />)}
                    <DropDown open={open3 != null && open3?.id == email?.id} onClose={() => setOpen1(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button={"burger" + open3?.id}>
                        {status != 2 ?
                            <>
                                <button onClick={() => { router.push(`/email/EmailForm`); localStorage.setItem("email", JSON.stringify(storageItems(email, 0))) }}>전달</button>
                                <button onClick={() => { router.push(`/email/EmailForm`); localStorage.setItem("email", JSON.stringify(storageItems(email, 1))) }}>답장</button>
                                <button onClick={() => { mailDelete(open3.id).then(r => window.location.href = "/email").catch(e => console.log(e)) }}>삭제</button>
                            </>
                            :
                            <>
                                <button onClick={() => { router.push(`/email/EmailForm`); localStorage.setItem("email", JSON.stringify(storageItems(email, 2))) }}>수정</button>
                                <button onClick={() => { mailCancel(open3.id).then(r => window.location.href = "/email").catch(e => console.log(e)) }}>삭제</button>
                            </>

                        }
                    </DropDown>
                </div>
            </div>
        </div>
        <div className="w-8/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-4">
                {email != null ? <MailDetail /> : <></>}
            </div>
        </div>
    </Main>
}