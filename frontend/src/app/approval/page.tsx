"use client";
import { getUser } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import Modal from "../Global/Modal";
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

    const testData: approvalResponseDTO[] = [
        {
            id: 1,
            title: "2024년 예산 승인 요청",
            status: 2, // 예를 들어, 2는 "결제 대기중"을 의미한다고 가정
            sender: "홍길동",
            approver: "김철수",
            viewers: ["이영희", "박민수"],
        },
        {
            id: 2,
            title: "프로젝트 X 완료 보고서",
            status: 1, // 예를 들어, 1은 "안 읽음"을 의미한다고 가정
            sender: "이순신",
            approver: "최진희",
            viewers: ["강호동"],
        },
        {
            title: "연차 휴가 신청",
            status: 3, // 예를 들어, 3은 "허가"를 의미한다고 가정
            sender: "정우성",
            approver: "황정민",
            viewers: ["김연아", "손흥민", "이지영"],
        },
        {
            id: 4,
            title: "새로운 마케팅 계획",
            status: 4, // 예를 들어, 0은 "반환"를 의미한다고 가정
            sender: "오세훈",
            approver: "임지연",
            viewers: ["문재인", "유재석", "김태훈", "한성언", "남도원"],
        },
        {
            id: 1,
            title: "2024년 예산 승인 요청",
            status: 2, // 예를 들어, 2는 "결제 대기중"을 의미한다고 가정
            sender: "홍길동",
            approver: "김철수",
            viewers: ["이영희", "박민수"],
        },
        {
            id: 2,
            title: "프로젝트 X 완료 보고서",
            status: 1, // 예를 들어, 1은 "안 읽음"을 의미한다고 가정
            sender: "이순신",
            approver: "최진희",
            viewers: ["강호동"],
        },
        {
            title: "연차 휴가 신청",
            status: 3, // 예를 들어, 3은 "허가"를 의미한다고 가정
            sender: "정우성",
            approver: "황정민",
            viewers: ["김연아", "손흥민", "이지영"],
        },
        {
            id: 4,
            title: "새로운 마케팅 계획",
            status: 4, // 예를 들어, 0은 "반환"를 의미한다고 가정
            sender: "오세훈",
            approver: "임지연",
            viewers: ["문재인", "유재석", "김태훈", "한성언", "남도원"],
        },
        {
            id: 1,
            title: "2024년 예산 승인 요청",
            status: 2, // 예를 들어, 2는 "결제 대기중"을 의미한다고 가정
            sender: "홍길동",
            approver: "김철수",
            viewers: ["이영희", "박민수"],
        },
        {
            id: 2,
            title: "프로젝트 X 완료 보고서",
            status: 1, // 예를 들어, 1은 "안 읽음"을 의미한다고 가정
            sender: "이순신",
            approver: "최진희",
            viewers: ["강호동"],
        },
        {
            title: "연차 휴가 신청",
            status: 3, // 예를 들어, 3은 "허가"를 의미한다고 가정
            sender: "정우성",
            approver: "황정민",
            viewers: ["김연아", "손흥민", "이지영"],
        },
        {
            id: 4,
            title: "새로운 마케팅 계획",
            status: 4, // 예를 들어, 0은 "반환"를 의미한다고 가정
            sender: "오세훈",
            approver: "임지연",
            viewers: ["문재인", "유재석", "김태훈", "한성언", "남도원"],
        },
        {
            id: 1,
            title: "2024년 예산 승인 요청",
            status: 2, // 예를 들어, 2는 "결제 대기중"을 의미한다고 가정
            sender: "홍길동",
            approver: "김철수",
            viewers: ["이영희", "박민수"],
        },
        {
            id: 2,
            title: "프로젝트 X 완료 보고서",
            status: 1, // 예를 들어, 1은 "안 읽음"을 의미한다고 가정
            sender: "이순신",
            approver: "최진희",
            viewers: ["강호동"],
        },
        {
            title: "연차 휴가 신청",
            status: 3, // 예를 들어, 3은 "허가"를 의미한다고 가정
            sender: "정우성",
            approver: "황정민",
            viewers: ["김연아", "손흥민", "이지영"],
        },
        {
            id: 4,
            title: "새로운 마케팅 계획",
            status: 4, // 예를 들어, 0은 "반환"를 의미한다고 가정
            sender: "오세훈",
            approver: "임지연",
            viewers: ["문재인", "유재석", "김태훈", "한성언", "남도원"],
        }
    ];

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

    // 상태 매핑 함수
    const getStatusText = (status: number): string => {
        switch (status) {
            case 0:
                return "전체";
            case 1:
                return "안 읽음";
            case 2:
                return "결제 대기중..";
            case 3:
                return "허가";
            case 4:
                return "반환";
            default:
                return "알 수 없음";
        }
    };

    // 상태 색상 매핑 함수
    const getStatusColor = (status: number): string => {
        switch (status) {
            case 0:
                return "text-black"; // 전체
            case 1:
                return "text-yellow-500"; // 안 읽음
            case 2:
                return "text-blue-500"; // 결제 대기중
            case 3:
                return "text-green-500"; // 허가
            case 4:
                return "text-red-500"; // 반환
            default:
                return "text-black"; // 알 수 없음
        }
    };

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
                <div className="bg-white shadow w-full">
                    <div className="bg-gray-200 w-full justify-between h-[50px] flex flex-row mb-5">
                        {filter == 0 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >전체</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => setFilter(0)}>전체</button>
                            </div>
                        }
                        {filter == 1 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >안 읽음</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => setFilter(1)}>안 읽음</button>
                            </div>
                        }
                        {filter == 2 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >결제 대기중</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => setFilter(2)}>결제 대기중</button>
                            </div>
                        }
                        {filter == 3 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >허가</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => setFilter(3)}>허가</button>
                            </div>
                        }
                        {filter == 4 ?
                            <div className="flex w-[20%] justify-center items-center official-color rounded-md">
                                <button className="font-bold btn-lx text-center text-white" >반환</button>
                            </div> :
                            <div className="flex w-[20%] justify-center items-center">
                                <button className="font-bold btn-lx text-center" onClick={() => setFilter(4)}>반환</button>
                            </div>
                        }
                    </div>
                    <div className="relative flex flex-col justify-center w-full h-full">
                        <div className="w-full h-[705px] overflow-x-hidden overflow-y-scroll">
                            {/* Here we display the filtered approval data */}
                            {testData.filter(item => filter === 0 || item.status === filter).map((item, index) => (
                                <div key={index}
                                    className="w-[550px] h-[50px] border-2 border-gray-300 mb-1 ml-1 rounded-lg shadow-md flex justify-between">
                                    <h4 className="flex items-center justify-center font-bold w-[80%]">
                                        <a href="#" className="">{item.title}</a></h4>
                                    <p className={`flex items-center justify-center text-sm w-[20%] ${getStatusColor(item.status)}`}>{getStatusText(item.status)}</p>
                                </div>
                            ))}

                            {/* 결재 서류 작성 */}
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
            </div>
        </div>
    </Main >
}