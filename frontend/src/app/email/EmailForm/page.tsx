'use client';

import { emailFiles, getUser, mailImage, mailUpdate, reservationEmail, reservationFiles, sendEmail } from "@/app/API/UserAPI";
import DropDown, { Direcion } from "@/app/Global/DropDown";
import Main from "@/app/Global/Layout/MainLayout";
import { eongetDateTimeFormat, eontransferLocalTime, getDateTimeFormatInput, transferLocalTime } from "@/app/Global/Method";
import QuillNoSSRWrapper from "@/app/Global/QuillNoSSRWrapper";
import { Ultra } from "next/font/google";
import { useSearchParams } from "next/navigation";
import { useEffect, useMemo, useRef, useState } from "react";
import ReactQuill from "react-quill";
import 'react-quill/dist/quill.snow.css';


export default function EmailForm() {
    // const searchParams = useSearchParams()
    // console.log(searchParams.get("emailId"));
    const [email, setEmail] = useState(null as any);
    const [A, setA] = useState("");
    const [flag, setFlag] = useState(0);
    const [open, setOpen] = useState(false);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [receiverIds, setReceiverIds] = useState<string[]>([]);
    const [user, setUser] = useState(null as any);
    const [fileList, setFileList] = useState<File[]>([]);
    const [time, setTime] = useState<Date | null>(null);
    const [senderTime, setSenderTime] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [image, setImage] = useState("");
    const [files, setFiles] = useState<MailFile[]>([]);
    const [id, setId] = useState(0);

    interface MailFile {
        key: string,
        original_name: string,
        value: string
    }

    // const [email, setEmail] = useState<Email | null>(null);
    const quillInstance = useRef<ReactQuill>(null);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    console.log("flag : " + flag);
    useEffect(() => { console.log(time) }, [time])
    useEffect(() => {
        if (ACCESS_TOKEN) {
            getUser().then(r => setUser(r)).catch(e => console.log(e));
            if (localStorage.getItem('email')) {
                const email = JSON.parse(localStorage.getItem('email') as string);
                setEmail(email);
                setId(email.id);
                setReceiverIds(email?.receiverIds);
                setTitle(email?.title);
                setContent(email?.content);
                setSenderTime(email?.senderTime);
                setFiles(email?.files);
                setFlag(2);
                console.log("플래그값 : " + flag);
            }
        }
        else
            window.location.href = "/";
    }, [ACCESS_TOKEN])
    const imageHandler = () => {
        const input = document.createElement('input') as HTMLInputElement;
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.addEventListener('change', async () => {
            const file = input.files?.[0];

            try {
                const formData = new FormData();
                formData.append('file', file as any);
                const imgUrl = await mailImage(formData);
                console.log(imgUrl);
                const editor = (quillInstance?.current as any).getEditor();
                const range = editor.getSelection();
                editor.insertEmbed(range.index, 'image', imgUrl);
                editor.setSelection(range.index + 1);
            } catch (error) {
                console.log(error);
            }
        });
    };
    // console.log(content);
    const modules = useMemo(
        () => ({
            toolbar: {
                container: [
                    [{ header: '1' }, { header: '2' }],
                    [{ size: [] }],
                    ['bold', 'italic', 'underline', 'strike', 'blockquote'],
                    [{ list: 'ordered' }, { list: 'bullet' }, { align: [] }],
                    ['image'],
                ],
                handlers: { image: imageHandler },
            },
            clipboard: {
                matchVisual: false,
            },
        }),
        [],
    );

    function ShowReciver(index: number, name: string) {
        return <div key={index}>
            <button className="btn">{name}</button>
        </div>
    }

    function getEmail() {
        return { content: content, title: title, receiverIds: receiverIds, senderId: user.username, sendTime: time, attachments: fileList }
    }

    function test() {
        if (flag == 2) {
            if (fileList.length == 0) {
                mailUpdate({ id: id, content: content, title: title, receiverIds: receiverIds, sendTime: eontransferLocalTime(time), files: files }).catch(e => console.log(e));
            } else {
                const form = new FormData();
                for (const file of fileList)
                    form.append('attachments', file);
                mailUpdate({ id: id, content: content, title: title, receiverIds: receiverIds, sendTime: eontransferLocalTime(time), files: files }).then(r => reservationFiles({ attachments: form, emailId: email.id }).then(r => window.location.href = "/email").catch(e => console.log(e))).catch(e => console.log(e));
            }
        }
        else if (flag == 0) {
            if (fileList.length == 0) {
                sendEmail({ content: content, title: title, receiverIds: receiverIds }).then(r => window.location.href = "/email").catch(e => console.log(e))
            } else {
                const form = new FormData();
                for (const file of fileList)
                    form.append('attachments', file);

                sendEmail({ content: content, title: title, receiverIds: receiverIds }).then(r => emailFiles({ attachments: form, emailId: r })).then(r => window.location.href = "/email").catch(e => console.log(e)).catch(e => console.log(e))
            }
        }
        else {
            if (fileList.length == 0) {
                reservationEmail({ content: content, title: title, receiverIds: receiverIds, sendTime: eontransferLocalTime(time) }).then(r => window.location.href = "/email").catch(e => console.log(e))
            } else {
                const form = new FormData();
                for (const file of fileList)
                    form.append('attachments', file);

                reservationEmail({ content: content, title: title, receiverIds: receiverIds, sendTime: eontransferLocalTime(time) }).then(r => reservationFiles({ attachments: form, emailId: r }).then(r => window.location.href = "/email").catch(e => console.log(e))).catch(e => console.log(e))
            }
        }
    }

    const sliceText = (text: string) => {
        const slice: string[] = text.split(".");
        const extension: string = slice[slice.length - 1];

        return extension;
    }

    return <Main user={user}>
        <div className="flex flex-col items-center gap-5 bg-white w-full p-6">
            <h2 className="font-bold">메일 쓰깅</h2>
            <div className="w-full border-b-2"></div>
            <div className="flex flex-row justify-center gap-3">
                <button className="mail-hover w-[100px]" onClick={test}>보내기</button>
                <button className="mail-hover w-[100px]" onClick={() => setOpen(!open)}>예약</button>
                <DropDown open={open} onClose={() => setOpen(false)} className="mt-8" defaultDriection={Direcion.DOWN} width={200} height={200} button="button1">
                    <input type="datetime-local" name="" id="" defaultValue={getDateTimeFormatInput(senderTime)} onChange={(e) => {
                        const inputDateTimeString = e.target.value; // "YYYY-MM-DDTHH:mm" 형식의 문자열
                        const selectedDate = new Date(inputDateTimeString);
                        setTime(selectedDate);
                        email == null ? setFlag(1) : setFlag(2);
                        console.log("플래그값 : " + flag);
                    }} />
                </DropDown>
                <button className="mail-hover w-[100px]">임시저장</button>
            </div>
            {error ? <p className="font-bold text-red-600">{error}</p> : <></>}
            <div className="flex justify-center gap-5 w-full">
                <label htmlFor="" className="w-[5%]  whitespace-nowrap">받는 사람</label>
                <div className="w-[70%] flex border-solid border-b-2">
                    {receiverIds.length == 0 ? <></> : receiverIds.map((recever, index) => (ShowReciver(index, recever)))}
                    <input type="text" className="w-full whitespace-nowrap" onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                            const receiver: string[] = [...receiverIds];
                            receiver.push(e.currentTarget.value);
                            setReceiverIds(receiver);
                            e.currentTarget.value = "";
                        }
                    }} />
                </div>
            </div>
            <div className="flex justify-center gap-5 w-full">
                <label htmlFor="" className="w-[5%]">제목</label>
                <input type="text" className="border-b-2 w-[70%]" defaultValue={title} onChange={(e) => {
                    setTitle(e.target.value);
                }} />
            </div>
            <div className="flex justify-center w-full gap-5">
                <label htmlFor="" className="w-[5%]">파일첨부</label>
                <input type="file" multiple className="border-b-2 w-[70%]" onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    if (event.target.files) {
                        const filesArray = Array.from(event.target.files);
                        setFileList((prevFiles) => [...prevFiles, ...filesArray]);
                    }
                }} />
            </div>
            <div className="w-[1000px] h-[150px]  overflow-y-scroll">
                {files.length != 0 ? files.map((f: MailFile, index: number) => <ul key={index}>
                    <div className="flex border-solid border-2 border-gray-200 p-4">
                        <button className="mr-2" onClick={() => { const removeFile = [...files]; removeFile.splice(index, 1); setFiles(removeFile); }}>X</button>
                        <img src={"/" + sliceText(f.original_name) + ".PNG"} alt="" />
                        <p>{f.original_name}</p>
                    </div>
                </ul>) : <></>}
                {fileList.length != 0 ? fileList.map((f: File, index: number) => <ul key={index}>
                    <div className="flex border-solid border-2 border-gray-200 p-4">
                        <button className="mr-2" onClick={() => { const removeFile = [...fileList]; removeFile.splice(index, 1); setFileList(removeFile); }}>X</button>
                        <img src={"/" + sliceText(f.name) + ".PNG"} alt="" />
                        <p>{f.name}</p>
                    </div>
                </ul>) : <></>}
            </div>
            <div className="w-full flex justify-center h-[500px]">
                <QuillNoSSRWrapper
                    forwardedRef={quillInstance}
                    value={content}
                    onChange={(e: any) => setContent(e)}
                    modules={modules}
                    theme="snow"
                    className='w-full h-[70%]'
                    placeholder="내용을 입력해주세요."
                />
            </div>
            {/* <div dangerouslySetInnerHTML={{ __html: A }}></div> */}
        </div>
    </Main >

}



// const Test = () => {
//     return (
//         <button className="mail-hover w-[100px]" onClick={() => {

//         }}>보내기</button>
//     );
// }
