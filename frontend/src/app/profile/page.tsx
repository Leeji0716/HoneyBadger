"use client";

import { useEffect, useRef, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import { deleteProfileImage, getUser, putProfileImage, updatePassword } from "../API/UserAPI";
import { getDateKorean, PhoneString } from "../Global/Method";
import Modal from "../Global/Modal";

export default function HOME() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [hover, setHover] = useState(false);
    const [open, setOpen] = useState(false);
    const [error, setError] = useState('');
    const file = useRef(null as any);
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => setUser(r)).catch(e => console.log(e));
    }, [ACCESS_TOKEN])

    function getRole(role: number) {
        switch (role) {
            case 0: return "사장"
            case 1: return "부사장"
            case 2: return "전무"
            case 3: return "상무"
            case 4: return "이사"
            case 5: return "사외 이사"
            case 6: return "고문"
            case 7: return "감사"
            case 8: return "부장"
            case 9: return "과장"
            case 10: return "대리"
            case 11: return "주임"
            case 12: return "직원"
        }
    }

    return <Main>
        <div className="w-full flex items-center justify-center p-10">
            <div className="w-full bg-white h-full shadow p-2 flex flex-col text-lg items-center">
                <div className="flex items-center mt-10">
                    <div className="flex flex-col items-center">
                        <div className="flex" onMouseEnter={() => setHover(true)} onMouseLeave={() => setHover(false)} onClick={() => { file?.current.click() }}>
                            <img src={user?.url ? user?.url : '/base_profile.png'} alt="profile" className="w-[250px] h-[250px]" />
                            <div className="w-[250px] h-[250px] bg-gray-500 absolute opacity-80" hidden={!hover}></div>
                            <input ref={file} type="file" hidden onChange={e => {
                                const formData = new FormData();
                                if (e.target.files) {
                                    formData.append('file', e.target.files[0])
                                    putProfileImage(formData).then(r => { console.log(r); setUser(r); }).catch(e => console.log(e));
                                }

                            }} />
                        </div>
                        <button className="hover:underline hover:text-red-500 hover:font-bold" onClick={() => deleteProfileImage().then(r => setUser(r)).catch(e => console.log(e))}>프로필 이미지 삭제</button>
                    </div>
                    <table>
                        <tbody className="text-start">
                            <tr>
                                <th className="fotn-bold w-[200px]">id</th>
                                <td>{user?.username}</td>
                            </tr>
                            <tr>
                                <th className="fotn-bold">이름</th>
                                <td>{user?.name}</td>
                            </tr>
                            <tr>
                                <th className="fotn-bold">부서</th>
                                <td>{user?.department?.name ? user?.department?.name : "미할당"}</td>
                            </tr>
                            <tr>
                                <th className="fotn-bold">직책</th>
                                <td>{getRole(user?.role)}</td>
                            </tr>
                            <tr>
                                <th className="fotn-bold">비밀번호</th>
                                <td><button className="hover:underline hover:text-red-500 hover:font-bold" onClick={() => setOpen(true)}>변경하기</button></td>
                            </tr>
                            <tr>
                                <th className="fotn-bold">전화번호</th>
                                <td>{user?.phoneNumber}</td>
                            </tr>
                            <tr>
                                <th className="fotn-bold">입사일</th>
                                <td>{getDateKorean(user?.joinDate)}</td>
                            </tr>
                        </tbody>
                    </table>
                    <Modal open={open} onClose={() => setOpen(false)} escClose={true} outlineClose={true} className="w-[500px] h-[500px] flex flex-col items-center justify-center">
                        <div className="h-[50px] w-[400px] text-center text-lg text-red-500">{error}</div>
                        <input id="pre" type="password" placeholder="현재 비밀번호" className="input input-lg input-info" onKeyDown={e => { if (e.key == "Enter") document.getElementById('new1')?.focus() }} />
                        <input id="new1" type="password" placeholder="새 비밀번호" className="input input-lg input-info my-5" onKeyDown={e => { if (e.key == "Enter") document.getElementById('new2')?.focus() }} />
                        <input id="new2" type="password" placeholder="비밀번호 확인" className="input input-lg input-info" onKeyDown={e => { if (e.key == "Enter") document.getElementById('submit')?.click() }} />
                        <div className="flex justify-between w-[150px] mt-10">
                            <button id="submit" onClick={() => {
                                const prePassword = (document.getElementById('pre') as HTMLInputElement)?.value;
                                if (prePassword == '') {
                                    setError('기존 비밀번호를 입력해주세요.');
                                    return;
                                }

                                const newPassword = (document.getElementById('new1') as HTMLInputElement)?.value;
                                if (newPassword == '') {
                                    setError('새 비밀번호를 입력해주세요.');
                                    return;
                                }
                                if (newPassword != (document.getElementById('new2') as HTMLInputElement)?.value) setError('비밀번호가 일치하지 않습니다.');
                                else {
                                    setError('');
                                    updatePassword(prePassword, newPassword).then(() => {
                                        localStorage.clear();
                                        location.href = "/";
                                    }).catch(e => {
                                        if (e.response.status == 404 && e.response.data == "password") {
                                            setError("기존 비밀번호가 일치하지 않습니다.");
                                        } else
                                            console.log(e);
                                    })
                                }
                            }} className="btn btn-info text-white">확인</button>
                            <button onClick={() => setOpen(false)} className="btn btn-info text-white">취소</button>
                        </div>
                    </Modal>
                </div>
                <div className="my-auto flex w-[850px] justify-between">
                    <div className="border border-black w-[400px] h-[200px] flex flex-col">
                        <div className="flex h-[150px] mt-auto">
                            <div className="mx-auto flex flex-col">
                                <div className="flex flex-col mt-4">
                                    <label className="font-bold text-[#8fbee9]">HoneyBadger</label>
                                    <label className="text-xxs text-center text-gray-500">Don't hold back. Be brave</label>
                                </div>
                                <div className="mt-auto flex flex-col h-[60px]">
                                    <div className="flex  items-center my-auto">
                                        <img src='/_phone.png' className="w-[20px] h-[20px] mr-2" />
                                        <label className="text-xxs">{PhoneString(user?.phoneNumber)}</label>
                                    </div>
                                    <div className="flex items-center my-auto">
                                        <img src='/_call.png' className="w-[20px] h-[20px] mr-2" />
                                        <label className="text-xxs">1312</label>
                                    </div>
                                    <div className="flex items-center my-auto">
                                        <img src='/_mail.png' className="w-[20px] h-[20px] mr-2" />
                                        <label className="text-xxs">{user?.username}@honeybadger.com</label>
                                    </div>
                                </div>
                            </div>
                            <div className="mx-auto flex flex-col">
                                <div className="flex flex-col mt-4">
                                    <label className="font-bold text-[#8fbee9]">{user?.name}</label>
                                    <label className="text-xs text-gray-500">{getRole(user?.role)}</label>
                                </div>

                                <div className="mt-auto flex flex-col h-[60px]">
                                    <div className="flex items-center my-auto">
                                        <img src='/_web.png' className="w-[20px] h-[20px] mr-2" />
                                        <label className="text-xxs">www.벌꿀오소리.메인.한국</label>
                                    </div>
                                    <div className="flex items-center my-auto">
                                        <img src='/_location.png' className="w-[20px] h-[20px] mr-2" />
                                        <label className="text-xxs">대전광역시 서구 둔산로 52 3층</label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="w-full h-[20px] bg-[#8fbee9] mt-auto"></div>
                    </div>
                    <div className="border bg-[#8fbee9] border-black w-[400px] h-[200px] flex flex-col items-center justify-center relative">
                        <div className="flex">
                            <img src='/_logo.png' className="w-[75px] h-[50px] mr-2 bg-yellow-500 rounded-full p-2"/>
                            <div className="flex flex-col">
                                <label className="font-bold text-white text-lg">HoneyBadger</label>
                                <label className="text-xxs text-center text-gray-300">Don't hold back. Be brave</label>
                            </div>
                        </div>
                        <div className="bottom-[16px] absolute text-white text-xs">SEO | Web Devlopement | App Development </div>
                    </div>
                </div>
            </div>
        </div>
    </Main>
}