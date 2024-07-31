"use client";
import { approvalFiles, createApproval, getUser, getUsers } from "@/app/API/UserAPI";
import DropDown, { Direcion } from "@/app/Global/DropDown";
import Main from "@/app/Global/Layout/MainLayout";
import { getFileIcon, getRole, sliceText } from "@/app/Global/Method";
import { useEffect, useState } from "react";

export default function Approval() {
    interface approvalRequestDTO {
        title: string,
        content: string,
        sender: string,
        approversname: string[],
        viewersname: string[]
    }

    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [user, setUser] = useState(null as any); //현재 유저
    const [userList, setUserList] = useState([] as any[])
    const [isClientLoading, setClientLoading] = useState(true);
    const [nowDate, setNowDate] = useState("");
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [selectedApprover, setSelectedApprover] = useState([] as any[]);
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

    // 현재 날짜 가져오기
    useEffect(() => {
        const currentDate = new Date();
        const formattedDate = currentDate.toISOString().split('T')[0]; // YYYY-MM-DD 형식으로 포맷
        setNowDate(formattedDate);
    }, []);

    //userList 가져오기
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

    // 승인 유저 dropdown으로 선택하기
    function SelectApprover({ index, modal, id }: { index: number, modal: any, id: string }) {
        return <>
            {getSpecificApprover(index) == null ? <label className="cursor-pointer hover:underline">선택</label> : <></>}
            <DropDown
                open={modal}
                onClose={() => setSettingOpen(false)}

                className={"h-[18.75rem] overflow-y-scroll bg-white"}
                width={200}
                height={100}
                defaultDriection={Direcion.DOWN}
                button={id}
            >
                {renderUserList(index)}
            </DropDown>
        </>
    }

    // 승인 유저 추가 & 승인 유저 제거
    const handleUserSelect = (user: { username: any; }, approvarIndex: number) => {
        setSelectedApprover((prevSelectedApprover) => {
            const newSelectedApprovar = [...prevSelectedApprover];
            // 인덱스가 배열 길이보다 크면 배열 길이만큼 null을 채워서 확장
            while (newSelectedApprovar.length <= approvarIndex) {
                newSelectedApprovar.push(null);
            }
            // 해당 인덱스에 유저 할당
            newSelectedApprovar[approvarIndex] = user;
            return newSelectedApprovar;
        });
    };

    // 승인 유저 제거
    const handleIndexChange = (index: number) => {
        const target = selectedApprover[index];
        if (!target) {
            return;
        }

        setSelectedApprover((prevSelectedApprovers) => {
            const newSelectedApprovers = [...prevSelectedApprovers];
            newSelectedApprovers[index] = null; // 해당 인덱스의 요소를 null로 설정
            return newSelectedApprovers;
        });
    };

    // 승인 유저 선택
    const renderUserList = (approvarIndex: number) => {
        const filteredUserList = userList.filter(user =>
            !selectedApprover.some(selected => selected && selected.username === user.username) &&
            !selectedViewer.some(selected => selected && selected.username === user.username)
        );
        return (
            <ul>
                {filteredUserList.map((user, index) => (
                    <li
                        key={index}
                        onClick={() => handleUserSelect(user, approvarIndex)}
                        className="font-bold hover:underline cursor-pointer">
                        {user.name} - {user.username}
                    </li>
                ))}
            </ul>
        );
    };

    // 승인 유저 인덱스로 찾기
    const getSpecificApprover = (index: number) => {
        if (index >= 0 && index < selectedApprover.length) {
            return selectedApprover[index];
        }
        return null; // 인덱스가 범위를 벗어난 경우 null 반환
    };

    // 참조 유저 선택
    const renderUsersList = () => {
        const filteredUserList = userList.filter(user =>
            !selectedApprover.some(selected => selected && selected.username === user.username) &&
            !selectedViewer.some(selected => selected && selected.username === user.username)
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

    // 참조 유저 제거
    const handleInputChange = (event: any) => {
        const inputValue = event.target.value;
        const selectedUserNames = inputValue.split(',').map((username: string) => username.trim());

        setSelectedViewer((prevSelectedViewers) =>
            prevSelectedViewers.filter(user => selectedUserNames.includes(user.name))
        );
    };

    //참조 유저 추가 & 제거
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

    // 결재 만들기
    const handleCreateApproval = () => {
        if (selectedApprover && title) {
            const approver = selectedApprover.map(user => user?.username ?? null);
            let targetIndex = null;
            for (let i = approver.length - 1; i >= 0; i--) {
                if (approver[i] !== null) {
                    targetIndex = i;
                    break;
                }
            }
            const finalApprover = [];
            if (targetIndex !== null) {
                for (let i = 0; i <= targetIndex; i++) {
                    finalApprover.push(approver[i]);
                }
            }
            if (finalApprover.includes(null)) {
                window.confirm('승인자가 차례대로 작성되어있는지 확인해주세요.');
            } else {
                const viewer = selectedViewer.map(user => user.username);

                const approvalRequest: approvalRequestDTO = { title: title, content: content, sender: user.username, approversname: finalApprover, viewersname: viewer };
                const form = new FormData();
                fileList.forEach(file => form.append('attachments', file));
                if (fileList.length == 0) {
                    createApproval(approvalRequest).then(r => window.location.href = "/approval").catch(e => console.log(e));
                } else {
                    createApproval(approvalRequest)
                        .then(response => {
                            return approvalFiles({ attachments: form, approvalId: response.id })
                                .then((r) => {
                                    console.log(r);
                                    window.location.href = "/approval";
                                })
                                .catch(error => {
                                    console.error("파일 에러", error);
                                });
                        })
                        .catch(error => {
                            console.error("An error occurred in createApproval:", error);
                        });
                }
            }
        } else {
            console.error("제목이나 승인자가 없습니다.");
            window.confirm('각 항목이 모두 입력되었는지 확인해주세요.')
        }
    };

    //페이지
    return <Main user={user} isClientLoading={isClientLoading}>
        <div className="w-full flex items-center justify-center h-full pt-10 pb-12">
            <div className="w-11/12 h-full bg-white shadow flex flex-col justify-center items-center gap-2 ">
                <div className="flex justify-center items-center w-full relative">
                    <h1 className="font-bold text-2xl flex items-center h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem]">결재 작성</h1>
                    <div className="right-16 absolute">
                        <a href="#" className="text-white btn btn-sm official-color w-[6.25rem] h-[1.875rem] mr-1" onClick={() => handleCreateApproval()}>보내기</a>
                        <a href="#" className="text-white btn btn-sm btn-error w-[6.25rem] h-[1.875rem]" onClick={() => window.location.href="/approval"}>취소</a>
                    </div>
                </div>
                <div className="w-11/12 h-[90%]">


                    {/* 작성자 및 결재 승인자 정보 */}
                    <div className="flex flex-wrap w-full">
                        {/* 작성자 정보 */}
                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-2 border-red-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300">
                                <label htmlFor="senderDepartment" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안부서</label>
                                <div className="w-[50%] flex justify-center items-center">
                                    <p id="senderDepartment">{user?.department?.name ? user?.department?.name : "미할당"}</p>
                                </div>
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300">
                                <label htmlFor="senderName" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안자</label>
                                <div className="w-[50%] flex justify-center items-center">
                                    <p id="senderName">{user?.name}</p>
                                </div>
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300">
                                <label htmlFor="senderRole" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">직책</label>
                                <div className="w-[50%] flex justify-center items-center">
                                    <p id="senderRole">{getRole(user?.role)}</p>
                                </div>
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300">
                                <label htmlFor="sendDate" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안일</label>
                                <div className="w-[50%] flex justify-center items-center">
                                    <p id="sendDate">{nowDate}</p>
                                </div>
                            </div>
                        </div>

                        {/* 결재 승인자 정보 */}
                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(0)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(0)}>
                                {getSpecificApprover(0)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectZero"
                                onClick={() => setZeroOpen(!zeroOpen)}>
                                <SelectApprover index={0} modal={zeroOpen} id={"selectZero"} />
                            </div>
                        </div>

                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(1)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(1)}>
                                {getSpecificApprover(1)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectOne"
                                onClick={() => setOneOpen(!oneOpen)}>
                                <SelectApprover index={1} modal={oneOpen} id={"selectOne"} />
                            </div>
                        </div>

                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(2)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(2)}>
                                {getSpecificApprover(2)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectTwo"
                                onClick={() => setTwoOpen(!twoOpen)}>
                                <SelectApprover index={2} modal={twoOpen} id={"selectTwo"} />
                            </div>
                        </div>

                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(3)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(3)}>
                                {getSpecificApprover(3)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectThree"
                                onClick={() => setThreeOpen(!threeOpen)}>
                                <SelectApprover index={3} modal={threeOpen} id={"selectThree"} />
                            </div>
                        </div>

                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-l-2 border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center" >
                                {getRole(getSpecificApprover(4)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(4)}>
                                {getSpecificApprover(4)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectFour"
                                onClick={() => setFourOpen(!fourOpen)}>
                                <SelectApprover index={4} modal={fourOpen} id={"selectFour"} />
                            </div>
                        </div>

                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(5)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(5)}>
                                {getSpecificApprover(5)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectFive"
                                onClick={() => setFiveOpen(!fiveOpen)}>
                                <SelectApprover index={5} modal={fiveOpen} id={"selectFive"} />
                            </div>
                        </div>

                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(6)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(6)}>
                                {getSpecificApprover(6)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectSix"
                                onClick={() => setSixOpen(!sixOpen)}>
                                <SelectApprover index={6} modal={sixOpen} id={"selectSix"} />
                            </div>
                        </div>

                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(7)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(7)}>
                                {getSpecificApprover(7)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectSeven"
                                onClick={() => setSevenOpen(!sevenOpen)}>
                                <SelectApprover index={7} modal={sevenOpen} id={"selectSeven"} />
                            </div>
                        </div>

                        <div className="w-[20%] h-[12.5rem] HD:h-[10rem] SD:h-[8rem] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(8)?.role)}
                            </div>
                            <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex border-b-2 border-gray-300 justify-center items-center cursor-pointer hover:line-through hover:font-bold" onClick={() => handleIndexChange(8)}>
                                {getSpecificApprover(8)?.name}
                            </div>
                            <div className="w-full h-[6.25rem] HD:h-[5rem] SD:h-[4rem] flex border-b-2 border-gray-300 justify-center items-center text-gray-500" id="selectEight"
                                onClick={() => setEightOpen(!eightOpen)}>
                                <SelectApprover index={8} modal={eightOpen} id={"selectEight"} />
                            </div>
                        </div>
                    </div>

                    {/* 제목 & 내용 & 참조인 */}
                    <>
                        <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex flex-row justify-center border-b-2 border-gray-300">
                            <label htmlFor="title" className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">제목</label>
                            <input type="text" id="title" className="w-[90%] border-r-2 border-gray-300 pl-5" placeholder="제목을 입력해주세요."
                                defaultValue={title} onChange={(e) => { setTitle(e.target.value); }} />
                        </div>
                        <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex flex-row justify-center border-b-2 border-gray-300">
                            <label htmlFor="content" className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">내용</label>
                            <input type="text" id="content" className="w-[90%] border-r-2 border-gray-300 pl-5" placeholder="내용을 입력해주세요."
                                defaultValue={content} onChange={(e) => { setContent(e.target.value); }} />
                        </div>
                        <div className="w-full h-[3.125rem] HD:h-[2.5rem] SD:h-[2rem] flex flex-row justify-center border-b-2 border-gray-300">
                            <label htmlFor="selectViewer" className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">참조인</label>
                            <input type="text" id="selectViewer" className="w-[90%] border-r-2 border-gray-300 pl-5" placeholder="참조인을 선택해주세요."
                                value={selectedViewersText} onClick={() => setSettingOpen(!settingOpen)} onChange={(e) => handleInputChange(e)} />
                            <DropDown
                                open={settingOpen}
                                onClose={() => setSettingOpen(false)}

                                className={"w-full h-[18.75rem] overflow-y-scroll"}
                                width={200}
                                height={100}
                                defaultDriection={Direcion.DOWN}
                                button={"selectViewer"}
                            >
                                {renderUsersList()}
                            </DropDown>
                        </div>
                    </>

                    {/* 파일 */}
                    <>
                        <div className="flex w-[87.5rem] gap-5">
                            <input id="file" hidden type="file" multiple className="border-b-2" onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                                if (event.target.files) {
                                    const filesArray = Array.from(event.target.files);
                                    setFileList((prevFiles) => [...prevFiles, ...filesArray]);
                                }
                            }} />
                        </div>
                        <div className="w-full h-[21%] border border-gray-300 border-r-2 border-l-2 border-b-2 border-gray-300 flex flex-col flex-wrap overflow-x-scroll relative">
                            <img src="/plus.png" alt="" className="w-[1.875rem] h-[1.875rem] absolute bottom-0 right-5 cursor-pointer"
                                onClick={() => document.getElementById('file')?.click()}></img>
                            {fileList.length !== 0 && fileList.map((f: File, index: number) => (
                                <ul key={index}>
                                    <div className="flex items-center bg-white p-2 w-[31.25rem]">
                                        <img src="/x.png" alt="" className="mr-2 w-[1.625rem] h-[1.9375rem] cursor-pointer"
                                            onClick={() => {
                                                const removeFile = [...fileList];
                                                removeFile.splice(index, 1);
                                                setFileList(removeFile);
                                            }}></img>
                                        <img src={getFileIcon(f.name)} className="w-[1.625rem] h-[1.9375rem] mr-2" alt="" />
                                        <p>{sliceText(f.name)}</p>
                                    </div>
                                </ul>
                            ))}
                        </div>
                    </>
                </div>
            </div>
        </div>
    </Main >

}