"use client";

import { useEffect, useState } from "react";
import { getUser } from "../API/UserAPI";
import Main from "../Global/Layout/MainLayout";
import { getDateKorean, getSoloarToLunarDate } from "../Global/Method";

export default function Home() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [isClientLoading, setClientLoading] = useState(true);

    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r); const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
            }).catch(e => {
                setClientLoading(false);
                console.log(e);
            });
        else
            location.href = "/"
    }, [ACCESS_TOKEN])

    const lunar = getSoloarToLunarDate(new Date().getTime());
    return <Main user={user} isClientLoading={isClientLoading} >
        <div className="flex flex-col">
            <div>{getDateKorean(new Date().getTime())}</div>
            <div>{getDateKorean(lunar.getTime())}</div>
        </div>
    </Main>
}