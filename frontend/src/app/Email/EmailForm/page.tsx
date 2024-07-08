'use client';

import { getUser, sendEmail } from "@/app/API/UserAPI";
import DropDown, { Direcion } from "@/app/Global/DropDown";
import Main from "@/app/Global/Layout/MainLayout";
import QuillNoSSRWrapper from "@/app/Global/QuillNoSSRWrapper";
import { useEffect, useMemo, useRef, useState } from "react";
import ReactQuill from "react-quill";
import 'react-quill/dist/quill.snow.css';



export default function EmailForm() {
    const [A, setA] = useState("");
    const [open, setOpen] = useState(false);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [senderId, setSenderId] = useState("");
    const [receiverIds, setReceiverIds] = useState<string[]>([]);
    const [user, setUser] = useState(null as any);
    const [fileList, setFileList] = useState<File[]>([]);
    const [time,setTime] = useState<Date>();
    // const [email, setEmail] = useState<Email | null>(null);
    const quillInstance = useRef<ReactQuill>(null);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');


    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => setUser(r)).catch(e => console.log(e));
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
                const imgUrl = ""; // url
                const editor = (quillInstance?.current as any).getEditor();
                const range = editor.getSelection();
                editor.insertEmbed(range.index, 'image', imgUrl);
                editor.setSelection(range.index + 1);
            } catch (error) {
                console.log(error);
            }
        });
    };

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
                // handlers: { image: imageHandler },
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
    useEffect(() => {
        console.log(time);
    }, [time])

    return <Main>
        <div className="flex flex-col items-start gap-5 bg-white w-full p-6">
            <h2 className="font-bold">메일 쓰깅</h2>
            <div className="w-full border-b-2"></div>
            <div className="flex flex-row gap-3">
                <button className="mail-hover w-[100px]" onClick={() => { sendEmail({ content: content, title: title, receiverIds: receiverIds, senderId: user.username }).then(r => window.location.href = "/email").catch(e => console.log(e)) }}>보내기</button>
                <button className="mail-hover w-[100px]"  onClick={() => setOpen(!open)}>예약</button>
                <DropDown open={open} onClose={() => setOpen(false)} className="" defaultDriection={Direcion.DOWN} width={200} height={200} button="button1">
                    <input type="datetime-local" name="" id="" onChange={(e)=> {
                        const inputDateTimeString = e.target.value; // "YYYY-MM-DDTHH:mm" 형식의 문자열
                        const selectedDate = new Date(inputDateTimeString);
                        setTime(selectedDate);
                    }} />
                </DropDown>
                <button className="mail-hover w-[100px]">임시저장</button>
            </div>
            <div className="flex gap-5 w-full">
                <label htmlFor="" className="w-[5%]">받는 사람</label>
                {receiverIds.length == 0 ? <></> : receiverIds.map((recever, index) => (ShowReciver(index, recever)))}
                <input type="text" className="border-b-2 w-[70%]" onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                        const receiver: string[] = [...receiverIds];
                        receiver.push(e.currentTarget.value);
                        setReceiverIds(receiver);
                        e.currentTarget.value = "";
                    }
                }} />
            </div>
            <div className="flex gap-5 w-full">
                <label htmlFor="" className="w-[5%]">제목</label>
                <input type="text" className="border-b-2 w-[70%]" onChange={(e) => {
                    setTitle(e.target.value);
                }} />
            </div>
            <div className="flex w-full gap-5">
                <label htmlFor="" className="w-[5%]">파일첨부</label>
                <input type="file" multiple className="border-b-2 w-[70%]" onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    if(event.target.files){
                    const filesArray = Array.from(event.target.files);
                    setFileList((prevFiles) => [...prevFiles, ...filesArray]);
                }
                }} />
            </div>
            <div className="w-full h-[500px]">
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
            <input type="datetime-local" name="" id="" />
            {/* <div dangerouslySetInnerHTML={{ __html: A }}></div> */}
        </div>
    </Main>

}