/* eslint-disable react-hooks/exhaustive-deps */
"use client";
import { useCallback, useEffect, useRef, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import { cancelUpload, createFileFolder, deleteFile, downloadFiles, getFileFolders, getStorageFile, getStorageFiles, getUser } from "../API/UserAPI";
import { getRole, getStorageDate } from "../Global/Method";
import { getSocket, Subscribe } from "../API/SocketAPI";
import { useRouter } from "next/navigation";
import { NavigateOptions } from "next/dist/shared/lib/app-router-context.shared-runtime";




declare module "react" {
    interface InputHTMLAttributes<T> extends HTMLAttributes<T> {
        webkitdirectory?: string;
    }
}
interface Upload {
    key: string,
    name: string,
    bytes: Uint8Array,
    index: number,
    totalIndex: number,
    status: number,
    type: number,
    base: string,
    location: string;
    url: string,
    uploadType: number, // 0 덮어쓰기 1 새로만들기
    baseLocation: string
}
interface Confirm {
    upload: Upload,
    file: any
}

export default function Home() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [isClientLoading, setClientLoading] = useState(true);
    const [orderOpen, setOrderOpen] = useState(false);
    const [order, setOrder] = useState(0);
    const [type, setType] = useState(-1);
    const [typeOpen, setTypeOpen] = useState(false);
    const [descOpen, setDescOpen] = useState(false);
    const [desc, setDesc] = useState(false);
    const [style, setStyle] = useState(true);
    const [extra, setExtra] = useState(false);
    const [selects, setSelect] = useState([] as any[]);
    const [base, setBase] = useState('개인');
    const [baseLocation, setBaseLocation] = useState('');
    const [baseFolders, setBaseFolders] = useState({} as any);
    const [location, setLocation] = useState('');
    const [used, setUsed] = useState(0);
    const [page, setPage] = useState(0);
    const [maxPage, setMaxPage] = useState(0);
    const [files, setFiles] = useState([] as any[]);
    const max = 10737418240;
    const [folds, setFolds] = useState([] as string[]);
    const [reload, setReload] = useState(false);
    const [keyword, setKeyword] = useState("");
    const [isOpenUpload, setOpenUpload] = useState(false);
    const [socket, setSocket] = useState(null as any);
    const [uploads, setUploads] = useState<Upload[]>([]);
    const [uploadConfirm, setUploadConfirm] = useState<Confirm[]>([]);

    const [sendFold, setSendFold] = useState(false);
    const router = useRouter();
    const uploadsRef = useRef(uploads);
    const confirmRef = useRef(uploadConfirm);
    const isClickedFirst = useRef<Boolean>(false);
    const chunkStack = 1024;
    const chunkSize = 1024 * chunkStack;
    const size = chunkSize / chunkStack;
    const scrollRef = useRef<HTMLDivElement>(null);
    const handleBeforeUnload = useCallback(
        (e: BeforeUnloadEvent) => {
            if (isUploading()) {
                e.preventDefault();
                e.returnValue = true;
            }
        },
        [uploads],
    );

    const handlePopState = useCallback(() => {
        if (isUploading()) {
            history.pushState(null, "", "");
            console.log("test");
            // onGoBack && onGoBack();  // ✨
        } else {
            history.go(-1);
        }
    }, [uploads]);
    const handleUnload = useCallback(() => {
        if (user) {
            socket.unsubscribe('/api/sub/uploadFile/' + user?.unsermae);
        }
    }, [socket, user])
    useEffect(() => {
        const interval = setInterval(() => { scrollRef?.current?.scrollTo(0, scrollRef?.current?.scrollHeight); clearInterval(interval) }, 10);

    }, [uploadsRef])

    useEffect(() => {
        if (reload) {
            renew(location, page, type, keyword);
            setReload(false);
        }
    }, [reload]);
    useEffect(() => {
        function getOrder() {
            if (order == 0)
                return 0;
            else
                return order * 2 - (desc ? 0 : 1);
        }
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                if (!socket && !user) {
                    const subscribe = [] as Subscribe[];
                    subscribe.push({
                        location: "/api/sub/uploadFile/" + r.username, active: res => {
                            const receive = res.body;
                            const uploads = uploadsRef.current
                            const index = uploads.findIndex(f => f.key == receive.key);
                            if (index >= 0) {
                                const upload = uploads[index];
                                if (res.statusCodeValue == 200) {
                                    if (receive.index <= upload.index)
                                        return;
                                    upload.index = receive.index;
                                    if (receive.name)
                                        upload.name = receive.name;
                                    if (upload.status == 1) {
                                        if (upload.index < upload.totalIndex) {
                                            // 진행중
                                            // const binary  = String.fromCharCode(...upload.bytes.slice(chunkSize * upload.index, chunkSize * (upload.index + 1)));
                                            const start = upload.index * chunkSize;
                                            let binary = "";
                                            for (let i = 0; i < chunkStack; i++)
                                                binary += String.fromCharCode(...upload.bytes.slice(start + i * size, start + (i + 1) * size));
                                            const chunk = window.btoa(binary); // base64
                                            socket.publish({
                                                destination: "/api/pub/uploadFile/" + r?.username, body: JSON.stringify({
                                                    key: upload.key,
                                                    index: upload.index,
                                                    totalIndex: upload.totalIndex,
                                                    chunk: chunk,
                                                    location: upload.url,
                                                    name: upload.name,
                                                    uploadType: upload.uploadType,
                                                    baseLocation: upload.baseLocation
                                                })
                                            });

                                        } else {
                                            // 업로드 완료
                                            upload.index = upload.totalIndex;
                                            upload.status = 0;
                                            // const interval = setInterval(() => { setReload(true); clearInterval(interval) }, 100);
                                            setReload(true);

                                        }
                                    }
                                } else {// 오류로 인한 취소
                                    upload.status = -1;
                                    cancelUpload({ Location: upload.url, Name: upload.name, Key: upload.key });
                                    if (res.body.name == "storage")
                                        alert("최대 용량을 초과했습니다.")
                                }

                                uploadsRef.current = [...uploads];
                                setUploads(uploadsRef.current);
                            }
                        }
                    })
                    const socket = getSocket(subscribe, () => { const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000); });
                    setSocket(socket);
                }
                setLocation('/api/user/' + r?.username + '/storage');
                setBaseLocation('/api/user/' + r?.username + '/storage');
                getStorageFiles({ Location: "/api/user/" + r?.username + "/storage", Order: getOrder(), Keyword: keyword }).then(r => { setMaxPage(r.totalPages); setFiles(r.content); }).catch(e => console.log(e));
                getStorageFile({ Location: '/api/user/' + r?.username + "/storage" }).then(r => setUsed(r.size)).catch(e => console.log(e));
                getFileFolders({ Location: '/api/user/' + r?.username + "/storage" }).then(r => { baseFolders["개인"] = r; setBaseFolders({ ...baseFolders }) }).catch(e => console.log(e));
                if (r?.department) {
                    const key = r.department.name;
                    getFileFolders({ Location: '/api/department/' + r?.department?.name + '/storage' }).then(r => { baseFolders[key] = r; setBaseFolders({ ...baseFolders }) }).catch(e => console.log(e));
                    if (r?.role) {
                        const role = getRole(r?.role);
                        getFileFolders({ Location: '/api/department/' + r?.department?.name + "/role/" + getRole(r?.role) + '/storage' }).then(r => { baseFolders[`${key} ${role}`] = r; setBaseFolders({ ...baseFolders }) }).catch(e => console.log(e));
                    }
                }
                if (r?.role) {
                    const role = getRole(r?.role);
                    getFileFolders({ Location: '/api/role/' + getRole(r?.role) + '/storage' }).then(r => { baseFolders[`${role}`] = r; setBaseFolders({ ...baseFolders }) }).catch(e => console.log(e));
                }
                // setFolds(['개인']);

            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            window.location.href = '/';
    }, [ACCESS_TOKEN])

    function isUploading() {
        return uploads.filter(f => f.status == 1).length > 0;
    }
    useEffect(() => {
        if (!isClickedFirst) {
            history.pushState(null, "", "");
            (isClickedFirst as any).current = true;
        }
    }, []);

    useEffect(() => {
        const originalPush = router.push;
        const newPush = (
            href: string,
            options?: NavigateOptions | undefined,
        ): void => {
            if (isUploading()) {
                originalPush(href, options);
                console.log("test2");
                return;
            }
        }
        router.push = newPush;

        return (() => {
            router.push = originalPush;
        })
    }, [router, uploads]);

    useEffect(() => {
        window.addEventListener("beforeunload", handleBeforeUnload);
        window.addEventListener("popstate", handlePopState);
        window.addEventListener('unload', handleUnload)
        return (() => {
            window.removeEventListener("beforeunload", handleBeforeUnload);
            window.removeEventListener("popstate", handlePopState);
            window.removeEventListener('unload', handleUnload);
        });
    }, [handleBeforeUnload, handlePopState, handleUnload]);

    function getOrder() {
        if (order == 0)
            return 0;
        else
            return order * 2 - (desc ? 0 : 1);
    }
    function FileTypeImage(file: any) {
        switch (file.type) {
            case 0: return "/folder.png";
            case 1: return file?.url;
            case 2: return "/video.png";
            case 3: return "/audio.png";
            case 4: return "/compress.png";
            case 5: return "/document.png";
            default: return "/etc.png";
        }
    }
    function FileTypeName(file: any) {
        switch (file.type) {
            case 0: return "폴더";
            case 1: return "이미지";
            case 2: return "비디오";
            case 3: return "오디오";
            case 4: return "압축";
            case 5: return "문서";
            default: return "기타";
        }
    }
    function UploadImage(type: number) {
        switch (type) {
            case 0: return "/folder.png";
            case 1: return "/png.png";
            case 2: return "/video.png";
            case 3: return "/audio.png";
            case 4: return "/compress.png";
            case 5: return "/document.png";
            default: return "/etc.png";
        }
    }
    function CalcSize(bytes: number) {
        if (bytes == 0)
            return "0 KB";
        else
            if (bytes < 1024)
                return "1 KB";

        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return (Math.floor((bytes / Math.pow(k, i)) * 10) / 10).toLocaleString("ko-kr", { maximumFractionDigits: 1 }) + ' ' + sizes[i];

    }

    function Order() {
        switch (order) {
            case 0:
                return "종류";
            case 1:
                return "이름";
            case 2:
                return "크기";
            case 3:
                return "수정한날짜";
            case 4:
                return "올린날짜";
        }
    }
    function Desc() {
        switch (order) {
            case 1:
                return desc ? 'ㅎ-ㄱ' : "ㄱ-ㅎ";
            case 2:
                return desc ? '큰순' : '작은순';
            case 3:
            case 4:
                return desc ? "최신순" : '오래된순';
        }
    }
    function DescButton() {
        let ascName = "";
        let descName = "";
        switch (order) {
            case 1:
                descName = 'ㅎ-ㄱ';
                ascName = "ㄱ-ㅎ";
                break;
            case 2:
                descName = '큰순';
                ascName = '작은순';
                break;
            case 3:
            case 4:
                descName = "최신순";
                ascName = '오래된순';
                break;
        }
        return <div className={"absolute bg-white top-5 right-0 border-2 w-[120px] h-[84px] flex flex-col text-left" + (descOpen ? '' : ' hidden')}>
            <label className={"cursor-pointer hover:underline p-2 flex items-center h-[40px]" + (!desc ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setDesc(false); setDescOpen(false); setReload(true); }}>{ascName} {!desc ? <img alt="down" width={20} height={20} src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
            <label className={"cursor-pointer hover:underline p-2 flex items-center h-[40px]" + (desc ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setDesc(true); setDescOpen(false); setReload(true); }}>{descName} {desc ? <img alt="down" width={20} height={20} src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
        </div>
    }
    function renew(location: string, page: number, type: number, keyword: string) {
        setSelect([]);
        setLocation(location);
        setPage(page);
        setType(type);
        setKeyword(keyword);
        getStorageFiles({ Location: location, Page: page, Type: type, Order: getOrder(), Keyword: keyword }).then(r => { setMaxPage(r.totalPages); setFiles(r.content); }).catch(e => console.log(e));
        getStorageFile({ Location: baseLocation }).then(r => { setUsed(r.size); }).catch(e => console.log(e));
    }
    function FileType(name: string) {
        if (name.includes(".")) {
            const type = name.split(".")[1];
            switch (type.toUpperCase()) {
                case "PNG": case "JPEG": case "JPG": case "TIFF": case "GIF": case "BMP": return 1;
                case "MP4": case "WMV": case "AVI": case "MKV": case "MPEG-2": case "MOV": return 2;
                case "WAV": case "MP3": case "OGG": case "WMA": case "AAC": return 3;
                case "ZIP": case "7Z": case "APK": case "RAR": case "TAR": case "DEB": case "RPM": case "JAR": case "EAR": case "WAR": case "COMPRESS": case "GZIP": case "BZIP2": case "XZ": return 4;
                case "TXT": case "HWP": case "PDF": case "XLS": case "XLSX": case "PPT": case "PPTX": case "DOC": case "DOCX": return 5;
                default: return 6;
            };
        }
    }
    function Folder(props: { folder: any, stack?: number, url: string }) {

        const folder = props.folder;
        let stack = props?.stack ? props.stack : 0;
        return <>
            <div className={"flex items-center" + (location == props.url ? ' text-[#8fbee9]' : '')} style={{ paddingLeft: (stack * 7) }}>
                {folder.child.length > 0 ?
                    <img alt="right" width={12} height={12} src={folds.includes(props.url) ? "/right.png" : "/down.png"} className="w-[12px] h-[12px] cursor-pointer mr-1" onClick={() => {
                        if (folds.includes(props.url))
                            setFolds([...folds.filter(f => f != props.url)])
                        else
                            setFolds([...folds, props.url])
                    }} />
                    : <p className="w-[12px] h-[12px] mr-1" />
                }
                <label className="cursor-pointer hover:underline" onClick={() => renew(props.url, 0, -1, "")}>{folder?.name}</label>
            </div>
            {folds.includes(props.url) ? (folder?.child as any[])?.map((f, index) => <Folder key={index} folder={f} stack={stack + 1} url={props.url + "/" + f?.name} />) : <></>}

        </>
    }

    function FolderTree(props: { name: string, location: string, folderList: any[] }) {
        return <div className="flex flex-col mt-2">
            <div className="flex items-center">
                {props.folderList?.length > 0 ?
                    <img alt="up/down" width={18} height={18} src={folds.includes(props.name) ? '/right.png' : '/down.png'} className="w-[18px] h-[18px] mr-1 cursor-pointer" onClick={() => {
                        if (folds.includes(props.name))
                            setFolds([...folds.filter(f => f != props.name)]);
                        else
                            setFolds([...folds, props.name]);
                    }} />
                    : <p className="w-[18px] h-[18px] mr-1" />
                }
                <label className={"cursor-pointer hover:underline font-bold flex text-lg items-center" + (base == props?.name ? ' text-[#8fbee9]' : '')} onClick={e => {
                    if (base == props.name && (e.target as HTMLElement).tagName == "IMG")
                        return;
                    setBase(props.name);
                    setBaseLocation(props.location);
                    setLocation(props.location);
                    setType(-1);
                    setPage(0);
                    setKeyword("");
                    setSelect([]);
                    getStorageFiles({ Location: props.location, Order: getOrder(), Keyword: "" }).then(r => { setMaxPage(r.totalPages); setFiles(r.content); }).catch(e => console.log(e));
                    getStorageFile({ Location: props.location }).then(r => setUsed(r.size)).catch(e => console.log(e));
                    getFileFolders({ Location: props.location }).then(r => {
                        baseFolders[props.name] = r;
                        setBaseFolders({ ...baseFolders });
                    }).catch(e => console.log(e));
                    if (base != props.name) {
                        setFolds([props.name]);
                    }
                }}>
                    {props.name}폴더
                </label>
            </div>
            {!folds.includes(props.name) ?
                <></> :
                <div className="flex flex-col pl-6">
                    {props.folderList?.map((f, index) => <Folder folder={f} key={index} url={props.location + "/" + f.name} />)}
                </div>
            }
        </div>
    }

    return <Main user={user} isClientLoading={isClientLoading} classname="HD">
        <div className="w-2/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-2 flex flex-col relative ml-12">
                <div className="overflow-y-auto flex flex-col">
                    <FolderTree name="개인" location={'/api/user/' + user?.username + '/storage'} folderList={baseFolders["개인"]} />
                    {user?.department ?
                        <>
                            <FolderTree name={user?.department?.name} location={'/api/department/' + user?.department?.name + '/storage'} folderList={baseFolders[user?.department?.name]} />
                            {user?.role && getRole(user?.role) ?
                                <FolderTree name={`${user?.department?.name} ${getRole(user?.role)}`} location={'/api/department/' + user?.department?.name + "/role/" + getRole(user?.role) + '/storage'} folderList={baseFolders[`${user?.department?.name} ${getRole(user?.role)}`]} />
                                : <></>}
                        </>
                        :
                        <></>
                    }
                    {user?.role && getRole(user?.role) ?
                        <FolderTree name={`전체 ${getRole(user?.role)}`} location={'/api/role/' + getRole(user?.role) + '/storage'} folderList={baseFolders[`${getRole(user?.role)}`]} />
                        : <></>}

                    {/* <label className="cursor-pointer hover:underline pl-[18px] text-lg mt-2 font-bold">사진</label>
                    <label className="cursor-pointer hover:underline pl-[18px] text-lg mt-2 font-bold">동영상</label>
                    <label className="cursor-pointer hover:underline pl-[18px] text-lg mt-2 font-bold">음악</label> */}
                </div>
                <div className="h-[150px] absolute bottom-0 left-0 w-full mt-auto border flex flex-col items-center p-8">
                    <div className="flex justify-between items-end w-full">
                        <label><label className="text-[#8fbee9]">{CalcSize(used)}</label> / {CalcSize(max)}</label>
                        <label className="text-xs text-gray-500">여유 {CalcSize(max - used)}</label>
                    </div>
                    <input type="range" className={"range range-xs mt-2" + (used / max >= 0.95 ? ' range-error' : used / max >= 0.8 ? ' range-warning' : ' range-info')} defaultValue={used * 1000 / max} min={0} max={1000} disabled />
                    {/* <label className="flex mt-2 self-end items-center"><img alt="trash" width={24}
                        height={24} src="/trash_can.png" className="w-[24px] h-[24px]" />휴지통</label> */}
                </div>
            </div>
        </div>
        <div className="w-10/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-4 relative">
                <div className="flex justify-between">
                    <div className="text-lg font-bold flex items-center">
                        <label className="cursor-pointer hover:underline" onClick={() => renew(baseLocation, 0, -1, "")}>{base + '폴더'}</label>
                        {location == baseLocation ? <></> : location.replaceAll(baseLocation, "").split("/").splice(1).map((str, index) => {
                            if (str == '')
                                return;
                            return <div key={index} className="flex items-center">
                                <img alt="right" width={20} height={20} src="/right.png" className="w-[20px] h-[20px]" />
                                <label className="flex items-center cursor-pointer hover:underline" onClick={() => renew(baseLocation + location.replaceAll(baseLocation, "").split("/").slice(0, index + 2).join("/"), 0, -1, "")}>{str}</label>
                            </div>
                        })}
                    </div>

                    <div className="border-2 border-gray-500 rounded-lg flex p-2 w-[360px]">
                        <input id="file_input" placeholder="검색어.." type="text" className="outline-none text-xs w-full" defaultValue={keyword} onKeyDown={(e) => { if (e.key == "Enter") document.getElementById('file_search')?.click() }} onChange={e => {
                            const x = document.getElementById('file_x');
                            if (e.target.value != "")
                                x?.classList?.remove("hidden");
                            else
                                x?.classList?.add('hidden');
                        }} />
                        <img alt="file_x" width={16} height={16} id="file_x" src="/x.png" className="w-[16px] h-[16px] cursor-pointer hover:border-2 hover:border-gray-400 rounded-lg mr-2 hidden" onClick={e => {
                            (document.getElementById('file_input') as HTMLInputElement).value = "";
                            (e.target as HTMLElement).classList.add('hidden');
                        }} />
                        <img alt="file_search" width={16} height={16} id="file_search" src="/searchb.png" className="w-[16px] h-[16px] cursor-pointer" onClick={() => {
                            const value = (document.getElementById('file_input') as HTMLInputElement).value;
                            renew(baseLocation, 0, -1, value);
                        }} />
                    </div>
                </div>
                <div className="flex justify-between">
                    <div className="flex items-center">
                        <input id="all" type="checkbox" className={"mr-2 my-auto checkbox checked:border-0 [--chkbg:#8fbee9] [--chkfg:white] self-start group-hover:border-gray-300 checked:hover:border-0 hover:border-2 " + (selects.length > 0 && selects.length < files.length ? ' hidden' : '')} checked={selects.length != 0 && selects.length == files.length} onChange={() => { }} onClick={e => {
                            if ((e.target as HTMLInputElement).checked)
                                setSelect([...files]);
                            else
                                setSelect([]);
                        }} />
                        <img alt="-" width={24} height={24} src="/-.png" className={"w-[24px] h-[24px] mr-2 cursor-pointer" + (selects.length == 0 || selects.length == files.length ? ' hidden' : '')} onClick={() => { setSelect([]); (document.getElementById('all') as HTMLInputElement).checked = false; }} />
                        <div className="relative">
                            <button className="btn btn-info btn-sm text-white mr-1" onClick={() => { setOpenUpload(!isOpenUpload) }}>올리기</button>
                            <div className={"absolute border-2 p-2 flex flex-col left-4 top-8 bg-white w-[100px]" + (isOpenUpload ? '' : ' hidden')}>
                                <button className="text-sm" onClick={() => document.getElementById('file_upload')?.click()}>파일 올리기</button>
                                <input id="file_upload" multiple type="file" hidden onChange={e => {
                                    if (!socket.connected) {
                                        window.location.reload();
                                        alert('오류 발생으로 페이지를 새로고칩니다.');
                                        return;
                                    }
                                    function create(file: any, confirm?: Confirm) {
                                        const reader = new FileReader();
                                        const key = new Date().getTime().toString();
                                        const upload = { key: key, name: file.name, status: 1, type: FileType(file.name), base: base, baseLocation: baseLocation, location: location.replaceAll(baseLocation, "") + "/", url: location, uploadType: 0 } as Upload;
                                        // uploadsRef.current = [...uploads, upload];
                                        if (!uploadsRef.current.includes(upload))
                                            uploadsRef.current.push(upload);

                                        setUploads(uploadsRef.current);
                                        reader.onload = async function (event) {
                                            let buffer = event.target?.result;
                                            const bytes = new Uint8Array(buffer as ArrayBuffer);
                                            const len = bytes.byteLength;

                                            const totalIndex = Math.ceil(len / chunkSize);

                                            // const upload = { key: key, index: 0, totalIndex: totalIndex, bytes: bytes, name: file.name, status: 1, type: FileType(file.name), base: base, location: location.replaceAll(baseLocation, "") + "/", url: location } as Upload;
                                            upload.index = 0;
                                            upload.totalIndex = totalIndex;
                                            upload.bytes = bytes;
                                            // uploadsRef.current = [...uploads, upload];
                                            if (!uploadsRef.current.includes(upload))
                                                uploadsRef.current.push(upload);
                                            setUploads(uploadsRef.current);
                                            if (confirm) {
                                                confirm.upload = upload;
                                                confirmRef.current = [...confirmRef.current];
                                                setUploadConfirm(confirmRef.current);
                                            }
                                            else {
                                                const start = upload.index * chunkSize;
                                                let binary = "";
                                                for (let i = 0; i < chunkStack; i++)
                                                    binary += String.fromCharCode(...upload.bytes.slice(start + i * size, start + (i + 1) * size));
                                                const chunk = window.btoa(binary); // base64
                                                socket.publish({
                                                    destination: "/api/pub/uploadFile/" + user?.username, body: JSON.stringify({
                                                        key: upload.key,
                                                        index: upload.index,
                                                        totalIndex: upload.totalIndex,
                                                        chunk: chunk,
                                                        location: upload.url,
                                                        name: upload.name,
                                                        uploadType: upload.uploadType,
                                                        baseLocation: upload.baseLocation
                                                    })
                                                });
                                            }
                                        };
                                        reader.readAsArrayBuffer(file);
                                    }
                                    const files = e.target.files;

                                    if (files && files?.length >= 1) {
                                        let total_size = 0;
                                        for (let i = 0; i < files.length; i++)
                                            total_size += files[i]?.size;
                                        if (total_size + used >= max) {
                                            alert("최대 용량을 초과했습니다.")
                                            return;
                                        }

                                        for (let i = 0; i < files.length; i++) {
                                            const file = files[i];
                                            if (file) {
                                                if (file.size > 1024 * 1024 * 1024 * 4) {
                                                    alert('4GB 이상 파일은 업로드 불가능합니다. - ' + file.name)
                                                    return;
                                                }
                                                getStorageFile({ Location: location + "/" + file.name }).then(r => {
                                                    const confirm = { file: r } as Confirm;
                                                    create(file, confirm);
                                                    confirmRef.current = [...confirmRef.current, confirm]
                                                    setUploadConfirm(confirmRef.current)
                                                }).catch(e => {
                                                    if (e?.response?.status == 403 && e?.response.data == 'file not found')
                                                        create(file);
                                                    else {
                                                        console.log(e);
                                                        return;
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    e.target.value = '';
                                }} />
                                <button className="text-sm" onClick={() => document.getElementById('folder_upload')?.click()}>폴더 올리기</button>
                                <input id="folder_upload" type="file" hidden webkitdirectory="" onChange={e=>{
                                    if (!socket.connected) {
                                        window.location.reload();
                                        alert('오류 발생으로 페이지를 새로고칩니다.');
                                        return;
                                    }
                                    function create(file: any, confirm?: Confirm) {
                                        const reader = new FileReader();
                                        const key = new Date().getTime().toString();
                                        const upload = { key: key, name: file.name, status: 1, type: FileType(file.name), base: base, baseLocation: baseLocation, location: location.replaceAll(baseLocation, "") + "/", url: location, uploadType: 0 } as Upload;
                                        // uploadsRef.current = [...uploads, upload];
                                        if (!uploadsRef.current.includes(upload))
                                            uploadsRef.current.push(upload);

                                        setUploads(uploadsRef.current);
                                        reader.onload = async function (event) {
                                            let buffer = event.target?.result;
                                            const bytes = new Uint8Array(buffer as ArrayBuffer);
                                            const len = bytes.byteLength;

                                            const totalIndex = Math.ceil(len / chunkSize);

                                            // const upload = { key: key, index: 0, totalIndex: totalIndex, bytes: bytes, name: file.name, status: 1, type: FileType(file.name), base: base, location: location.replaceAll(baseLocation, "") + "/", url: location } as Upload;
                                            upload.index = 0;
                                            upload.totalIndex = totalIndex;
                                            upload.bytes = bytes;
                                            // uploadsRef.current = [...uploads, upload];
                                            if (!uploadsRef.current.includes(upload))
                                                uploadsRef.current.push(upload);
                                            setUploads(uploadsRef.current);
                                            if (confirm) {
                                                confirm.upload = upload;
                                                confirmRef.current = [...confirmRef.current];
                                                setUploadConfirm(confirmRef.current);
                                            }
                                            else {
                                                const start = upload.index * chunkSize;
                                                let binary = "";
                                                for (let i = 0; i < chunkStack; i++)
                                                    binary += String.fromCharCode(...upload.bytes.slice(start + i * size, start + (i + 1) * size));
                                                const chunk = window.btoa(binary); // base64
                                                socket.publish({
                                                    destination: "/api/pub/uploadFile/" + user?.username, body: JSON.stringify({
                                                        key: upload.key,
                                                        index: upload.index,
                                                        totalIndex: upload.totalIndex,
                                                        chunk: chunk,
                                                        location: upload.url,
                                                        name: upload.name,
                                                        uploadType: upload.uploadType,
                                                        baseLocation: upload.baseLocation
                                                    })
                                                });
                                            }
                                        };
                                        reader.readAsArrayBuffer(file);
                                    }
                                    const files = e.target.files;

                                    if (files && files?.length >= 1) {
                                        let total_size = 0;
                                        for (let i = 0; i < files.length; i++)
                                            total_size += files[i]?.size;
                                        if (total_size + used >= max) {
                                            alert("최대 용량을 초과했습니다.")
                                            return;
                                        }

                                        for (let i = 0; i < files.length; i++) {
                                            const file = files[i];
                                            if (file) {
                                                if (file.size > 1024 * 1024 * 1024 * 4) {
                                                    alert('4GB 이상 파일은 업로드 불가능합니다. - ' + file.name)
                                                    return;
                                                }
                                                getStorageFile({ Location: location + "/" + file.name }).then(r => {
                                                    const confirm = { file: r } as Confirm;
                                                    create(file, confirm);
                                                    confirmRef.current = [...confirmRef.current, confirm]
                                                    setUploadConfirm(confirmRef.current)
                                                }).catch(e => {
                                                    if (e?.response?.status == 403 && e?.response.data == 'file not found')
                                                        create(file);
                                                    else {
                                                        console.log(e);
                                                        return;
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    e.target.value = '';
                                }}/>
                            </div>
                        </div>
                        <button className={"btn btn-xs mr-1 hover:underline" + (keyword || selects.length > 0 ? ' hidden' : '')} onClick={() => {
                            if (used >= max) {
                                alert("최대 용량을 초과했습니다.")
                                return;
                            }
                            createFileFolder({ Location: location, Page: page, Base: baseLocation }).then(() => {


                                renew(location, type == -1 || type == 0 ? page : 0, type == 0 ? 0 : -1, "");
                                getFileFolders({ Location: baseLocation }).then(r => {
                                    baseFolders[base] = r;
                                    setBaseFolders({ ...baseFolders });
                                }).catch(e => console.log(e));
                            }).catch(e => {
                                if (e.response.status == 403 && e.response.data == 'storage')
                                    alert('최대 용량을 초과했습니다.');
                                else
                                    console.log(e)
                            })
                        }}>새폴더</button>
                        <button className={"btn btn-xs mr-1 hover:underline" + (selects.length > 0 ? '' : ' hidden')} onClick={() => {
                            if (confirm("다운로드 하시겠습니까?")) {
                                const download = async () => {
                                    const name = new Date().getTime().toString() + ".zip";
                                    const response = await downloadFiles({ name: encodeURIComponent(name), urls: encodeURIComponent(selects.map(s => s.url).join('?')) })
                                    const url = window.URL.createObjectURL(new Blob([response]));
                                    const link = document.createElement('a');
                                    link.href = url;

                                    if (selects.length == 1)
                                        link.setAttribute('download', selects[0].type != 0 ? selects[0].name : selects[0].name + ".zip");
                                    else {
                                        link.setAttribute('download', base + '.zip');
                                    }
                                    document.body.appendChild(link);
                                    link.click();
                                    link.remove();
                                }
                                download();
                            }
                        }}>내려받기</button>
                        {/* <button className={"btn btn-xs mr-1 hover:underline" + (selects.length > 0 ? '' : ' hidden')}>공유</button> */}
                        <button className={"btn btn-xs mr-1 hover:underline" + (selects.length > 0 ? '' : ' hidden')} onClick={() => {
                            if (confirm("삭제 하시겠습니까?"))
                                selects.forEach(s => deleteFile({ Url: s.url }).then(() => setReload(true)));
                        }}>삭제</button>
                        {/* <button className={"btn btn-xs mr-1 hover:underline " + (selects.length > 0 ? '' : ' hidden')}><p className="my-auto text-xs h-full">...</p></button> */}
                        <div className="relative">
                            <button className="btn btn-xs hover:underline" onClick={() => setTypeOpen(!typeOpen)}>파일유형</button>
                            <div className={"absolute top-8 z-[1] border-2 left-0 bg-white w-[150px] h-[220px] p-2 flex flex-col" + (typeOpen ? '' : ' hidden')}>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[25px]" + (type == -1 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setType(-1); setTypeOpen(false); setPage(0); setReload(true); }}><p className="w-[5px] h-[5px] mr-2" />전체 <img alt="down" width={20} height={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (type == -1 ? '' : ' hidden')} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[25px]" + (type == 0 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setType(0); setTypeOpen(false); setPage(0); setReload(true); }}><img alt="folder" width={20} height={20} src="/folder.png" className="w-[20px] h-[20px] mr-2" /> 폴더 <img alt="down" width={20} height={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (type == 0 ? '' : ' hidden')} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[25px]" + (type == 1 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setType(1); setTypeOpen(false); setPage(0); setReload(true); }}><img alt="png" height={20} width={20} src="/png.png" className="w-[20px] h-[20px] mr-2" />이미지 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (type == 1 ? '' : ' hidden')} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[25px]" + (type == 2 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setType(2); setTypeOpen(false); setPage(0); setReload(true); }}><img alt="video" height={20} width={20} src="/video.png" className="w-[20px] h-[20px] mr-2" />비디오 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (type == 2 ? '' : ' hidden')} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[25px]" + (type == 3 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setType(3); setTypeOpen(false); setPage(0); setReload(true); }}><img alt="audio" height={20} width={20} src="/audio.png" className="w-[20px] h-[20px] mr-2" />오디오 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (type == 3 ? '' : ' hidden')} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[25px]" + (type == 4 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setType(4); setTypeOpen(false); setPage(0); setReload(true); }}><img alt="compress" height={20} width={20} src="/compress.png" className="w-[20px] h-[20px] mr-2" />압축 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (type == 4 ? '' : ' hidden')} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[25px]" + (type == 5 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setType(5); setTypeOpen(false); setPage(0); setReload(true); }}><img alt="document" height={20} width={20} src="/document.png" className="w-[20px] h-[20px] mr-2" />문서 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (type == 5 ? '' : ' hidden')} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[25px]" + (type == 6 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setType(6); setTypeOpen(false); setPage(0); setReload(true); }}><img alt="etc" height={20} width={20} src="/etc.png" className="w-[20px] h-[20px] mr-2" />기타 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (type == 6 ? '' : ' hidden')} /></label>
                            </div>
                        </div>
                        <label className={"text-xs text-gray-400 ml-1" + (selects.length > 0 ? '' : ' hidden')}><label className="text-[#8fbee9]">{selects.length}</label>개 선택</label>
                    </div>
                    <div className="flex items-center">
                        <div className="relative">
                            <button className="text-xs flex mr-2 items-center" onClick={() => { setOrderOpen(!orderOpen); setDescOpen(false) }}>
                                <Order />
                                <img alt="down" height={18} width={18} src='/down.png' className={"w-[18px] h-[18px] ml-1" + (orderOpen ? ' hidden' : '')} />
                                <img alt="up" height={18} width={18} src="/up.png" className={"w-[18px] h-[18px] ml-1" + (orderOpen ? '' : ' hidden')} />
                            </button>
                            <div className={"absolute bg-white z-[1] top-5 right-0 border-2 w-[120px] h-[204px] flex flex-col text-left" + (orderOpen ? '' : ' hidden')}>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[40px]" + (order == 0 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(0); setOrderOpen(false); setDescOpen(false); setDesc(false); setReload(true); }}>종류 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (order == 0 ? "" : " hidden")} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[40px]" + (order == 1 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(1); setOrderOpen(false); setDescOpen(false); setDesc(false); setReload(true); }}>이름 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (order == 1 ? "" : " hidden")} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[40px]" + (order == 2 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(2); setOrderOpen(false); setDescOpen(false); setDesc(false); setReload(true); }}>크기 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (order == 2 ? "" : " hidden")} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[40px]" + (order == 3 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(3); setOrderOpen(false); setDescOpen(false); setDesc(true); setReload(true); }}>수정한날짜 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (order == 3 ? "" : " hidden")} /></label>
                                <label className={"cursor-pointer hover:underline p-2 flex items-center h-[40px]" + (order == 4 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(4); setOrderOpen(false); setDescOpen(false); setDesc(true); setReload(true); }}>올린날짜 <img alt="down" height={20} width={20} src="/downo.png" className={"w-[20px] h-[20px] ml-auto" + (order == 4 ? "" : " hidden")} /></label>
                            </div>
                        </div>
                        {order > 0 && order < 5 ?
                            <div className="relative z-[1]">
                                <button className="text-xs flex mr-2 items-center" onClick={() => { setOrderOpen(false); setDescOpen(!descOpen); }}>
                                    <Desc />
                                    <img alt="down" height={20} width={20} src='/down.png' className="w-[18px] h-[18px] ml-1" />
                                </button>
                                <DescButton />
                            </div>
                            : <></>
                        }
                        <img alt="list" height={20} width={20} src="/list.png" className={'w-[20px] h-[20px] cursor-pointer mx-2' + (style ? '' : ' hidden')} onClick={() => setStyle(!style)} />
                        <img alt="listo" height={20} width={20} src="/listo.png" className={'w-[20px] h-[20px] cursor-pointer mx-2' + (style ? ' hidden' : '')} />
                        <img alt="grid" height={20} width={20} src="/grid.png" className={'w-[20px] h-[20px] cursor-pointer mx-2' + (style ? ' hidden' : '')} onClick={() => setStyle(!style)} />
                        <img alt="grido" height={20} width={20} src="/grido.png" className={'w-[20px] h-[20px] cursor-pointer mx-2' + (style ? '' : ' hidden')} />
                        <img alt="exclamation" height={20} width={20} src="/exclamation.png" className={"w-[20px] h-[20px] cursor-pointer mx-2" + (extra ? ' hidden' : '')} onClick={() => setExtra(!extra)} />
                        <img alt="exclamationo" height={20} width={20} src="/exclamationo.png" className={"w-[20px] h-[20px] cursor-pointer mx-2" + (extra ? '' : ' hidden')} onClick={() => setExtra(!extra)} />
                    </div>
                </div>
                <div className="divider"></div>
                <div className="flex SD:h-[120px] HD:h-[310px] FHD:h-[670px]" onContextMenu={e => e.preventDefault()}>
                    {style ? // Icon 형태
                        <div className="overflow-y-auto flex flex-wrap w-full h-full ">
                            {files?.map((file, index) => <div key={index} className="w-[150px] h-[196px] group cursor-pointer mr-2 mb-2" onMouseDown={e => { if (e.button == 2) e.preventDefault() }} onClick={e => {
                                setDescOpen(false);
                                setTypeOpen(false);
                                setOrderOpen(false);
                                setOpenUpload(false);
                                if (e.target as HTMLInputElement && (e.target as HTMLInputElement).type == 'checkbox')
                                    return;
                                else {
                                    if (file.type == 0)
                                        renew(file?.url, 0, -1, "");
                                    else {
                                        if (confirm("다운로드 하시겠습니까?")) {
                                            var el = document.createElement('a');
                                            el.href = file.url;
                                            el.download = file.name;
                                            el.click();
                                        }
                                    }
                                }
                            }}>
                                <div className="checkbox-div w-[150px] h-[150px] relative flex flex-col items-center justify-center rounded-lg group-hover:border-2 border-gray-300" >
                                    <input type="checkbox" checked={selects.includes(file)} name="check" className="absolute top-2 left-2 checkbox border-0 [--chkbg:#8fbee9] [--chkfg:white] self-start group-hover:border-gray-300 checked:group-hover:border-[#dce2e8] group-hover:border-2 " onChange={() => { }} onClick={e => {
                                        if ((e.target as HTMLInputElement).checked)
                                            setSelect([...selects, file]);
                                        else
                                            setSelect([...selects.filter(f => f.name != file.name)]);
                                    }} />
                                    <img alt="fileType" height={80} width={80} src={FileTypeImage(file)} className="w-[80px] h-[80px]" />
                                </div>
                                <div className="my-auto text-center w-full text-sm">{file?.name}</div>
                            </div>)}
                        </div>
                        : // List 형태
                        <div className="w-full px-4 overflow-y-auto">
                            <table>
                                <thead>
                                    <tr>
                                        <th className="w-[30px] min-w-[30px]"></th>
                                        <th className="w-[44px] min-w-[44px]">종류</th>
                                        <th className="w-full text-left">이름</th>
                                        <th className="w-[100px] min-w-[100px]">크기</th>
                                        <th className="w-[200px] min-w-[200px] text-right">수정한 날짜</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {files?.map((file, index) => <tr key={index}>
                                        <td className="w-[30px]">
                                            <input type="checkbox" name="check" className="checkbox checkbox-sm [--chkbg:#8fbee9] [--chkfg:white] self-start border border-gray-300 checked:hover:border-[#dce2e8] hover:border-2 " />
                                        </td>
                                        <td className="p-2">
                                            <img alt="filyType" height={28} width={28} src={FileTypeImage(file)} className="w-[28px] h-[28px]" />
                                        </td>
                                        <td>
                                            <label className="cursor-pointer hover:underline" onClick={() => {
                                                setDescOpen(false);
                                                setTypeOpen(false);
                                                setOrderOpen(false);
                                                setOpenUpload(false);
                                                if (file.type == 0)
                                                    renew(file?.url, 0, -1, "");
                                                else {
                                                    if (confirm("다운로드 하시겠습니까?")) {
                                                        var el = document.createElement('a');
                                                        el.href = file.url;
                                                        el.download = file.name;
                                                        el.click();
                                                    }
                                                }
                                            }}>{file.name}</label>
                                        </td>
                                        <td className="text-end">{CalcSize(file.size)}</td>
                                        <td className="text-right">{getStorageDate(file?.modifyDate)}</td>
                                    </tr>)}
                                </tbody>
                            </table>
                        </div>
                    }

                    {extra ?
                        selects.length == 0 ?
                            <div className="flex flex-col w-[280px] min-w-[280px] h-full border-l-2 p-4 items-center justify-center">
                                <div className="text-center break-keep text-gray-400 text-sm">
                                    원하는 파일, 폴더를 선택하면 이곳에 상세보기가 표시됩니다.
                                </div>
                            </div>
                            :
                            <div className="flex flex-col w-[280px] min-w-[280px] h-full border-l-2 p-4">
                                <div className="flex">
                                    <div className="font-bold w-[232px] break-all">{selects[selects.length - 1]?.name}</div>
                                    <button className="w-[24px] min-w-[24px] h-[24px]" onClick={() => setExtra(false)}><img alt="x" width={24} height={24} src="/x.png" className="w-[24px] h-[24px]" /></button>
                                </div>
                                <div className="p-4 flex w-[246px] h-[246px] items-center justify-center">
                                    <img alt="fileType" height={150} width={150} src={FileTypeImage(selects[selects.length - 1])} className="w-[150px] h-[150px]" />
                                </div>
                                <div>
                                    <table>
                                        <tbody className="text-xs text-left">
                                            <tr>
                                                <th className="w-[80px] min-w-[80px]">종류</th>
                                                <td className="w-[166px]">{FileTypeName(selects[selects.length - 1])}</td>
                                            </tr>
                                            <tr>
                                                <th>위치</th>
                                                <td><div className="w-[166px] overflow-ellipsis whitespace-nowrap overflow-hidden">{base}:{location.replaceAll(baseLocation, "")}/</div></td>
                                            </tr>
                                            <tr>
                                                <th>크기</th>
                                                <td><div className="w-[166px] overflow-ellipsis whitespace-nowrap overflow-hidden">{CalcSize(selects[selects.length - 1].size)}({selects[selects.length - 1].size} bytes)</div></td>
                                            </tr>
                                            <tr>
                                                <th>올린 날짜</th>
                                                <td>{getStorageDate(selects[selects.length - 1].createDate)}</td>
                                            </tr>
                                            <tr>
                                                <th>수정한 날짜</th>
                                                <td>{getStorageDate(selects[selects.length - 1].modifyDate)}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        :
                        <></>
                    }
                    <div className={"absolute bottom-8 right-8 flex cursor-pointer justify-between items-center px-4 border-2 bg-[#8fbee9] z-[2] w-[380px] h-[50px]" + (uploads.length > 0 && sendFold ? '' : ' hidden')} onClick={e => {
                        if ((e.target as HTMLElement).id == 'cancel')
                            return;
                        setSendFold(!sendFold);
                    }}>
                        <div className="felx items-center">
                            <label className="text-white">{`${uploads.filter(f => f.status == 0).length} / ${uploads.length}개 ${uploads.filter(f => f.status == 1).length > 0 ? '올리는 중' : '완료'}`}{uploads.filter(f => f.status == -1).length > 0 ? <label className="text-white text-sm">{uploads.filter(f => f.status == -1).length}개 취소</label> : <></>}</label>
                            <label className="font-bold"></label>
                        </div>
                        <label id="cancel" className="text-white text-sm cursor-pointer hover:underline" onClick={() => {
                            if (uploads.filter(f => f.status == 1).length > 0) {
                                if (confirm("모든 업로드를 취소하시겠습니까?")) {
                                    // 취소기능
                                    uploads.forEach(upload => {
                                        upload.status = -1;
                                        cancelUpload({ Location: upload.url, Name: upload.name, Key: upload.key });
                                    });
                                    setReload(true);
                                } else
                                    return;
                            }
                            uploadsRef.current = [];
                            setUploads(uploadsRef.current);
                        }} >닫기</label>

                    </div>
                    <div className={"absolute bottom-8 right-8 p-4 flex flex-col border-2 bg-white z-[2] w-[380px] h-[400px]" + (uploads.length > 0 && !sendFold ? '' : ' hidden')}>
                        <div className="flex justify-between items-center">
                            <label>
                                <label className="text-[#8fbee9]">{uploads.filter(f => f.status == 0).length}</label>
                                {` / ${uploads.length}개 `}
                                <label className="font-bold">{uploads.filter(f => f.status == 1).length > 0 ? '올리는 중' : '완료'}</label>
                            </label>
                            <div className="flex">
                                <img alt="-" width={16} height={16} src="/-b.png" className="w-[16px] h-[16px] cursor-pointer mr-2" onClick={() => setSendFold(!sendFold)} />
                                <img alt="large_x" width={16} height={16} src="/large_x.png" className="w-[16px] h-[16px] cursor-pointer" onClick={() => {
                                    if (uploads.filter(f => f.status == 1).length > 0) {
                                        if (confirm("모든 업로드를 취소하시겠습니까?")) {
                                            // 취소기능
                                            uploads.forEach(upload => {
                                                upload.status = -1;
                                                cancelUpload({ Location: upload.url, Name: upload.name, Key: upload.key });
                                            });
                                            setReload(true);
                                        } else
                                            return;
                                    }
                                    uploadsRef.current = [];
                                    setUploads(uploadsRef.current);
                                }} />
                            </div>
                        </div>
                        <div ref={scrollRef} className="flex flex-col overflow-y-auto p-2">
                            {uploads?.map((upload, index) => <div key={index} className="flex flex-col mt-2">
                                <input key={upload.key} type="range" className={"range range-xs mt-2 mb-1 cursor-default" + (upload.bytes ? (upload.status == 1 ? ' range-success' : (upload.status == 0 ? ' range-info' : ' range-warning')) : '')} value={upload.bytes ? (upload.index * 1000 / upload.totalIndex) : 0} min={0} max={1000} disabled onChange={() => { }} />
                                <div className="flex">
                                    <img alt="file" width={32} height={32} src={UploadImage(upload.type)} className="w-[32px] h-[32px] mr-2" />
                                    <div className="flex flex-col text-xs w-[327px]">
                                        <div className={"w-[260px] whitespace-nowrap overflow-ellipsis overflow-hidden" + (upload.status == -1 ? ' line-through' : '')}>{upload.name}</div>
                                        <div className={"w-[260px] " + (upload.status == -1 ? ' line-through' : '')}>{upload.bytes ? `${CalcSize(upload.bytes.length)} ${base}:${upload.location}` : '파일 불러오는중'}</div>
                                    </div>
                                    <img alt="cancel" height={16} width={16} src="/large_x.png" className="w-[16px] min-w-[16px] h-[16px] min-h-[16px] cursor-pointer" hidden={upload.status != 1} onClick={() => {
                                        upload.status = -1;
                                        cancelUpload({ Location: upload.url, Name: upload.name, Key: upload.key });
                                    }} />
                                </div>
                            </div>)}
                        </div>
                    </div>
                </div>
                {uploadConfirm.length > 0 ?
                    <div style={{ zIndex: 100 }} className={"fixed top-[50%] left-[50%] -translate-x-1/2 -translate-y-1/2 p-0 bg-white w-[360px] h-[360px] border-2 p-4 flex flex-col"}>
                        <img src="/large_x.png" className="w-[16px] h-[16px] self-end cursor-pointer" onClick={() => {
                            const confirm = uploadConfirm[uploadConfirm.length - 1];
                            if (confirm.upload) {
                                confirm.upload.status = -1;
                                cancelUpload({ Location: confirm.upload.url, Name: confirm.upload.name, Key: confirm.upload.key });
                            }
                            confirmRef.current = confirmRef.current.filter(f => f != confirm);
                            setUploadConfirm([...confirmRef.current]);
                        }} />
                        <img src="/exclamation.png" className="w-[100px] h-[100px] self-center p-4" />
                        <label className="self-center font-bold text-sm pt-2">이 위치에 같은 이름의 파일이 이미있습니다.</label>
                        <label className="self-center font-bold text-sm pb-2">기존 파일을 덮어 쓰시겠습니까?</label>
                        <div className="flex flex-col p-4">
                            <label className="text-xs pb-4">파일명:{uploadConfirm[uploadConfirm.length - 1]?.upload?.name}</label>
                            <label className="text-xs py-2">신규 : {getStorageDate(new Date().getTime())}</label>
                            <label className="text-xs py-2">기존 : {getStorageDate(uploadConfirm[uploadConfirm.length - 1]?.file.modifyDate)}</label>
                        </div>
                        <label className={'text-center ' + (uploadConfirm[uploadConfirm.length - 1].upload ? ' hidden' : '')}>
                            파일 로딩중
                        </label>
                        <div className={"flex justify-evenly px-4" + (uploadConfirm[uploadConfirm.length - 1].upload ? '' : ' hidden')}>
                            <button className="btn btn-xs hover:underline" onClick={() => {
                                const confirm = uploadConfirm[uploadConfirm.length - 1];
                                const upload = confirm.upload;
                                const start = upload.index * chunkSize;
                                let binary = "";
                                for (let i = 0; i < chunkStack; i++)
                                    binary += String.fromCharCode(...upload.bytes.slice(start + i * size, start + (i + 1) * size));
                                const chunk = window.btoa(binary); // base64
                                socket.publish({
                                    destination: "/api/pub/uploadFile/" + user?.username, body: JSON.stringify({
                                        key: upload.key,
                                        index: upload.index,
                                        totalIndex: upload.totalIndex,
                                        chunk: chunk,
                                        location: upload.url,
                                        name: upload.name,
                                        uploadType: upload.uploadType
                                    })
                                });
                                confirmRef.current = confirmRef.current.filter(f => f != confirm);
                                setUploadConfirm([...confirmRef.current]);
                            }}>덮어쓰기</button>
                            <button className="btn btn-xs hovoer:underline" onClick={() => {
                                const confirm = uploadConfirm[uploadConfirm.length - 1];
                                const upload = confirm.upload;
                                upload.uploadType = 1;
                                const start = upload.index * chunkSize;
                                let binary = "";
                                for (let i = 0; i < chunkStack; i++)
                                    binary += String.fromCharCode(...upload.bytes.slice(start + i * size, start + (i + 1) * size));
                                const chunk = window.btoa(binary); // base64
                                socket.publish({
                                    destination: "/api/pub/uploadFile/" + user?.username, body: JSON.stringify({
                                        key: upload.key,
                                        index: upload.index,
                                        totalIndex: upload.totalIndex,
                                        chunk: chunk,
                                        location: upload.url,
                                        name: upload.name,
                                        uploadType: upload.uploadType
                                    })
                                });
                                confirmRef.current = confirmRef.current.filter(f => f != confirm);
                                setUploadConfirm([...confirmRef.current]);
                            }}>추가저장</button>
                            <button className="btn btn-xs hover:underline" onClick={() => {
                                const confirm = uploadConfirm[uploadConfirm.length - 1];
                                confirm.upload.status = -1;
                                cancelUpload({ Location: confirm.upload.url, Name: confirm.upload.name, Key: confirm.upload.key });
                                confirmRef.current = confirmRef.current.filter(f => f != confirm);
                                setUploadConfirm([...confirmRef.current]);
                            }}>취소</button>
                        </div>
                    </div>
                    : <></>}

                {maxPage > 1 ?
                    < div className="flex justify-center items-center absolute bottom-0 left-0 w-full h-[45px]">
                        <img alt="lleft" height={16} width={16} src={page > 0 ? "/lleft.png" : "/lleftg.png"} className={"w-[16px] h-[16px]" + (page > 0 ? ' cursor-pointer hover:bg-gray-300 rounded-full' : '')} onClick={() => {
                            const now = Math.max(0, page - (page % 10) - 1);
                            setFiles([]);
                            renew(location, now, type, keyword);
                        }} />
                        <img alt="left" height={16} width={16} src={page > 0 ? "/left.png" : "/leftg.png"} className={"w-[16px] h-[16px]" + (page > 0 ? ' cursor-pointer hover:bg-gray-300 rounded-full' : '')} onClick={() => {
                            const now = Math.max(0, page - 1);
                            setFiles([]);
                            renew(location, now, type, keyword);
                        }} />
                        {[0, 1, 2, 3, 4, 5, 6, 7, 8, 9].map(n => {
                            const now = page - page % 10 + n;
                            if (now >= maxPage)
                                return;
                            return <button key={n} disabled={now == page} className="btn btn-xs" onClick={() => {
                                setFiles([]);
                                renew(location, now, type, keyword);
                            }}>{now + 1}</button>;
                        })}
                        <img alt="right" height={16} width={16} src={page < maxPage - 1 ? "/right.png" : "/rightg.png"} className={"w-[16px] h-[16px]" + (page < maxPage - 1 ? ' cursor-pointer hover:bg-gray-300 rounded-full' : '')} onClick={() => {
                            const now = Math.min(maxPage - 1, page + 1);
                            setFiles([]);
                            renew(location, now, type, keyword);
                        }} />
                        <img alt="rright" height={16} width={16} src={page < maxPage - 1 ? "/rright.png" : "/rrightg.png"} className={"w-[16px] h-[16px]" + (page < maxPage - 1 ? ' cursor-pointer hover:bg-gray-300 rounded-full' : '')} onClick={() => {
                            const now = Math.min(maxPage - 1, (page - page % 10) + 10);
                            setFiles([]);
                            renew(location, now, type, keyword);
                        }} />
                    </div>
                    : <></>
                }
            </div>
        </div>
    </Main >
}
