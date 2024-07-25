"use client";
import { acceptApproval, createApproval, deleteApproval, getApprovalList, getUser, readApproval } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import { useEffect, useState } from "react";
import { getjyDate, getRole } from "../Global/Method";

export default function Approval() {
    interface approvalResponseDTO {
        id: number,
        title: string,
        content: string,
        sender: userResponseDTO,
        approvers: approverResponseDTO[],
        viewers: userResponseDTO[],
        approvalStatus: number, //승인 안승인
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

    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [filter, setFilter] = useState(-1); // Approval 필터
    const [user, setUser] = useState(null as any);
    const [isClientLoading, setClientLoading] = useState(true);
    const [keyword, setKeyword] = useState('');
    const [approval, setApproval] = useState<approvalResponseDTO>(null as any);
    const [approvalList, setApprovalList] = useState<approvalResponseDTO[]>([]);
    const selectedViewersText = approval?.viewers.map(viewer => viewer.name).join(', ');

    // 유저 토큰 확인하기
    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
                getApprovalList().then(r => setApprovalList(r)).catch(e => console.log(e));
                // getApprovalList(keyword, 0).then(r => setApprovalList(r)).catch(e => console.log(e));
            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            location.href = '/';
    }, [ACCESS_TOKEN])

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

    // approval 상세보기
    function ApprovalDetail() {
        return <div>
            <div className="w-full h-[90%]">
                <div className="flex flex-wrap w-full">
                    {/* 작성자 정보 */}
                    <div className="w-[20%] h-[200px] border-2 border-red-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300">
                            <label htmlFor="senderDepartment" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안부서</label>
                            <div className="w-[50%] flex justify-center items-center">
                                <p id="senderDepartment">{approval.sender?.DepartmentResponseDTO?.name ? approval.sender?.DepartmentResponseDTO?.name : "미할당"}</p>
                            </div>
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300">
                            <label htmlFor="senderName" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안자</label>
                            <div className="w-[50%] flex justify-center items-center">
                                <p id="senderName">{approval.sender?.name}</p>
                            </div>
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300">
                            <label htmlFor="senderRole" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">직책</label>
                            <div className="w-[50%] flex justify-center items-center">
                                <p id="senderRole">{getRole(approval.sender?.role)}</p>
                            </div>
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300">
                            <label htmlFor="sendDate" className="w-[50%] flex justify-center items-center border-r-2 border-gray-300">기안일</label>
                            <div className="w-[50%] flex justify-center items-center">
                                <p id="sendDate">{getjyDate(approval.sendDate)}</p>
                            </div>
                        </div>
                    </div>

                    {/* 결재 승인자 정보 */}
                    <div className="w-[20%] h-[200px] border-t-2 border-r-2 border-b border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getRole(getSpecificApprover(0)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(0)?.approver.name}
                        </div>
                        <div id="selectZero" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(0)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(0)?.approverStatus ?? -1)}
                        </div>
                    </div>
                    <div className="w-[20%] h-[200px] border-t-2 border-r-2 border-b border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getRole(getSpecificApprover(1)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(1)?.approver.name}
                        </div>
                        <div id="selectOne" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(1)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(1)?.approverStatus ?? -1)}
                        </div>
                    </div>
                    <div className="w-[20%] h-[200px] border-t-2 border-r-2 border-b border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getRole(getSpecificApprover(2)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(2)?.approver.name}
                        </div>
                        <div id="selectTwo" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(2)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(2)?.approverStatus ?? -1)}
                        </div>
                    </div>
                    <div className="w-[20%] h-[200px] border-t-2 border-r-2 border-b border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getRole(getSpecificApprover(3)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(3)?.approver.name}
                        </div>
                        <div id="selectThree" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(3)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(3)?.approverStatus ?? -1)}
                        </div>
                    </div>
                    <div className="w-[20%] h-[200px] border-l-2 border-r-2 border-b-2 border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center" >
                            {getRole(getSpecificApprover(4)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(4)?.approver.name}
                        </div>
                        <div id="selectFour" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(4)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(4)?.approverStatus ?? -1)}
                        </div>
                    </div>
                    <div className="w-[20%] h-[200px] border-r-2 border-b-2 border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getRole(getSpecificApprover(5)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(5)?.approver.name}
                        </div>
                        <div id="selectFive" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(5)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(5)?.approverStatus ?? -1)}
                        </div>
                    </div>
                    <div className="w-[20%] h-[200px] border-r-2 border-b-2 border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getRole(getSpecificApprover(6)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(6)?.approver.name}
                        </div>
                        <div id="selectSix" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(6)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(6)?.approverStatus ?? -1)}
                        </div>
                    </div>
                    <div className="w-[20%] h-[200px] border-r-2 border-b-2 border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getRole(getSpecificApprover(7)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(7)?.approver.name}
                        </div>
                        <div id="selectSeven" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(7)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(7)?.approverStatus ?? -1)}
                        </div>
                    </div>
                    <div className="w-[20%] h-[200px] border-r-2 border-b-2 border-gray-300">
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getRole(getSpecificApprover(8)?.approver.role ?? -1)}
                        </div>
                        <div className="w-full h-[50px] flex border-b-2 border-gray-300 justify-center items-center">
                            {getSpecificApprover(8)?.approver.name}
                        </div>
                        <div id="selectEight" className={`w-full h-[100px] flex border-b-2 border-gray-300 justify-center items-center text-2xl font-bold
                            ${getApprovarStatusColor(getSpecificApprover(8)?.approverStatus ?? -1)}`}>{getApprovarStatusText(getSpecificApprover(8)?.approverStatus ?? -1)}
                        </div>
                    </div>
                </div>
                <div className="w-full h-[50px] flex flex-row justify-center border-b-2 border-gray-300">
                    <label className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">제목</label>
                    <label className="w-[90%] flex items-center border-r-2 border-gray-300 pl-5">{approval.title}</label>
                </div>
                <div className="w-full h-[50px] flex flex-row justify-center border-b-2 border-gray-300">
                    <label className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">내용</label>
                    <label className="w-[90%] flex items-center border-r-2 border-gray-300 pl-5">{approval.content}</label>
                </div>
                <div className="w-full h-[50px] flex flex-row justify-center border-b-2 border-gray-300">
                    <label className="w-[10%] flex justify-center items-center border-r-2 border-l-2 border-gray-300">참조인</label>
                    <label className="w-[90%] flex items-center border-r-2 border-gray-300 pl-5">{selectedViewersText}</label>
                </div>
                <div className="relative w-full h-[150px] border border-gary-500 overflow-y-scroll border-r-2 border-l-2 border-b-2 border-gray-300">
                    <button className="btn btn-sm absolute top-[5px] right-[5px]">파일 선택</button>
                    {/* <img src="/plus.png" alt="" className="w-[30px] h-[30px] absolute top-[5px] right-[5px] cursor-pointer" ></img> */}
                </div>
            </div>
        </div>
    }


    // 페이지
    return <Main user={user} isClientLoading={isClientLoading}>
        {/* 왼쪽 부분 */}
        <div className="w-4/12 flex items-center justify-center h-full pt-10 pb-4">
            <div className="w-11/12 h-full">
                {/* 검색 인풋 */}
                <div className="flex items-center border-2 border-gray rounded-full h-[50px] mb-5 shadow">
                    <img src="/searchg.png" className="w-[30px] h-[30px] m-2" alt="검색 사진" />
                    <input
                        type="text"
                        placeholder="결재 제목 검색"
                        className="bolder-0 outline-none bg-white text-black w-[90%]"
                        value={keyword}
                        onChange={e => setKeyword(e.target.value)}
                    />
                    <button className="text-gray-300 whitespace-nowrap w-[50px] h-[50px] m-2">
                        검색
                    </button>
                </div>
                {/* 필터 */}
                <div className="bg-white shadow w-full">
                    <div className="bg-gray-200 w-full justify-between h-[50px] flex flex-row mb-5">
                        {filter == -1 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >전체</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => {
                                    setFilter(-1);
                                    if (approvalList.length > 0) {
                                        setApproval(approvalList[0]);
                                    }
                                }}>전체</button>
                            </div>
                        }
                        {filter == 0 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >결재 대기중</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => {
                                    setFilter(0);
                                    const filteredApprovalList = approvalList.filter(approval => approval.approvalStatus === 0);
                                    if (filteredApprovalList.length > 0) {
                                        setApproval(filteredApprovalList[0]);
                                    }
                                }}>결재 대기중</button>
                            </div>
                        }
                        {filter == 1 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >결재 중</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => {
                                    setFilter(1);
                                    const filteredApprovalList = approvalList.filter(approval => approval.approvalStatus === 1);
                                    if (filteredApprovalList.length > 0) {
                                        setApproval(filteredApprovalList[0]);
                                    }
                                }}>결재 중</button>
                            </div>
                        }
                        {filter == 2 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >허가</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => {
                                    setFilter(2);
                                    const filteredApprovalList = approvalList.filter(approval => approval.approvalStatus === 2);
                                    if (filteredApprovalList.length > 0) {
                                        setApproval(filteredApprovalList[0]);
                                    }
                                }}>허가</button>
                            </div>
                        }
                        {filter == 3 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >반환</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => {
                                    setFilter(3);
                                    const filteredApprovalList = approvalList.filter(approval => approval.approvalStatus === 3);
                                    if (filteredApprovalList.length > 0) {
                                        setApproval(filteredApprovalList[0]);
                                    }
                                }}>반환</button>
                            </div>
                        }
                    </div>

                    {/* 필터링 된 리스트 -> 누르면 읽음 & 상세보기 */}
                    <div className="relative flex flex-col justify-center w-full h-full">
                        <div className="w-full h-[705px] overflow-x-hidden overflow-y-scroll">
                            {approvalList.filter(approval => filter === -1 || approval.approvalStatus === filter).map((approval, index) => (
                                <div key={index}
                                    className="w-[550px] h-[50px] border-2 border-gray-300 mb-1 ml-1 rounded-lg shadow-md flex justify-between">
                                    {user && approval.readUsers.includes(user.username) === false ? <div className="h-full w-[6px] official-color mr-2"></div> : <></>}
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
                                    <p className={`flex items-center justify-center text-sm w-[20%] ${getStatusColor(approval.approvalStatus)}`}>{getStatusText(approval.approvalStatus)}</p>
                                </div>
                            ))}

                            {/* 결재 기안서 만들기 */}
                            <a href="/approval/ApprovalForm" className="absolute bottom-4 right-4 w-[50px] h-[50px] btn rounded-full official-color text-xl font-bold text-white flex items-center justify-center">
                                +
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        {/* 오른쪽 부분 */}
        <div className="w-8/12 flex items-center justify-center pt-10 pb-4">
            <div className="w-11/12 bg-white h-full flex flex-col shadow">
                {approval != null ? <ApprovalDetail /> : <></>}

                {approval && approval.sender.username === user.username ?
                    <div className="w-full h-[40px] mt-5 flex justify-end">
                        {approval.approvalStatus < 1 ?
                            <><button className="px-4 py-2 bg-red-500 text-white rounded-md mr-2" onClick={() => {
                                if (window.confirm('삭제하시겠습니까?')) {
                                    deleteApproval(approval.id);
                                    setApprovalList(prevApprovalList => prevApprovalList.filter(e => e.id !== approval.id));
                                    if (approvalList.length > 0) {
                                        setApproval(approvalList[0]);
                                    }
                                }
                            }}>삭제</button>
                                <button className="px-4 py-2 bg-blue-500 text-white rounded-md mr-2">수정</button></> :
                            <><button className="px-4 py-2 bg-red-500 text-white rounded-md" disabled>삭제</button><button className="px-4 py-2 bg-blue-500 text-white rounded-md mr-2" disabled>수정</button></>
                        }
                    </div>
                    :
                    <></>
                }
                {approval && approval.approvers.some(e => e.approver.username === user.username) ? (
                    <div className="w-full h-[50px] mt-5 flex justify-end">
                        {approval.approvers
                            .filter(e => e.approver.username === user.username && e.approverStatus === 1)
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
            </div>
        </div>
    </Main >
}