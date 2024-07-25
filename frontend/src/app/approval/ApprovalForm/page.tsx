"use client";
import { createApproval, getUser, getUsers } from "@/app/API/UserAPI";
import DropDown, { Direcion } from "@/app/Global/DropDown";
import Main from "@/app/Global/Layout/MainLayout";
import { getRole } from "@/app/Global/Method";
import { use, useEffect, useState } from "react";


export default function Approval() {

    interface approvalRequestDTO {
        title: string,
        content: string,
        sender: string,
        approversname: string[],
        viewersname: string[]
    }

    const [filter, setFilter] = useState(0); //결제 필터 (전체 + status = 총 5개 : 0~4)
    const [user, setUser] = useState(null as any); //현재 유저
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [userList, setUserList] = useState([] as any[])
    const [isClientLoading, setClientLoading] = useState(true);
    const [keyword, setKeyword] = useState('');
    const [nowDate, setNowDate] = useState("");
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [selectedApprovar, setSelectedApprovar] = useState([] as any[]);
    const [selectedViewer, setSelectedViewer] = useState([] as any[]);
    const [settingOpen, setSettingOpen] = useState(false);
    const [zeroOpen, setZeroOpen] = useState(false);
    const [oneOpen, setOneOpen] = useState(false);
    const [twoOpen, setTwoOpen] = useState(false);
    const [threeOpen, setThreeOpen] = useState(false);
    const [fourOpen, setFourOpen] = useState(false);
    const [fiveOpen, setFiveOpen] = useState(false);
    const [sixOpen, setSixOpen] = useState(false);
    const [sevenOpen, setSevenOpen] = useState(false);
    const [eightOpen, setEightOpen] = useState(false);
    const selectedViewersText = selectedViewer.map(viewer => viewer.name).join(', ');
    const [fileList, setFileList] = useState<File[]>([]);

    //users 가져오기
    useEffect(() => {
        const fetchData = async () => {
            try {
                const usersData = await getUsers();
                setUserList(usersData);
            } catch (error) {
                console.error(error);
            }
        };
        fetchData();
    }, []);

    //승인 유저 추가 & 승인 유저 제거
    const handleUserSelect = (user: { username: any; }, approvarIndex: number) => {
        setSelectedApprovar((prevSelectedApprovar) => {
            const newSelectedApprovar = [...prevSelectedApprovar];
            // 인덱스가 배열 길이보다 크면 배열 길이만큼 null을 채워서 확장
            while (newSelectedApprovar.length <= approvarIndex) {
                newSelectedApprovar.push(null);
            }
            // 해당 인덱스에 유저 할당
            newSelectedApprovar[approvarIndex] = user;
            return newSelectedApprovar;
        });
    };

    // 승인 유저 선택
    const renderUserList = (approvarIndex: number) => {
        const filteredUserList = userList.filter(user =>
            !selectedApprovar.some(selected => selected && selected.username === user.username) &&
            !selectedViewer.some(selected => selected && selected.username === user.username)
        );
        return (
            <ul>
                {filteredUserList.map((user, index) => (

                    <li
                        key={index}
                        onClick={() => handleUserSelect(user, approvarIndex)}
                        className="font-bold hover:underline cursor-pointer"
                    >
                        {user.name} - {user.username}
                    </li>
                ))}
            </ul>
        );
    };

    // 승인자 인덱스로 찾기
    const getSpecificApprover = (index: number) => {
        if (index >= 0 && index < selectedApprovar.length) {
            return selectedApprovar[index];
        }
        return null; // 인덱스가 범위를 벗어난 경우 null 반환
    };

    // 참조인 유저 선택
    const renderUsersList = () => {
        const filteredUserList = userList.filter(user =>
            !selectedApprovar.some(selected => selected && selected.username === user.username)
        );
        return (
            <ul>
                {filteredUserList.map((user, index) => (

                    <li
                        key={index}
                        onClick={() => handleUsersSelect(user)}
                        className="font-bold hover:underline cursor-pointer"
                    >
                        {user.name} - {user.username}
                    </li>
                ))}
            </ul>
        );
    };

    // 참조인 제거
    const handleInputChange = (event: any) => {
        const inputValue = event.target.value;
        const selectedUserNames = inputValue.split(',').map((username: string) => username.trim());

        setSelectedViewer((prevSelectedViewers) =>
            prevSelectedViewers.filter(user => selectedUserNames.includes(user.name))
        );
    };

    //참조인 추가 & 참조인 제거
    const handleUsersSelect = (user: { username: any; }) => {
        setSelectedViewer((prevSelectedViewers) => {
            const isUserSelected = prevSelectedViewers.find((u) => u.username === user.username);
            if (isUserSelected) {
                // 이미 선택된 유저가 있으면 배열에서 제거
                return prevSelectedViewers.filter((u) => u.username !== user.username);
            }
            return [...prevSelectedViewers, user];
        });
        setSettingOpen(false); // 드롭다운을 닫음
    };

    // 현재 날짜 가져오기
    useEffect(() => {
        const currentDate = new Date();
        const formattedDate = currentDate.toISOString().split('T')[0]; // YYYY-MM-DD 형식으로 포맷
        setNowDate(formattedDate);
    }, []);

    // 유저 토큰 확인하기
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                console.log(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            location.href = '/';
    }, [ACCESS_TOKEN])

    // 결재 만들기
    const handleCreateApproval = () => {
        if (selectedApprovar && title) {
            const approver = selectedApprovar.map((user) => user?.username);
            const viewer = selectedViewer.map((user) => user.username);

            const approvalRequest: approvalRequestDTO = { title: title, content: content, sender: user.username, approversname: approver, viewersname: viewer };
            createApproval(approvalRequest)
                .then(r => {
                    console.log("이거는 생성"+r);
                    window.location.href = "/approval"
                })
                .catch(e => {
                    console.error(e);
                });
        } else {
            console.error("제목이나 승인자가 없습니다.");
        }
    };

    const sliceText = (text: string) => {
        const slice: string[] = text.split(".");
        const extension: string = slice[slice.length - 1];

        return extension;
    }

    //페이지 시작//
    return <Main user={user} isClientLoading={isClientLoading}>
        <div className="w-full flex items-center justify-center h-full pt-10 pb-4">
            <div className="w-11/12 h-full bg-white shadow flex flex-col justify-center items-center gap-2 ">
                <h1 className="font-bold text-2xl flex items-center h-[50px]">결재 작성</h1>
                <div className="w-11/12 h-[90%]">
                    <div className="flex flex-row justify-end">
                        <a href="#" className="btn official-color w-[100px] h-[30px]" onClick={() => handleCreateApproval()}>보내기</a>
                    </div>
                    {/* 작성자 및 결재 승인자 정보 */}
                    <div className="flex flex-wrap w-full">
                        {/* 작성자 정보 */}
                        <div className="w-[20%] h-[200px] border-2 border-red-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300">
                                <label htmlFor="senderDepartment" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안부서</label>
                                <div className="w-[50%] flex justify-center items-center">
                                    <p id="senderDepartment">{user?.department?.name ? user?.department?.name : "미할당"}</p>
                                </div>
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300">
                                <label htmlFor="senderName" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안자</label>
                                <div className="w-[50%] flex justify-center items-center">
                                    <p id="senderName">{user?.name}</p>
                                </div>
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300">
                                <label htmlFor="senderRole" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">직책</label>
                                <div className="w-[50%] flex justify-center items-center">
                                    <p id="senderRole">{getRole(user?.role)}</p>
                                </div>
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300">
                                <label htmlFor="sendDate" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안일</label>
                                <div className="w-[50%] flex justify-center items-center">
                                    <p id="sendDate">{nowDate}</p>
                                </div>
                            </div>
                        </div>

                        {/* 결재 승인자 정보 */}
                        <div className="w-[20%] h-[200px] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(0)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(0)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectZero" onClick={() => setZeroOpen(!zeroOpen)}>
                                <DropDown
                                    open={zeroOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(0)}
                                    className={"h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectZero"}
                                />
                            </div>
                        </div>
                        <div className="w-[20%] h-[200px] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(1)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(1)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectOne" onClick={() => setOneOpen(!oneOpen)}>
                                <DropDown
                                    open={oneOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(1)}
                                    className={"h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectOne"}
                                />
                            </div>
                        </div>
                        <div className="w-[20%] h-[200px] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(2)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(2)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectTwo" onClick={() => setTwoOpen(!twoOpen)}>
                                <DropDown
                                    open={twoOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(2)}
                                    className={"h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectTwo"}
                                />
                            </div>
                        </div>
                        <div className="w-[20%] h-[200px] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(3)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(3)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectThree" onClick={() => setThreeOpen(!threeOpen)}>
                                <DropDown
                                    open={threeOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(3)}
                                    className={"h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectThree"}
                                />
                            </div>
                        </div>
                        <div className="w-[20%] h-[200px] border-l-2 border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center" >
                                {getRole(getSpecificApprover(4)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(4)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectFour" onClick={() => setFourOpen(!fourOpen)}>
                                <DropDown
                                    open={fourOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(4)}
                                    className={"h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectFour"}
                                />
                            </div>
                        </div>
                        <div className="w-[20%] h-[200px] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(5)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(5)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectFive" onClick={() => setFiveOpen(!fiveOpen)}>
                                <DropDown
                                    open={fiveOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(5)}
                                    className={"h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectFive"}
                                />
                            </div>
                        </div>
                        <div className="w-[20%] h-[200px] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(6)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(6)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectSix" onClick={() => setSixOpen(!sixOpen)}>
                                <DropDown
                                    open={sixOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(6)}
                                    className={"h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectSix"}
                                />
                            </div>
                        </div>
                        <div className="w-[20%] h-[200px] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(7)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(7)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectSeven" onClick={() => setSevenOpen(!sevenOpen)}>
                                <DropDown
                                    open={sevenOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(7)}
                                    className={"h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectSeven"}
                                />
                            </div>
                        </div>
                        <div className="w-[20%] h-[200px] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(8)?.role)}
                            </div>
                            <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(8)?.name}
                            </div>
                            <div className="w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center" id="selectEight" onClick={() => setEightOpen(!eightOpen)}>
                                <DropDown
                                    open={eightOpen}
                                    onClose={() => setSettingOpen(false)}
                                    children={renderUserList(8)}
                                    className={"w-full h-[300px] overflow-y-scroll bg-white"}
                                    width={200}
                                    height={100}
                                    defaultDriection={Direcion.DOWN}
                                    button={"selectEight"}
                                />
                            </div>
                        </div>
                    </div>

                    <div className="w-full h-[50px] flex flex-row justify-center border-b-2 border-gray-300">
                        <label htmlFor="title" className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">제목</label>
                        <input type="text" id="title" className="w-[90%] border-r-2 border-gray-300 pl-5" placeholder="제목을 입력해주세요."
                            defaultValue={title} onChange={(e) => { setTitle(e.target.value); }} />
                    </div>
                    <div className="w-full h-[50px] flex flex-row justify-center border-b-2 border-gray-300">
                        <label htmlFor="content" className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">내용</label>
                        <input type="text" id="content" className="w-[90%] border-r-2 border-gray-300 pl-5" placeholder="내용을 입력해주세요."
                            defaultValue={content} onChange={(e) => { setContent(e.target.value); }} />
                    </div>
                    <div className="w-full h-[50px] flex flex-row justify-center border-b-2 border-gray-300">
                        <label htmlFor="selectViewer" className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">참조인</label>
                        <input type="text" id="selectViewer" className="w-[90%] border-r-2 border-gray-300 pl-5" placeholder="참조인을 선택해주세요."
                            value={selectedViewersText} onClick={() => setSettingOpen(!settingOpen)} onChange={(e) => handleInputChange(e)} />
                        <DropDown
                            open={settingOpen}
                            onClose={() => setSettingOpen(false)}
                            children={renderUsersList()}
                            className={"w-full h-[300px] overflow-y-scroll"}
                            width={200}
                            height={100}
                            defaultDriection={Direcion.DOWN}
                            button={"selectViewer"}
                        />
                    </div>

                    <div className="relative w-full h-[150px] border border-gary-500 overflow-y-scroll border-r-2 border-l-2 border-b-2 border-gray-300">
                        <button className="btn btn-sm absolute top-[5px] right-[5px]">파일 선택</button>
                        {/* <img src="/plus.png" alt="" className="w-[30px] h-[30px] absolute top-[5px] right-[5px] cursor-pointer" ></img> */}
                    </div>

                    {fileList.length != 0 ? fileList.map((f: File, index: number) => <ul key={index}>
                        <div className="flex items-center bg-white p-2">
                            <img src="/x.png" alt="" className="mr-2  w-[26px] h-[31px] cursor-pointer" onClick={() => { const removeFile = [...fileList]; removeFile.splice(index, 1); setFileList(removeFile); }}></img>
                            <img src={"/" + sliceText(f.name) + ".PNG"} className="w-[26px] h-[31px] mr-2" alt="" />
                            <p>{f.name}</p>
                        </div>
                    </ul>) : <></>}
                </div>
            </div>
        </div>
    </Main >

}