"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import { getStorageFile, getStorageFiles, getUser } from "../API/UserAPI";
import { getStorageDate } from "../Global/Method";

export default function Home() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [isClientLoading, setClientLoading] = useState(true);
    // const [fold, setFold] = useState(true);
    const [orderOpen, setOrderOpen] = useState(false);
    const [order, setOrder] = useState(0);
    const [descOpen, setDescOpen] = useState(false);
    const [desc, setDesc] = useState(false);
    const [style, setStyle] = useState(true);
    const [extra, setExtra] = useState(false);
    const [selects, setSelect] = useState([] as any[]);
    const [base, setBase] = useState('개인');
    const [baseLocation, setBaseLocation] = useState('');
    const [baseFolders, setBaseFolders] = useState([] as any[]);
    const [location, setLocation] = useState('');
    const [used, setUsed] = useState(0);
    const [page, setPage] = useState(0);
    const [maxPage, setMaxPage] = useState(0);
    const [listFolder, setListFolder] = useState([] as any[]);
    const [files, setFiles] = useState([] as any[]);
    const max = 10737418240;
    const [fold, setFold] = useState(true);

    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
                setLocation('/api/user/' + r?.username + '/storage');
                setBaseLocation('/api/user/' + r?.username + '/storage');
                getStorageFiles({ Location: "/api/user/" + r?.username + "/storage" }).then(r => { setMaxPage(r.totalPages); setFiles(r.content); }).catch(e => console.log(e));
                getStorageFile({ Location: '/api/user/' + r?.username + "/storage" }).then(r => setUsed(r.size)).catch(e => console.log(e));
            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            window.location.href = '/';
    }, [ACCESS_TOKEN])
    function FileTypeImage(file: any) {
        switch (file.type) {
            case 0: return "/folder.png";
            case 1: return file?.url;
            case 2: return "/video.png";
            case 3: return "/audio.png";
            case 4: return "/compress.png";
            case 5: return "/document.png";
            default: "/etc.png";
        }
    }
    function Icon(props: { file: any }) {
        const file = props.file;
        return <div className="w-[150px] h-[196px] group cursor-pointer mr-2 mb-2" onClick={e => {
            if (e.target as HTMLInputElement && (e.target as HTMLInputElement).type == 'checkbox')
                return;
            else {
                if (file.type == 0) {
                    setLocation(file?.url)
                    getStorageFiles({ Location: file?.url }).then(r => { setMaxPage(r.totalPages); setFiles(r.content); }).catch(e => console.log(e));
                    getStorageFile({ Location: baseLocation }).then(r => setUsed(r.size)).catch(e => console.log(e));
                } else {
                    // 파일 다운로드 등 기타 작동
                }
            }
        }}>
            <div className="checkbox-div w-[150px] h-[150px] relative flex flex-col items-center justify-center rounded-lg group-hover:border-2 border-gray-300" >
                <input type="checkbox" name="check" className="absolute top-2 left-2 checkbox border-0 [--chkbg:#8fbee9] [--chkfg:white] self-start group-hover:border-gray-300 checked:group-hover:border-[#dce2e8] group-hover:border-2" onClick={() => { }} />
                <img src={FileTypeImage(file)} className="w-[80px] h-[80px]" />
            </div>
            <div className="my-auto text-center w-full text-sm">{file?.name}</div>
        </div>
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
    function List(props: { file: any }) {
        const file = props.file;
        return <tr>
            <td className="w-[30px]">
                <input type="checkbox" name="check" className="checkbox checkbox-sm [--chkbg:#8fbee9] [--chkfg:white] self-start border border-gray-300 checked:hover:border-[#dce2e8] hover:border-2" />
            </td>
            <td className="p-2">
                <img src={FileTypeImage(file)} className="w-[28px] h-[28px]" />
            </td>
            <td>{file.name}</td>
            <td className="text-end">{CalcSize(file.size)}</td>
            <td className="text-right">{getStorageDate(file?.modifyDate)}</td>
        </tr>
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
        return <div className="absolute bg-white top-5 right-0 border-2 w-[120px] h-[204px] flex flex-col text-left">
            <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (!desc ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setDesc(false); setDescOpen(false); }}>{ascName} {!desc ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
            <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (desc ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setDesc(true); setDescOpen(false); }}>{descName} {desc ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
        </div>
    }
    function renew(location: string) {
        setLocation(location);
        getStorageFiles({ Location: location }).then(r => { setMaxPage(r.totalPages); setFiles(r.content); }).catch(e => console.log(e));
        getStorageFile({ Location: baseLocation }).then(r => setUsed(r.size)).catch(e => console.log(e));
    }
    function FolderTree(props: { name: string, location: string }) {

        return <div>
            <label className={"cursor-pointer font-bold flex text-lg items-center" + (base == props?.name ? ' text-[#8fbee9]' : '')} onClick={e => {
                if (base == props.name && (e.target as HTMLElement).tagName == "IMG")
                    return;
                setBase(props.name);
                setBaseLocation(props.location);
                setLocation(props.location);
                getStorageFiles({ Location: props.location }).then(r => { setMaxPage(r.totalPages); setFiles(r.content); }).catch(e => console.log(e));
                getStorageFile({ Location: props.location }).then(r => setUsed(r.size)).catch(e => console.log(e));
                if (base != props.name)
                    setFold(true);
            }}>
                <img src={fold ? '/right.png' : '/down.png'} className="w-[18px] h-[18px]" onClick={() => setFold(!fold)} />
                {props.name}폴더
            </label>
            {
                fold ?
                    <></> :
                    <div className="flex flex-col pl-6">
                        {/* 여기 작업해야함! folders 받아옴 */}
                        <label className="cursor-pointer">개인폴더</label>
                        <label className="cursor-pointer">그룹폴더</label>
                        <label className="cursor-pointer">그룹내역할폴더</label>
                        <label className="cursor-pointer">전체역할폴더</label>
                    </div>
            }
        </div>
    }
    
    return <Main user={user} isClientLoading={isClientLoading}>
        <div className="w-2/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-2 flex flex-col relative ml-12">
                <FolderTree name="개인" location={'/api/user/' + user?.username + '/storage'} />
                {/* <FolderTree name="그룹" location="" />
                <FolderTree name="그룹내역할" location="" />
                <FolderTree name="전체역할" location="" /> */}
                <label className="cursor-pointer pl-[18px] text-lg mt-2 font-bold">사진</label>
                <label className="cursor-pointer pl-[18px] text-lg mt-2 font-bold">동영상</label>
                <label className="cursor-pointer pl-[18px] text-lg mt-2 font-bold">음악</label>
                <div className="h-[150px] absolute bottom-0 left-0 w-full mt-auto border flex flex-col items-center p-8">
                    <div className="flex justify-between items-end w-full">
                        <label><label className="text-[#8fbee9]">{CalcSize(used)}</label> / {CalcSize(max)}</label>
                        <label className="text-xs text-gray-500">여유 {CalcSize(max - used)}</label>
                    </div>
                    <input type="range" className="range range-info range-xs mt-2" defaultValue={used * 1000 / max} min={0} max={1000} disabled />
                    <label className="flex mt-2 self-end items-center"><img src="/trash_can.png" className="w-[24px] h-[24px]"></img>휴지통</label>
                </div>
            </div>
        </div>
        <div className="w-10/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-4 relative">
                <div className="flex justify-between">
                    <label className="text-lg font-bold flex items-center"><label className="cursor-pointer" onClick={() => {
                        renew(baseLocation);
                    }}>{base + '폴더'}</label>{' > '} 새폴더</label>
                    {/* 여기도 작업해야함 */}
                    <div className="border-2 border-gray-500 rounded-lg flex p-2">
                        <input type="text" className="outline-none text-xs w-[300px]" onKeyDown={(e) => { if (e.key == "Enter") document.getElementById('file_search')?.click() }} />
                        <img id="file_search" src="/searchb.png" className="w-[16px] h-[16px] cursor-pointer"></img>
                    </div>
                </div>
                <div className="flex justify-between">
                    <div className="flex items-center">
                        <input id="all" type="checkbox" className="mr-2 my-auto checkbox checked:border-0 [--chkbg:#8fbee9] [--chkfg:white] self-start group-hover:border-gray-300 checked:hover:border-0 hover:border-2" onClick={e => {
                            document.getElementsByName('check').forEach(check => (check as HTMLInputElement).checked = (e.target as HTMLInputElement)?.checked);
                        }} />
                        <button className="btn btn-info btn-sm text-white mr-2">올리기</button>
                        <button className="btn btn-sm mr-2">새폴더</button>
                        <button className="btn btn-sm">파일유형</button>
                    </div>
                    <div className="flex items-center">
                        <div className="relative">
                            <button className="text-xs flex mr-2 items-center" onClick={() => { setOrderOpen(!orderOpen); setDescOpen(false) }}>
                                <Order />
                                <img src={orderOpen ? "/up.png" : '/down.png'} className="w-[18px] h-[18px] ml-1" />
                            </button>
                            {orderOpen ?
                                <div className="absolute bg-white top-5 right-0 border-2 w-[120px] h-[204px] flex flex-col text-left">
                                    <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (order == 0 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(0); setOrderOpen(false); setDescOpen(false); setDesc(false) }}>종류 {order == 0 ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
                                    <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (order == 1 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(1); setOrderOpen(false); setDescOpen(false); setDesc(false) }}>이름 {order == 1 ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
                                    <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (order == 2 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(2); setOrderOpen(false); setDescOpen(false); setDesc(false) }}>크기 {order == 2 ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
                                    <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (order == 3 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(3); setOrderOpen(false); setDescOpen(false); setDesc(true) }}>수정한날짜 {order == 3 ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
                                    <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (order == 4 ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setOrder(4); setOrderOpen(false); setDescOpen(false); setDesc(true) }}>올린날짜 {order == 4 ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
                                </div>
                                : <></>
                            }
                        </div>
                        {order > 0 && order < 5 ?
                            <div className="relative">
                                <button className="text-xs flex mr-2 items-center" onClick={() => { setOrderOpen(false); setDescOpen(!descOpen); }}>
                                    <Desc />
                                    <img src={'/down.png'} className="w-[18px] h-[18px] ml-1" />
                                </button>
                                {descOpen ? <DescButton /> : <></>}
                            </div>
                            : <></>
                        }
                        <button className="mx-2" onClick={() => setStyle(false)}><img src={style ? "/list.png" : "/listo.png"} className="w-[20px] h-[20px]" /></button>
                        <button className="mx-2" onClick={() => setStyle(true)}><img src={style ? "/grido.png" : "/grid.png"} className="w-[20px] h-[20px]" /></button>
                        <button className="mx-2" onClick={() => setExtra(!extra)}><img src="/exclamation.png" className="w-[20px] h-[20px]" /></button>
                    </div>
                </div>
                <div className="divider"></div>
                <div className="flex h-[690px]">
                    {style ?
                        <div className="overflow-y-auto flex flex-wrap w-full h-full">
                            {files?.map((file, index) => <Icon key={index} file={file} />)}
                        </div>
                        :
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
                                    {files?.map((file, index) => <List key={index} file={file} />)}
                                </tbody>
                            </table>
                        </div>
                    }

                    {extra ?
                        <div className="flex flex-col w-[280px] h-full border-l-2 p-4">
                            <div className="flex">
                                <div className="font-bold w-[232px] break-all">a a a a a a a a a a a a a a a a a a a a a a a a a aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</div>
                                <button className="w-[24px] min-w-[24px] h-[24px]" onClick={() => setExtra(false)}><img src="/x.png" /></button>
                            </div>
                            <div className="p-4 flex w-[246px] h-[246px] items-center justify-center">
                                <img src="/folder.png" className="w-[150px] h-[150px]" />
                            </div>
                            <div>
                                <table>
                                    <tbody className="text-xs text-left">
                                        <tr>
                                            <th className="w-[80px] min-w-[80px]">종류</th>
                                            <td className="w-[166px]">폴더</td>
                                        </tr>
                                        <tr>
                                            <th>위치</th>
                                            {/* <td>개인:/</td> */}
                                            <td><div className="w-[166px] overflow-ellipsis whitespace-nowrap overflow-hidden">개인:/a/b/c/d/e/f/g/h/i/j/k/l/m/n/o/p/q/r/s/t/u/v/w/x/y/z</div></td>
                                        </tr>
                                        <tr>
                                            <th>크기</th>
                                            <td>1GB</td>
                                        </tr>
                                        <tr>
                                            <th>올린 날짜</th>
                                            <td>2024.07.22 오전 09:45</td>
                                        </tr>
                                        <tr>
                                            <th>수정한 날짜</th>
                                            <td>2024.07.22 오전 09:45</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        :
                        <></>
                    }
                </div>
                <div className="flex justify-center absolute bottom-0 left-0 w-full">
                    <button><img src="/lleft.png" className="w-[12px] h-[12px]" /></button>
                    <button><img src="/left.png" className="w-[12px] h-[12px]" /></button>
                    <button className="btn btn-xs">1</button>
                    <button><img src="/right.png" className="w-[12px] h-[12px]" /></button>
                    <button><img src="/rright.png" className="w-[12px] h-[12px]" /></button>
                </div>
            </div>
        </div>
    </Main>
}
