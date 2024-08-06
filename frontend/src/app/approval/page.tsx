"use client";
import { acceptApproval, createApproval, deleteApproval, getApprovalList, getUser, getUsers, readApproval, updateViewer } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import { useEffect, useRef, useState } from "react";
import { getFileIcon, getjyDate, getRole, sliceText } from "../Global/Method";
import Modal from "../Global/Modal";


export default function Approval() {
    interface approvalRequestDTO {
        title: string,
        content: string,
        sender: string,
        approversname: string[],
        viewersname: string[]
    }

    interface approvalResponseDTO {
        id: number,
        title: string,
        content: string,
        files: originFileResponseDTO[],
        sender: userResponseDTO,
        approvers: approverResponseDTO[],
        viewers: userResponseDTO[],
        approvalStatus: number,
        readUsers: string[],
        sendDate: number
    }

    interface approverResponseDTO {
        approver: userResponseDTO,
        approverStatus: number,
        approvalDate: number
    }

    interface userResponseDTO {
        username: string,
        name: string,
        phoneNumber: string,
        role: number,
        createDate: number,
        joinDate: number,
        url: string,
        DepartmentResponseDTO: DepartmentResponseDTO
    }

    interface DepartmentResponseDTO {
        name: string
    }

    interface originFileResponseDTO {
        key: string
        original_name: string,
        value: string
    }

    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [page, setPage] = useState(0);
    const [maxPage, setMaxPage] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [filter, setFilter] = useState(-1); // Approval 필터
    const [user, setUser] = useState(null as any);
    const [isClientLoading, setClientLoading] = useState(true);
    const [keyword, setKeyword] = useState('');
    const [approval, setApproval] = useState<approvalResponseDTO>(null as any);
    const [approvalList, setApprovalList] = useState<approvalResponseDTO[]>([]);
    const [fileList, setFileList] = useState<originFileResponseDTO[]>([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [userList, setUserList] = useState([] as any[])
    const appBoxRef = useRef<HTMLDivElement>(null);

    // 유저 토큰 확인하기
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
                getApprovalList(keyword, 0).then(r => { setApprovalList(r.content); setMaxPage(r.totalPages) }).catch(e => console.log(e));
                // getApprovalList(keyword, 0).then(r => setApprovalList(r)).catch(e => console.log(e));
            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            location.href = '/';
    }, [ACCESS_TOKEN])

    const loadPage = () => {
        const appBox = appBoxRef.current;

        if (appBox != null) {
            const scrollLocation = appBox?.scrollTop;
            const maxScroll = appBox.scrollHeight - appBox.clientHeight;
            console.log("=======");
            console.log(scrollLocation);
            console.log("maxScroole");
            console.log(maxScroll);
            if (!isLoading && scrollLocation >= maxScroll - 1 && page < maxPage - 1) {
                setIsLoading(true);
                getApprovalList(keyword, page + 1).then(r => {
                    if (r.size > 0) {
                        setPage(page + 1);
                        const List = [...approvalList, ...r.content];
                        setApprovalList(List);
                        setMaxPage(r.totalPages);
                    }
                    setIsLoading(false);
                }).catch(e => { console.log(e); setIsLoading(false); });
            }
        }
    };


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

    // 검색
    const handleSearch = () => {
        getApprovalList(keyword, 0).then(r => {
            setApprovalList(r.content);
        }).catch(error => {
            console.error("Search error:", error);
        });
    };

    // Approval 상태 매핑 함수
    const getStatusText = (status: number): string => {
        switch (status) {
            case 0:
                return "결재 대기중..";
            case 1:
                return "결재 중";
            case 2:
                return "허가";
            case 3:
                return "반환";
            default:
                return "전체";
        }
    };

    // Approval 상태 색상 매핑 함수
    const getStatusColor = (status: number): string => {
        switch (status) {
            case 0:
                return "text-yellow-500"; // 결재 대기중
            case 1:
                return "text-blue-500"; // 결재 중
            case 2:
                return "text-green-500"; // 허가
            case 3:
                return "text-red-500"; // 반환
            default:
                return "text-black"; // 전체
        }
    };

    // Approver 승인자 상태 매핑 함수
    const getApprovarStatusText = (status: number): string => {
        switch (status) {
            case 0:
                return "결재 대기중..";
            case 1:
                return "결재 중";
            case 2:
                return "허가";
            case 3:
                return "반환";
            default:
                return "";
        }
    };

    // Approver 승인자 색상 매핑 함수
    const getApprovarStatusColor = (status: number): string => {
        switch (status) {
            case 0:
                return "text-yellow-500"; // 결재 대기중
            case 1:
                return "text-blue-500"; // 결재 중
            case 2:
                return "text-green-500"; // 허가
            case 3:
                return "text-red-500"; // 반환
            default:
                return "text-black"; // 알 수 없음
        }
    };

    // 승인자 인덱스로 찾기
    const getSpecificApprover = (index: number) => {
        if (index >= 0 && index < approval.approvers.length) {
            return approval.approvers[index];
        }
        return null; // 인덱스가 범위를 벗어난 경우 null 반환
    };

    // 필터링 버튼
    const renderFilterButton = (filterValue: number, label: string) => {
        const isActive = filter === filterValue;

        return (
            <div className={`flex w-[20%] justify-center items-center ${isActive ? "official-color rounded-md" : ""}`}>
                <button
                    className={`font-bold SD:text-xs btn-lx text-center ${isActive ? "text-white" : ""}`}
                    onClick={() => {
                        setFilter(filterValue);
                        const filteredApprovalList = approvalList.filter(approval => approval.approvalStatus === filterValue);
                        if (filteredApprovalList.length > 0) {
                            setApproval(filteredApprovalList[0]);
                        }
                    }}>
                    {label}
                </button>
            </div>
        );
    };

    // 한 Approval의 각 approver의 상태
    function GetApproverStatus({ index }: { index: number }) {
        return <>
            <div className={`w-full h-[6.25rem] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold flex-col
            ${getApprovarStatusColor(getSpecificApprover(index)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(index)?.approverStatus ?? -1)}
                <p className="text-sm">{getSpecificApprover(index) !== null && getSpecificApprover(index)?.approverStatus == 2 || getSpecificApprover(index)?.approverStatus == 3
                    ? getjyDate(getSpecificApprover(index)?.approvalDate) : ""}</p>
            </div>
        </>
    }

    function update(approvalId: number, approvalViewers: any[]) {
        const viewer = approvalViewers.map(user => user.username);
        console.log(viewer);

        const approvalRequest: approvalRequestDTO = {
            viewersname: viewer,
            title: "",
            content: "",
            sender: "",
            approversname: []
        };
        updateViewer(approvalId, approvalRequest)
            .then(r => {
                setApproval(r); console.log(r);
                const index = approvalList.findIndex(e => e.id === approval.id);
                const pre = [...approvalList]; pre[index] = r; setApprovalList(pre);
                setIsModalOpen(false);
            })
            .catch(error => console.error(error));
    }

    // approval 상세보기
    function ApprovalDetail() {
        const [approvalViewers, setApprovalViewers] = useState([] as any[]);
        const [approvalViewersText, setApprovalViewersText] = useState('');

        // 유저 추가 핸들러
        const handleAddUser = (user: any) => {
            setApprovalViewers((prevSelectedViewers) => {
                const isUserSelected = prevSelectedViewers.find((u) => u.username === user.username);
                if (isUserSelected) {
                    // 이미 선택된 유저가 있으면 배열에서 제거
                    return prevSelectedViewers.filter((u) => u.username !== user.username);
                }
                return [...prevSelectedViewers, user];
            });
        };

        // 컴포넌트 로드 시 approval.viewers를 초기 상태로 설정
        useEffect(() => {
            setApprovalViewers(approval.viewers);
        }, [approval.viewers]);

        useEffect(() => {
            setApprovalViewersText(approvalViewers.map((viewer) => viewer.name).join(', '));
        }, [approvalViewers]);

        const filteredUserList = userList.filter(user =>
            !approval?.approvers.some(approver => approver.approver.username === user.username)
        );

        // 참조 유저 제거
        const handleInputChange = (event: any) => {
            const inputValue = event.target.value;
            const selectedUserNames = inputValue.split(',').map((username: string) => username.trim());

            setApprovalViewers((prevSelectedViewers) =>
                prevSelectedViewers.filter(user => selectedUserNames.includes(user.name))
            );
        };

        return <div>
            <div className="w-full h-[90%]">
                <div className="flex flex-wrap w-full">
                    {/* 작성자 정보 */}
                    <div className="w-[20%] h-[12.5rem] border-2 border-red-300">
                        <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300">
                            <label htmlFor="senderDepartment" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안부서</label>
                            <div className="w-[50%] flex justify-center items-center">
                                <p id="senderDepartment">{approval.sender?.DepartmentResponseDTO?.name ? approval.sender?.DepartmentResponseDTO?.name : "미할당"}</p>
                            </div>
                        </div>
                        <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300">
                            <label htmlFor="senderName" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안자</label>
                            <div className="w-[50%] flex justify-center items-center">
                                <p id="senderName">{approval.sender?.name}</p>
                            </div>
                        </div>
                        <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300">
                            <label htmlFor="senderRole" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">직책</label>
                            <div className="w-[50%] flex justify-center items-center">
                                <p id="senderRole">{getRole(approval.sender?.role)}</p>
                            </div>
                        </div>
                        <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300">
                            <label htmlFor="sendDate" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안일</label>
                            <div className="w-[50%] flex justify-center items-center">
                                <p id="sendDate">{getjyDate(approval.sendDate)}</p>
                            </div>
                        </div>
                    </div>

                    {/* 결재 승인자 정보 */}
                    <>
                        <div className="w-[20%] h-[12.5rem] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(0)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(0)?.approver.name}
                            </div>
                            <GetApproverStatus index={0} />
                        </div>
                        <div className="w-[20%] h-[12.5rem] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(1)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(1)?.approver.name}
                            </div>
                            <GetApproverStatus index={1} />
                        </div>
                        <div className="w-[20%] h-[12.5rem] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(2)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(2)?.approver.name}
                            </div>
                            <GetApproverStatus index={2} />
                        </div>
                        <div className="w-[20%] h-[12.5rem] border-t-2 border-r-2 border-b border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(3)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(3)?.approver.name}
                            </div>
                            <GetApproverStatus index={3} />
                        </div>
                        <div className="w-[20%] h-[12.5rem] border-l-2 border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center" >
                                {getRole(getSpecificApprover(4)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(4)?.approver.name}
                            </div>
                            <GetApproverStatus index={4} />
                        </div>
                        <div className="w-[20%] h-[12.5rem] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(5)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(5)?.approver.name}
                            </div>
                            <GetApproverStatus index={5} />
                        </div>
                        <div className="w-[20%] h-[12.5rem] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(6)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(6)?.approver.name}
                            </div>
                            <GetApproverStatus index={6} />
                        </div>
                        <div className="w-[20%] h-[12.5rem] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(7)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(7)?.approver.name}
                            </div>
                            <GetApproverStatus index={7} />
                        </div>
                        <div className="w-[20%] h-[12.5rem] border-r-2 border-b-2 border-gray-300">
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getRole(getSpecificApprover(8)?.approver.role ?? -1)}
                            </div>
                            <div className="w-full h-[3.125rem] flex border-b-2 border-gray-300 justify-center items-center">
                                {getSpecificApprover(8)?.approver.name}
                            </div>
                            <GetApproverStatus index={8} />
                        </div>
                    </>
                </div>
                {/* 제목 & 내용 & 참조 유저 */}
                <>
                    <div className="w-full h-[3.125rem] flex flex-row justify-center border-b-2 border-gray-300">
                        <label className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">제목</label>
                        <label className="w-[90%] flex items-center border-r-2 border-gray-300 pl-5">{approval.title}</label>
                    </div>
                    <div className="w-full h-[3.125rem] flex flex-row justify-center border-b-2 border-gray-300">
                        <label className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">내용</label>
                        <label className="w-[90%] flex items-center border-r-2 border-gray-300 pl-5">{approval.content}</label>
                    </div>
                    <div className="w-full h-[3.125rem] flex flex-row justify-center border-b-2 border-gray-300">
                        <label className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">참조인</label>
                        <label className="w-[90%] flex items-center border-r-2 border-gray-300 pl-5">{approvalViewersText}</label>
                    </div>
                    <div className="w-full h-[10.625rem] border border-gray-300 border-r-2 border-l-2 border-b-2 border-gray-300 flex flex-col flex-wrap overflow-x-scroll">
                        {approval.files.length != 0 ? approval?.files.map((f: originFileResponseDTO, index: number) => (
                            <ul key={index}>
                                <div className="flex items-center bg-white p-2 w-[31.25rem]">
                                    <img src={getFileIcon(f.original_name)} className="w-[1.625rem] h-[1.9375rem] mr-2" alt="" />
                                    <a href={f.value}>{sliceText(f.original_name)}</a>
                                </div>
                            </ul>
                        ))
                            :
                            <></>
                        }
                    </div>
                </>
            </div>
            {/* 전달 & 삭제 & 허가 & 반환 버튼 */}
            <>
                {approval && approval.sender.username === user.username ?
                    <div className="w-full h-[2.5rem] mt-5 flex justify-end">
                        {approval.approvalStatus < 1 ?
                            <>
                                <button className="px-4 py-2 bg-red-500 text-white rounded-md mr-2" onClick={() => {
                                    if (window.confirm('삭제하시겠습니까?')) {
                                        deleteApproval(approval.id);
                                        setApprovalList(prevApprovalList => prevApprovalList.filter(e => e.id !== approval.id));
                                        if (approvalList.length > 0) {
                                            setApproval(approvalList[0]);
                                        }
                                    }
                                }}>삭제</button>
                            </>
                            :
                            <>
                                <button className="px-4 py-2 bg-gray-400 text-white rounded-md mr-2" disabled>삭제</button>

                            </>
                        }
                        <button className="px-4 py-2 bg-blue-400 text-white rounded-md mr-2" onClick={() => setIsModalOpen(true)}>전달</button>
                        <Modal open={isModalOpen} onClose={() => setIsModalOpen(false)} escClose={true} outlineClose={true}>
                            <div className="flex flex-col items-center w-[31.25rem] h-[43.75rem]">
                                <p className="font-bold text-3xl m-3 mb-8 flex justify-center w-full">멤버 추가하기</p>
                                <div className="w-full flex justify-end">
                                    <button className="px-4 py-2 official-color text-white rounded-md mr-2 w-[5rem] mb-2"
                                        onClick={() => update(approval.id, approvalViewers)}>전달</button>
                                </div>
                                <input type="text" className="w-[90%] flex items-center border-2 border-gray-300 pl-5 mb-2"
                                    onChange={(e) => handleInputChange(e)} value={approvalViewersText} />
                                <ul className="overflow-y-scroll w-full">
                                    {filteredUserList.map((user, index) => (
                                        <li key={index} className="flex justify-between items-center mb-5 w-full">
                                            <span className="font-bold text-md m-3 w-[25%]">{user.name}</span>
                                            <span className="text-md m-3 w-[25%]">부서</span>
                                            <span className="text-md m-3 w-[25%]">역할</span>
                                            {approvalViewersText.includes(user.name) ?
                                                <button
                                                    onClick={() => handleAddUser(user)}
                                                    className="font-bold text-3xl m-3 w-[25%] text-red-500"
                                                >
                                                    ✓
                                                </button>
                                                :
                                                <button
                                                    onClick={() => handleAddUser(user)}
                                                    className="font-bold text-3xl m-3 w-[25%]"
                                                >
                                                    +
                                                </button>
                                            }

                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </Modal>
                    </div>
                    :
                    <></>
                }
                {approval && approval.approvers.some(e => e.approver.username === user.username) ? (
                    <div className="w-full h-[3.125rem] mt-5 flex justify-end">
                        {approval.approvers.filter(e => e.approver.username === user.username && e.approverStatus === 1)
                            .map((e, index) => (
                                <div key={index}>
                                    <button className="px-4 py-2 bg-green-500 text-white rounded-md mr-2" onClick={() => {
                                        if (window.confirm('허가하시겠습니까?')) {
                                            acceptApproval(approval.id, true).then(
                                                r => {
                                                    setApproval(r);
                                                    const index = approvalList.findIndex(e => e.id === approval.id);
                                                    const pre = [...approvalList]; pre[index] = r; setApprovalList(pre);
                                                }
                                            )
                                        }
                                    }
                                    }>허가</button>
                                    <button className="px-4 py-2 bg-red-500 text-white rounded-md mr-2" onClick={() => {
                                        if (window.confirm('반환하시겠습니까?')) {
                                            acceptApproval(approval.id, false).then(
                                                r => {
                                                    setApproval(r);
                                                    const index = approvalList.findIndex(e => e.id === approval.id);
                                                    const pre = [...approvalList]; pre[index] = r; setApprovalList(pre);
                                                }
                                            )
                                        }
                                    }}>반환</button>
                                </div>
                            ))
                        }
                    </div>
                ) : (
                    <></>
                )}
            </>
        </div>
    }


    // 페이지
    return <Main user={user} isClientLoading={isClientLoading}>
        {/* 왼쪽 부분 */}
        <div className="w-4/12 flex items-center justify-center h-full pt-10 pb-12">
            <div className="w-11/12 h-full">
                {/* 검색 인풋 */}
                <div className="flex items-center border-2 border-gray rounded-full h-[6%] shadow bg-white">
                    <img src="/searchg.png" className="w-[1.875rem] h-[1.875rem] m-2" alt="검색 사진" />
                    <input
                        type="text"
                        placeholder="결재 제목 검색"
                        className="bolder-0 outline-none bg-white text-black w-[90%]"
                        value={keyword}
                        onChange={e => setKeyword(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                handleSearch();
                            }
                        }}
                    />
                    <button className="text-gray-300 whitespace-nowrap w-[3.125rem] h-[3.125rem] m-2" onClick={handleSearch} >
                        검색
                    </button>
                </div>

                {/* 리스트 */}
                <div className="pt-5 h-[94%]">
                    <div className="bg-white shadow w-full h-full flex flex-col">
                        {/* 필터 */}
                        <div className="bg-gray-100 w-full justify-between h-[3.125rem] min-h-[3.125rem] flex flex-row mb-5">
                            {renderFilterButton(-1, "전체")}
                            {renderFilterButton(0, "결재 대기중")}
                            {renderFilterButton(1, "결재 중")}
                            {renderFilterButton(2, "허가")}
                            {renderFilterButton(3, "반환")}
                        </div>

                        {/* 필터링 된 리스트 -> 누르면 읽음 & 상세보기 */}
                        <div className="relative flex flex-col justify-center w-full h-[100%]">
                            <div ref={appBoxRef} onScroll={loadPage} className="w-full h-[600px] overflow-x-hidden overflow-y-scroll">
                                {approvalList.filter(approval => filter === -1 || approval.approvalStatus === filter).map((approval, index) => (
                                    <div key={index}
                                        className="w-[98%] h-[3.125rem] border-2 border-gray-300 mb-1 ml-1 rounded-lg shadow-md flex justify-between">
                                        {user && approval.readUsers.includes(user.username) === false ? <div className="h-full w-[0.375rem] official-color mr-2"></div> : <></>}
                                        <h4 className="flex items-center justify-center font-bold w-[80%]">
                                            <a href="#" className="" onClick={() => {
                                                readApproval(approval?.id).then(
                                                    r => {
                                                        setApproval(r);
                                                        const index = approvalList.findIndex(e => e.id === approval.id);
                                                        const pre = [...approvalList]; pre[index] = r; setApprovalList(pre);
                                                    })
                                            }}>{approval.title}</a>
                                        </h4>
                                        <p className={`flex items-center justify-center text-sm w-[20%] ${getStatusColor(approval.approvalStatus)}`}>
                                            {getStatusText(approval.approvalStatus)}</p>
                                    </div>
                                ))}

                                {/* 결재 기안서 만들기 */}
                                <a href="/approval/ApprovalForm" className="absolute bottom-4 right-4 w-[3.125rem] h-[3.125rem] btn rounded-full official-color text-xl font-bold text-white flex items-center justify-center">
                                    +
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        {/* 오른쪽 부분 */}
        <div className="w-8/12 flex items-center justify-center pt-10 pb-12">
            <div className="w-11/12 bg-white h-full flex flex-col shadow">
                {/* 결재 기안서 상세 보기 */}
                {approval != null ? <ApprovalDetail /> : <></>}

            </div>
        </div>
    </Main >
}