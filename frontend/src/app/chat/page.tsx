"use client";
import { useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";

export default function Home() {
    const[open,setOpen] =useState(false);

    return <Main>
        <div className="w-4/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow">
            <button id="button1" onClick={()=>setOpen(!open)}>필터</button>
            <DropDown open={open} onClose={()=>setOpen(false)} className="" defaultDriection={Direcion.DOWN} width={100} height={100} button="button1"> 
                1
            </DropDown>
            </div>
        </div>
        <div className="w-8/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow">

            </div>
        </div>
    </Main>
}