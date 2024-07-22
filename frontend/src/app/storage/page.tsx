"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import { getUser } from "../API/UserAPI";

export default function Home() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [isClientLoading, setClientLoading] = useState(true);
    const [fold, setFold] = useState(true);
    const [orderOpen, setOrderOpen] = useState(false);
    const [order, setOrder] = useState(0);
    const [descOpen, setDescOpen] = useState(false);
    const [desc, setDesc] = useState(false);
    const [style, setStyle] = useState(true);
    const [extra, setExtra] = useState(false);
    const [selects, setSelect] = useState([] as any[]);
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            location.href = '/';
    }, [ACCESS_TOKEN])
    function Icon() {
        return <div className="w-[150px] h-[196px] group cursor-pointer mr-2 mb-2">
            <div className="checkbox-div w-[150px] h-[150px] relative flex flex-col items-center justify-center rounded-lg group-hover:border-2 border-gray-300" >
                <input type="checkbox" name="check" className="absolute top-2 left-2 checkbox border-0 [--chkbg:#8fbee9] [--chkfg:white] self-start group-hover:border-gray-300 checked:group-hover:border-[#dce2e8] group-hover:border-2"/>
                <img src="/folder.png" className="w-[80px] h-[80px]" />
            </div>
            <div className="my-auto text-center w-full text-sm">폴더이름</div>
        </div>
    }
    function List() {
        return <tr>
            <td className="w-[30px]">
                <input type="checkbox" name="check" className="checkbox checkbox-sm [--chkbg:#8fbee9] [--chkfg:white] self-start border border-gray-300 checked:hover:border-[#dce2e8] hover:border-2" />
            </td>
            <td className="p-2">
                <img src="/folder.png" className="w-[28px] h-[28px]" />
            </td>
            <td>폴더</td>
            <td>100.4KB</td>
            <td className="text-right">2024. 07. 22. 오후 12:36</td>
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
        let asc = "";
        let desc = "";
        switch (order) {
            case 1:
                desc = 'ㅎ-ㄱ';
                asc = "ㄱ-ㅎ";
            case 2:
                desc = '큰순';
                asc = '작은순';
            case 3:
            case 4:
                desc = "최신순";
                asc = '오래된순';
        }
        return <div className="absolute bg-white top-5 right-0 border-2 w-[120px] h-[204px] flex flex-col text-left">
            <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (!desc ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setDesc(false); setDescOpen(false); }}>{asc} {!desc ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
            <label className={"cursor-pointer p-2 flex items-center h-[40px]" + (desc ? ' font-bold text-[#8fbee9]' : '')} onClick={() => { setDesc(true); setDescOpen(false); }}>{desc} {desc ? <img src="/downo.png" className="w-[20px] h-[20px] ml-auto" /> : <></>}</label>
        </div>
    }
    return <Main user={user} isClientLoading={isClientLoading}>
        <div className="w-2/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-2 flex flex-col relative ml-12">
                <label className="cursor-pointer font-bold text-[#8fbee9] flex text-lg items-center" onClick={() => setFold(!fold)}><img src={fold ? '/right.png' : '/down.png'} className="w-[18px] h-[18px]" />모든 파일</label>
                {fold ?
                    <></> :
                    <div className="flex flex-col pl-6">
                        <label className="cursor-pointer">개인폴더</label>
                        <label className="cursor-pointer">그룹폴더</label>
                        <label className="cursor-pointer">그룹내역할폴더</label>
                        <label className="cursor-pointer">전체역할폴더</label>
                    </div>
                }
                <label className="cursor-pointer pl-[18px] text-lg mt-2 font-bold">사진</label>
                <label className="cursor-pointer pl-[18px] text-lg mt-2 font-bold">동영상</label>
                <label className="cursor-pointer pl-[18px] text-lg mt-2 font-bold">음악</label>
                <div className="h-[150px] absolute bottom-0 left-0 w-full mt-auto border flex flex-col items-center p-8">
                    <div className="flex justify-between items-end w-full">
                        <label><label className="text-[#8fbee9]">5GB</label> / 10GB</label>
                        <label className="text-xs text-gray-500">여유 {(10 - 5).toLocaleString('ko-kr', { maximumFractionDigits: 1 })}GB</label>
                    </div>
                    <input type="range" className="range range-info range-xs mt-2" defaultValue={5 / 10 * 1000} min={0} max={1000} disabled />
                    <label className="flex mt-2 self-end items-center"><img src="/trash_can.png" className="w-[24px] h-[24px]"></img>휴지통</label>
                </div>
            </div>
        </div>
        <div className="w-10/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-4 relative">
                <div className="flex justify-between">
                    <label className="text-lg font-bold flex items-center">개인폴더 {'>'} 새폴더</label>
                    <div className="border-2 border-gray-500 rounded-lg flex p-2">
                        <input type="text" className="outline-none text-xs w-[300px]" onKeyDown={(e) => { if (e.key == "Enter") document.getElementById('file_search')?.click() }} />
                        <img id="file_search" src="/searchb.png" className="w-[16px] h-[16px] cursor-pointer"></img>
                    </div>
                </div>
                <div className="flex justify-between">
                    <div className="flex items-center">
                        <input id="all" type="checkbox" className="mr-2 my-auto checkbox border-0 [--chkbg:#8fbee9] [--chkfg:white] self-start group-hover:border-gray-300 checked:hover:border-0 hover:border-2" onClick={e => {
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
                            {[0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19].map(i => <Icon key={i} />)}

                        </div>
                        :
                        <div className="w-full px-4 overflow-y-auto">
                            <table>
                                <thead>
                                    <tr>
                                        <th className="w-[30px] min-w-[30px]"></th>
                                        <th className="w-[44px] min-w-[44px]">종류</th>
                                        <th className="w-full text-left">이름</th>
                                        <th className="w-[70px] min-w-[70px]">크기</th>
                                        <th className="w-[200px] min-w-[200px] text-right">수정한 날짜</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {[0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19].map(i => <List key={i} />)}
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
