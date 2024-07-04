'use client';
import { useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";


export default function email() {
    const [open, setOpen] = useState(false);
    const [open1, setOpen1] = useState(false);
    return <Main>
        <div className="w-4/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow">
                <div className="w-full h-30 flex flex-row gap-10 justify-center">
                    <button id="button1" onClick={() => setOpen(!open)}>받은 메일</button>
                    <DropDown open={open} onClose={() => setOpen(false)} className="" defaultDriection={Direcion.DOWN} width={200} height={200} button="button1">
                        1
                    </DropDown>
                    <button id="button2" onClick={() => setOpen1(!open1)}>받은 메일</button>
                    <DropDown open={open1} onClose={() => setOpen1(false)} className="" defaultDriection={Direcion.DOWN} width={200} height={200} button="button2">
                        1
                    </DropDown>
                </div>
            </div>
        </div>
        <div className="w-8/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow">

            </div>
        </div>
    </Main>

}