"use client";
import { getUser } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import { useEffect, useState } from "react";


export default function Approval() {
    interface approvalResponseDTO {
        id?: number,
        title: string,
        status: number,
        sender: string,
        approver: string,
        viewers: string[],
    }

    const [filter, setFilter] = useState(0); //결제 필터 (전체 + status = 총 5개 : 0~4)
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [userList, setUserList] = useState([] as any[])
    const [selectedUsers, setSelectedUsers] = useState(new Set<string>());
    const [isClientLoading, setClientLoading] = useState(true);
    const [keyword, setKeyword] = useState('');

    // 유저 토큰 확인하기
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            location.href = '/';
    }, [ACCESS_TOKEN])

    return <Main user={user} isClientLoading={isClientLoading}>
        <div>hihi</div>
    </Main>

}