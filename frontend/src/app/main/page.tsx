"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import { getUser } from "../API/UserAPI";

export default function Home() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => setUser(r)).catch(e => console.log(e));
    }, [ACCESS_TOKEN])

    return <Main user={user}>
        <div className="w-4/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow">

            </div>
        </div>
        <div className="w-8/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-screen shadow">
            </div>
        </div>
    </Main>
}
