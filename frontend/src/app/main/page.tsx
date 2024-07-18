"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import { getUser } from "../API/UserAPI";

export default function Home() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [isClientLoading, setClientLoading] = useState(true);
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 100);
            }).catch(e => {setClientLoading(false); console.log(e);});
        else
            location.href = '/';
    }, [ACCESS_TOKEN])

    return <Main user={user} isClientLoading={isClientLoading}>
        <div className="w-4/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-2">
            </div>
        </div>
        <div className="w-8/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-2">
            </div>
        </div>
    </Main>
}
