'use client';
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import { getDate } from "../Global/Method";

interface EmailResponseDTO {
    id: number,
    title: string,
    content: string,
    senderId: number,
    semderName: string,
    receiverIds: string[]
}

interface EmailList {
    mail: EmailResponseDTO[]
}


export default function Email() {
    const [open, setOpen] = useState(false);
    const [open1, setOpen1] = useState(false);
    const [user, setUser] = useState(null as any);


    function MailBox({ title, content, date }: { title: string, content: string, date: number }) {
        return <div className="w-11/12 h-[70px] ml-2 mt-4 flex hover:bg-gray-300">
            <div className="h-full w-[6px] official-color mr-2"></div>
            <div className="w-[60px] h-full flex items-center">
                <img className="rounded-full w-[40px] h-[40px]" src="/hui.jpg" alt="후잉~" />
            </div>
            <div className="w-full h-full ml-4 flex flex-col justify-between">
                <div className="flex w-full justify-between h-1/3">
                    <p className="text-sm font-bold">한성언</p>
                    <p className="">파일</p>
                </div>
                <div className="flex flex-row w-full h-1/3">
                    <p className="text-blue-400 font-bold text-base">{title}</p>
                    <p className="flex ml-auto text-gray-500 text-sm">{getDate(date)}</p>
                </div>
                <div className="h-1/3" >
                    <p className="text-sm">{content}</p>
                </div>
            </div>
        </div>
    }

    function MailDetail() {

        return <div>
            <div className="w-full h-[18%] border-t-2 pt-2">
                <h2 className="text-2xl font-semibold">안녕하세용</h2>
                <div className="flex justify-start items-center gap-5 mt-2 mb-2 pt-2">
                    <p className="text-sm">보낸사람</p>
                    <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm">
                        gkstjddjs08@naver.com
                    </button>
                </div>
                <div className="flex justify-start items-center mb-2 gap-5">
                    <p className="text-sm">받는사람</p>
                    <button className="inline-flex bg-blue-200 rounded-full text-white font-bold px-4 py-2 text-sm">
                        하용
                    </button>
                </div>
                <p className="text-sm">2024.07.05 오후 2:21</p>
            </div>
            <div className="w-full h-[60%] border-t-2 flex justify-center mt-2 p-4">
                <p className="w-[80%] font-bold">dkwdisgdrfiogjdfpogjerod'igjdflkgjdrt;oigjdft;klgjdfoigjerdfklgjerto;idklfgjboeritdfgjklmroidtjf
                    rtjohkigflmtriohjpkgltryjipohkglmrtjopklgmfhrjotpkrthjko'l;htpkroflg;fgkopl;,hpkortl;fjioptfmkhlgjfthkgmfthjklmg,
                    thirjokgfltjirhokgflrtihjopkgfrejiogfkdljrgiokdflergjiofkdlgeriojfkdlrgjieodkflrejiogfdklerigjodkflregijdfkrgjioedfkrijgkdf
                    girjdkfrgeiojkdfioergjkdflergiojdfklergjiodkflrdiogjklfgrdjilkidrjgofklrdgiojfklrgijdkflregjoidflkrdojigflkrdogifjklrdojigfk
                </p>
            </div>
        </div>
    }

    const test = { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 };
    const test2 = [{ title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }, { title: "디아블로 확장팩 사게 오만..", content: "안녕하셍쇼", date: 1730000000000 }]
    return <Main>
        <div className="w-4/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow p-2">
                <div className="w-full h-30 flex flex-row gap-20 ">
                    <button id="button1" onClick={() => setOpen(!open)}>받은 메일</button>
                    <DropDown open={open} onClose={() => setOpen(false)} className="" defaultDriection={Direcion.DOWN} width={200} height={200} button="button1">
                        <button className="bg-white">중요</button>
                        <button className="bg-white">태그</button>
                        <button className="bg-white">태그</button>
                    </DropDown>
                    <button id="button2" className="" onClick={() => setOpen1(!open1)}>받은 메일</button>
                    <DropDown open={open1} onClose={() => setOpen1(false)} className="" defaultDriection={Direcion.DOWN} width={200} height={200} button="button2">
                        <button>중요</button>
                        <button>태그</button>
                        <button>태그</button>
                    </DropDown>
                </div>
                <div className="h-[88%] overflow-y-scroll">
                    <MailBox title="반가워용용용용용" content="안녕하셍쇼" date={1730000000000} />
                    <MailBox title={"반가워용용용용용"} content={test.content} date={test.date} />
                    {test2.map((t, index) => <MailBox key={index} title={"안녕하세용용용용용"} content={t.content} date={t.date} />)}
                </div>

            </div>
        </div>
        <div className="w-8/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow p-4">
                <MailDetail/>
            </div>
        </div>
        <div>
        </div>
    </Main>
}

