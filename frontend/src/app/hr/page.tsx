"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import { getEmail, getUser, mailCancel, mailDelete, readEmail } from "../API/UserAPI";
import { useRouter } from "next/navigation";
import { getDateTime } from "../Global/Method";




export default function Page() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                getEmail(1).then(r => {
                    console.log("---------------.");
                    console.log(r);
                }).catch(e => console.log(e))
            }).catch(e => console.log(e));
    }, [ACCESS_TOKEN])


    return <Main user={user}>
        <div className="w-4/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-2 ">
                <div className="w-full h-30 flex flex-row gap-20 ">
                    부서목록
                </div>
                <div className="h-[88%] overflow-y-scroll">
                    [인사부]
                </div>
            </div>
        </div>
        <div className="w-8/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-full w-11/12 bg-white shadow p-4">
                <table>
                    <thead>
                        <tr>
                            <th>1</th>
                            <th>2</th>
                            <th>3</th>
                            <th>4</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>a</td>
                            <td>b</td>
                            <td>c</td>
                            <td>d</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </Main>
}

