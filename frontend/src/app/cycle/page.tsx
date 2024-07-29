"use client";

import React, { useState, useEffect } from "react";
import { getUser, createSchedule, fetchSchedules, updateSchedule, deleteSchedule, getTagList, updateTag, deleteTag } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import { eontransferLocalTime, getDateEmailTime, getScheduleDate, isHoliday, transferLocalTime } from "../Global/Method";
import DropDown, { Direcion } from '../Global/DropDown';
// import MyComponent from "../Global/Accordion";

// 인터페이스 정의
interface CycleResponseDTO {
    cycleDTOList: CycleDTO[],
    holiday: boolean,
    holidatTitle: string
}

interface CycleDTO {
    id: number,
    title: string,
    content: string,
    startDate: number,
    endDate: number,
    tag: CycleTagDTO
}

interface CycleTagDTO {
    name: string,
    color: string
}

interface CycleTagResponseDTO {
    id: number,
    name: string,
    color: string
}

interface Schedule {
    id: number;
    title: string;
    content: string;
    startDate: string; // ISO 문자열 형식
    endDate: string;   // ISO 문자열 형식
    tagName?: string;
    tagColor?: string;
}

export default function Cycle() {
    // 상태 변수 정의
    const [user, setUser] = useState<any>(null);
    const ACCESS_TOKEN = typeof window === 'undefined' ? null : localStorage.getItem('accessToken');
    const last_date = typeof window === 'undefined' ? null : localStorage.getItem('last_date');
    const [isClientLoading, setClientLoading] = useState(true);
    const [selectedDate, setSelectedDate] = useState<Date | null>(new Date());
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [startDateInput, setStartDateInput] = useState<string>("");
    const [endDateInput, setEndDateInput] = useState<string>("");
    const [schedules, setSchedules] = useState<any[]>([]); // 기존 상태
    const [selectCycle, setSelectCycle] = useState<CycleDTO | null>();
    const [cycleStartDate, setCycleStartDate] = useState<Date | null>(null);
    const [cycleEndDate, setCycleEndDate] = useState<Date | null>(null);
    const [headerDate, setHeaderDate] = useState([] as any[]);
    const [statusid, setStatusid] = useState(0);
    const [isTagModalOpen, setIsTagModalOpen] = useState(false);
    const [selectedTag, setSelectedTag] = useState<CycleTagResponseDTO | null>(null);
    const [tags, setTags] = useState<string[]>([]);
    const [color, setColor] = useState<string>("");
    const [tagList, setTagList] = useState<CycleTagResponseDTO[]>([]);
    const [upcomingCycles, setUpcomingCycles] = useState<Schedule[]>([]); // 새로운 상태
    const [endDate, setEndDate] = useState<Date>(new Date());   // 예시: 현재 날짜로 초기화

    // 날짜와 월 관련 상수
    const year = selectedDate ? selectedDate.getFullYear() : new Date().getFullYear();
    const month = selectedDate ? selectedDate.getMonth() : new Date().getMonth();
    const firstDate = new Date(year, month, 1);
    const startDate = new Date(firstDate.getTime() - firstDate.getDay() * 1000 * 60 * 60 * 24);
    const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

    // 날짜 관련 함수
    const formatDateToISO = (date: Date): string => getScheduleDate(date); // YYYY-MM-DD 형식으로 반환
    const changeMonth = (delta: number) => { const date = new Date(year, month + delta, 1); setSelectedDate(date); localStorage.setItem('last_date', JSON.stringify(date)) };
    const handleDateClick = (date: Date) => { setSelectedDate(date); localStorage.setItem('last_date', JSON.stringify(date)) };

    // 태그 관련 함수
    const handleEditTag = (tag: CycleTagResponseDTO) => { setSelectedTag(tag); setIsTagModalOpen(true); };
    useEffect(() => {
        if (last_date)
            setSelectedDate(new Date(JSON.parse(last_date)));
    }, [last_date]);

    // 스케줄 관련 함수
    const loadSchedules = async () => {
        if (selectedDate) {
            try {
                const { startDate, endDate } = getFetchDateRange();
                console.log(`Fetching schedules from: ${formatDateToISO(startDate)} to: ${formatDateToISO(endDate)}`);
                const personal: CycleDTO[] = await fetchSchedules(startDate, endDate, 0);
                const group: CycleDTO[] = await fetchSchedules(startDate, endDate, 1);
                const data: CycleDTO[] = [...personal, ...group];
                const updatedData = data.map(cycle => ({
                    ...cycle,
                    startDate: transferLocalTime(new Date(cycle.startDate)),
                    endDate: transferLocalTime(new Date(cycle.endDate))
                }));
                console.log("Fetched schedules data:", updatedData);
                setSchedules(updatedData);
            } catch (error) {
                console.error("Failed to fetch schedules:", error);
            }
        } else {
            console.log("No selected date to fetch schedules.");
        }
    };

    const handleSaveTag = async () => {
        if (!selectedTag) return;
        try {
            await updateTag(selectedTag.id, { name: selectedTag.name, color: selectedTag.color });
            alert("태그가 성공적으로 수정되었습니다.");
            setIsTagModalOpen(false);
            setSelectedTag(null);
            getTagList(statusid).then(r => setTagList(r)).catch(e => console.log(e));
        } catch (error) {
            console.error("태그 수정 실패:", error);
            alert("태그 수정에 실패했습니다.");
        }
    };

    const getFetchDateRange = () => {
        if (selectedDate) {
            const startDate = new Date(selectedDate);
            startDate.setDate(selectedDate.getDate() - 2);
            startDate.setHours(0, 0, 0, 0);
            const endDate = new Date(selectedDate);
            endDate.setDate(selectedDate.getDate() + 2);
            endDate.setHours(23, 59, 59, 999);
            return { startDate, endDate };
        }
        return { startDate: new Date(), endDate: new Date() };
    };

    // 일정 저장 함수
    const handleSaveSchedule = async () => {
        if (!title || !content || !startDateInput || !endDateInput) {
            alert("모든 필드를 입력해 주세요.");
            return;
        }
        const startDate = transferLocalTime(new Date(startDateInput));
        const endDate = transferLocalTime(new Date(endDateInput));
        const scheduleData = {
            title: title,
            content: content,
            startDate: startDate,
            endDate: endDate,
            tagName: tags[0] || '', // 태그의 첫 번째 이름을 사용
            tagColor: color
        };
        try {
            if (selectCycle) {
                await updateSchedule(selectCycle.id, scheduleData, statusid);
                alert("일정이 성공적으로 수정되었습니다.");
            } else {
                await createSchedule(scheduleData, statusid);
                alert("일정이 성공적으로 생성되었습니다.");
            }
            setIsModalOpen(false);
            setSelectCycle(null);
            loadSchedules();
        } catch (error) {
            console.error("일정 저장 실패:", error);
            alert("일정 저장에 실패했습니다.");
        }
    };

    // 일정 삭제 함수
    const handleDeleteSchedule = async (id?: number) => {
        if (!id) return;
        if (window.confirm("정말로 삭제하시겠습니까?")) {
            try {
                await deleteSchedule(id);
                alert("일정이 성공적으로 삭제되었습니다.");
                setSelectCycle(null);
                loadSchedules();
            } catch (error) {
                console.error("일정 삭제 실패:", error);
                alert("일정 삭제에 실패했습니다.");
            }
        }
    };

    const getUpcomingCycles = (schedules: any[]): Schedule[] => {
        const now = new Date();
        return schedules
            .filter((schedule: any) => {
                const endDate = new Date(schedule.endDate);
                const fifteenMinutesBeforeEnd = new Date(endDate.getTime() - 15 * 60 * 1000); // 15분 전
                return now >= fifteenMinutesBeforeEnd && now <= endDate;
            })
            .map((schedule: any) => ({
                id: schedule.id,
                title: schedule.title,
                content: schedule.content,
                startDate: new Date(schedule.startDate).toISOString(),
                endDate: new Date(schedule.endDate).toISOString(),
                tagName: schedule.tag?.name,
                tagColor: schedule.tag?.color,
            }));
    };

    // DateColumn 컴포넌트
    function DateColumn({ date }: { date: Date }) {
        const now = new Date();
        const isToday = date.toDateString() === now.toDateString();
        const isSelected = selectedDate && date.toDateString() === selectedDate.toDateString();
        return (
            <div
                onClick={() => { handleDateClick(date); }}
                className={`w-[51px] h-[41px] rounded-2xl flex items-center justify-center cursor-pointer
                    ${date.getMonth() !== month ? 'opacity-25' : 'opacity-100'}
                    ${isToday ? 'bg-blue-500 text-white' : ''}
                    ${isSelected ? 'border-4 border-red-500' : ''}`}>
                {date.getDate()}
            </div>
        );
    }

    // 스케줄 테이블 컴포넌트
    function ScheduleTable() {
        const [dropDownOpen, setDropDownOpen] = useState<number | null>(null);

        const handleCycleClick = (cycle: CycleDTO) => {
            setSelectCycle(cycle);
            setCycleStartDate(eontransferLocalTime(new Date(cycle.startDate)));
            setCycleEndDate(new Date(cycle.endDate));
            setDropDownOpen(null); // 기존 드롭다운을 닫습니다.
        };

        const handleDropDownClick = (event: React.MouseEvent<HTMLButtonElement, MouseEvent>, dateIndex: number) => {
            event.stopPropagation(); // 클릭 이벤트 전파 방지
            setDropDownOpen(dateIndex);
        };

        const getTimeSlots = () => Array.from({ length: 24 }, (_, i) => `${i}:00`);

        return (
            <table className="w-full border-collapse">
                <thead>
                    <tr className="h-[50px]">
                        <th className="border w-[10%]">시간</th>
                        {headerDate.map((headerDate, dateIndex) => (
                            <th key={dateIndex} className={`border w-[10%] ${headerDate.isToday ? 'bg-red-500 text-white' : ''}`}>
                                {new Date(headerDate.dateStr).toLocaleDateString('ko-KR', { weekday: 'short', day: 'numeric' })}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody className="text-center">
                    {getTimeSlots().map((slot, slotIndex) => (
                        <tr key={slotIndex}>
                            <td className="border w-[10%]">{slot}</td>
                            {headerDate.map((headerDate, dateIndex) => {
                                const displayedCycles = new Set<number>(); // 표시된 사이클 ID를 추적
                                const filteredCycles = (schedules ?? []).flatMap((schedule: CycleResponseDTO) =>
                                    schedule.cycleDTOList.filter(cycle => {
                                        const cycleStart = new Date(cycle.startDate);
                                        const cycleEnd = new Date(cycle.endDate);
                                        const date = new Date(headerDate.dateStr);
                                        const cycleStartHour = cycleStart.getHours();
                                        const cycleEndHour = cycleEnd.getHours();
                                        return (
                                            (cycleStart.toDateString() === date.toDateString() && cycleStartHour === slotIndex) ||
                                            (cycleEnd.toDateString() === date.toDateString() && cycleEndHour === slotIndex)
                                        );
                                    })
                                ).filter(cycle => {
                                    if (displayedCycles.has(cycle.id)) {
                                        return false; // 이미 표시된 사이클은 제외
                                    }
                                    displayedCycles.add(cycle.id);
                                    return true;
                                });

                                const showDropDown = filteredCycles.length > 2; // 사이클이 2개 이상이면 드롭다운을 표시

                                return (
                                    <td key={`${dateIndex}-${slotIndex}`} className="border w-[10%] h-[60px] relative">
                                        <div className="flex flex-col h-full justify-center items-center">
                                            {showDropDown ? (
                                                <>
                                                    <button
                                                        className="mt-1 px-2 py-1 bg-purple-300 text-black rounded"
                                                        onClick={(e) => handleDropDownClick(e, dateIndex)}
                                                    >
                                                        더보기...
                                                    </button>
                                                    {dropDownOpen === dateIndex && (
                                                        <DropDown
                                                            open={true}
                                                            onClose={() => setDropDownOpen(null)}
                                                            className="bg-white"
                                                            width={200}
                                                            height={200}
                                                            defaultDriection={Direcion.DOWN}
                                                            button=""
                                                            x={0}
                                                            y={0}
                                                        >
                                                            {filteredCycles.map((cycle: CycleDTO, index: number) => (
                                                                <div
                                                                    key={index}
                                                                    className="cursor-pointer p-2 rounded mb-1 flex items-center space-x-2"
                                                                    onClick={() => handleCycleClick(cycle)}>
                                                                    <div
                                                                        className="w-4 h-4 rounded-md"
                                                                        style={{ backgroundColor: cycle.tag.color }}
                                                                        aria-label={`Tag color: ${cycle.tag.name}`}
                                                                    />
                                                                    <span className="text-black truncate max-w-[100px]" title={cycle.title}>
                                                                        {cycle.title}
                                                                    </span>
                                                                </div>
                                                            ))}
                                                        </DropDown>
                                                    )}
                                                </>
                                            ) : (
                                                filteredCycles.map((cycle: CycleDTO, index: number) => (
                                                    <div
                                                        key={index}
                                                        className="cursor-pointer p-2 rounded mb-1 flex items-center space-x-2"
                                                        onClick={() => handleCycleClick(cycle)}>
                                                        <div
                                                            className="w-4 h-4 rounded-md"
                                                            style={{ backgroundColor: cycle.tag.color }}
                                                            aria-label={`Tag color: ${cycle.tag.name}`}
                                                        />
                                                        <span className="text-black truncate max-w-[100px]" title={cycle.title}>
                                                            {cycle.title}
                                                        </span>
                                                    </div>
                                                ))
                                            )}
                                        </div>
                                    </td>
                                );
                            })}
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    }

    // 태그 리스트 필터링 함수
    function RenderTagList({ tag }: { tag: CycleTagResponseDTO }) {
        return <div className="flex items-center gap-4 p-2 border border-gray-300 rounded-lg shadow-sm mb-2 bg-white hover:bg-gray-50 transition-colors duration-300">
            <div className="flex items-center gap-2">
                <div
                    className="w-4 h-4 rounded-md"
                    style={{ backgroundColor: tag.color }}
                    aria-label={`Tag color: ${tag.name}`}>
                </div>
                <span className="text-sm font-medium">{tag.name}</span>
            </div>
            <div className="flex-grow"></div>
            <button
                onClick={() => handleEditTag(tag)}
                className="px-2 py-1 text-blue-500 hover:bg-blue-100 rounded-md border border-blue-300 text-xs transition-colors duration-300">
                수정
            </button>
            <button
                onClick={() => {
                    deleteTag(tag.id)
                        .then(() => getTagList(statusid))
                        .then(r => setTagList(r))
                        .catch(e => console.log(e));
                }}
                className="px-2 py-1 text-red-500 hover:bg-red-100 rounded-md border border-red-300 text-xs transition-colors duration-300">
                삭제
            </button>
        </div>
    }

    // 초기 데이터 로드
    useEffect(() => {
        if (ACCESS_TOKEN) {
            getUser().then(r => {
                setUser(r);
                setClientLoading(false);
                loadSchedules(); // 초기 로드 시 스케줄 불러오기
            }).catch(e => {
                setClientLoading(false);
                console.log(e);
            });
        } else {
            location.href = '/';
        }
    }, [ACCESS_TOKEN]);

    useEffect(() => {
        if (selectedDate)
            loadSchedules(); // 초기 로드 시 스케줄 불러오기
    }, [selectedDate]);


    useEffect(() => {
        if (selectedDate) {
            const result = [];
            const start = new Date(selectedDate);
            start.setDate(selectedDate.getDate() - 2);
            for (let i = 0; i < 5; i++) {
                const date = new Date(start);
                date.setDate(start.getDate() + i);
                result.push({
                    dateStr: getScheduleDate(date), // YYYY-MM-DD 형식
                    isToday: date.toDateString() === new Date().toDateString(),
                    dayOfMonth: date.getDate()
                });
            }
            setHeaderDate(result);
        } else
            setHeaderDate([]);
    }, [selectedDate]);

    useEffect(() => {
        if (isModalOpen && selectCycle) {
            setTitle(selectCycle.title);
            setContent(selectCycle.content);
            setStartDateInput(new Date(transferLocalTime(new Date(selectCycle.startDate))).toISOString().slice(0, 16));
            setEndDateInput(new Date(transferLocalTime(new Date(selectCycle.endDate))).toISOString().slice(0, 16));
        } else {
            setTitle('');
            setContent('');
            setStartDateInput('');
            setEndDateInput('');
        }
    }, [isModalOpen, selectCycle]);

    // useEffect(() => {
    //     fetchSchedules(startDate, endDate, statusid)
    //         .then(data => {
    //             setSchedules(data);
    //             setUpcomingCycles(getUpcomingCycles(data));
    //         })
    //         .catch(error => console.error('Error fetching schedules:', error));
    // }, [startDate, endDate, statusid]);

    return (
        <Main user={user} isClientLoading={isClientLoading}>
            <div className="flex bg-white w-full p-2">
                {/* Calendar Sidebar */}
                <div className="w-[20%] border-4 rounded-tl-lg rounded-bl-lg">
                    {/* Month Navigation */}
                    <div className="w-[370px] flex justify-end items-center p-2">
                        <button className="font-bold text-xl" onClick={() => changeMonth(-1)}>▴</button>
                        <button className="font-bold text-xl" onClick={() => changeMonth(1)}>▾</button>
                    </div>

                    {/* Calendar Table */}
                    <table>
                        <thead>
                            <tr className="w-[50px] h-[50px]">
                                <th>일</th>
                                <th>월</th>
                                <th>화</th>
                                <th>수</th>
                                <th>목</th>
                                <th>금</th>
                                <th>토</th>
                            </tr>
                        </thead>
                        <tbody className="text-center border-b-4 border-gray-300">
                            {[0, 1, 2, 3, 4, 5].map((week) => (
                                <tr key={week} className="border-t border-gray-300">
                                    {[0, 1, 2, 3, 4, 5, 6].map((dayIndex) => {
                                        const index = week * 7 + dayIndex;
                                        const date = new Date(startDate.getTime() + index * 1000 * 60 * 60 * 24);
                                        const isWithinRange = () => {
                                            if (cycleStartDate && cycleEndDate) {
                                                const cycleStart = new Date(cycleStartDate);
                                                const cycleEnd = new Date(cycleEndDate);
                                                const current = new Date(date);
                                                // 날짜의 시간 부분을 00:00:00으로 설정
                                                cycleStart.setHours(0, 0, 0, 0);
                                                cycleEnd.setHours(23, 59, 59, 999);
                                                return current >= cycleStart && current <= cycleEnd;
                                            }
                                            return false;
                                        };
                                        const isHolidayDate = isHoliday(date);
                                        const isSunday = dayIndex === 0; // 일요일
                                        const isSaturday = dayIndex === 6; // 토요일
                                        return (
                                            <td
                                                key={dayIndex}
                                                className={`p-0 border border-gray-200 rounded transition-colors duration-200
                                                    ${isHolidayDate ? "text-red-500" : isWithinRange() ? "bg-purple-300 text-white" : "bg-white text-gray-800"}
                                                    ${isSunday ? "text-red-500" : ""}
                                                    ${isSaturday ? "text-blue-700" : ""}
                                                    hover:bg-purple-100 hover:text-gray-800`}
                                                style={{ minWidth: "16px", minHeight: "16px", fontSize: "0.8rem", lineHeight: "1" }}
                                                onClick={() => handleDateClick(date)}>
                                                <DateColumn date={date} />
                                            </td>
                                        );
                                    })}
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    {/* Tag List */}
                    {/* {tagList?.map((tag: CycleTagResponseDTO, index: number) => (
                        <div key={index} className="flex items-center gap-4 p-2 border border-gray-300 rounded-lg shadow-sm mb-2 bg-white hover:bg-gray-50 transition-colors duration-300">
                            <div className="flex items-center gap-2">
                                <div
                                    className="w-4 h-4 rounded-md"
                                    style={{ backgroundColor: tag.color }}
                                    aria-label={`Tag color: ${tag.name}`}>
                                </div>
                                <span className="text-sm font-medium">{tag.name}</span>
                            </div>
                            <div className="flex-grow"></div>
                            <button
                                onClick={() => handleEditTag(tag)}
                                className="px-2 py-1 text-blue-500 hover:bg-blue-100 rounded-md border border-blue-300 text-xs transition-colors duration-300">
                                수정
                            </button>
                            <button
                                onClick={() => {
                                    deleteTag(tag.id)
                                        .then(() => getTagList(statusid))
                                        .then(r => setTagList(r))
                                        .catch(e => console.log(e));
                                }}
                                className="px-2 py-1 text-red-500 hover:bg-red-100 rounded-md border border-red-300 text-xs transition-colors duration-300">
                                삭제
                            </button>
                        </div>
                    ))} */}
                    <div>
                        <div className="collapse collapse-plus official-color" onClick={() => {
                            getTagList(0).then(r => setTagList(r)).catch(e => console.log(e));
                        }}>
                            <input type="radio" name="my-accordion-3" id="personal" defaultChecked />
                            <label htmlFor="personal" className="collapse-title text-xl font-medium">
                                개인
                            </label>
                            <div className="collapse-content">
                                {tagList.length !== 0 ? tagList?.map((t: CycleTagResponseDTO, index: number) => (
                                    <RenderTagList key={index} tag={t} />
                                )) : <></>}
                            </div>
                        </div>

                        <div className="collapse collapse-plus official-color" onClick={() => {
                            getTagList(1).then(r => setTagList(r)).catch(e => console.log(e));
                        }}>
                            <input type="radio" name="my-accordion-3" id="group" />
                            <label htmlFor="group" className="collapse-title text-xl font-medium">
                                그룹
                            </label>
                            <div className="collapse-content">
                                {tagList.length !== 0 ? tagList?.map((t: CycleTagResponseDTO, index: number) => (
                                    <RenderTagList key={index} tag={t} />
                                )) : <></>}
                            </div>
                        </div>

                        {/* <div className="collapse collapse-plus official-color">
                            <input type="radio" name="my-accordion-3" id="team" />
                            <label htmlFor="personal" className="collapse-title text-xl font-medium" onClick={() => {
                                getTagList(2).then(r => setTagList(r)).catch(e => console.log(e));
                            }}>
                                팀
                            </label>
                            <div className="collapse-content">
                                {tagList.length !== 0 ? tagList?.map((t: CycleTagResponseDTO, index: number) => (
                                    <RenderTagList key={index} tag={t} />
                                )) : <></>}
                            </div>
                        </div> */}
                    </div>
                </div>

                {/* Schedule Area */}
                <div className="border-t-4 border-b-4 w-[60%]">
                    <div className="flex justify-start items-center p-2">
                        <span className="text-4xl">
                            <span className="font-bold text-4xl">{monthNames[month]}</span> {year}
                        </span>
                    </div>
                    <div className="overflow-y-auto max-h-[822px]" style={{ scrollbarWidth: 'none' }}>
                        <ScheduleTable />
                    </div>
                </div>

                {/* Details and Modals */}
                <div className="border-t-4 w-[20%]">
                    {/* Schedule Creation and Editing Buttons */}
                    <div className="flex flex-col h-[10%] border-r-4 border-l-4 border-b-4">
                        <button
                            className="h-full border-b-2 border-r-2 border-l-2 flex items-center justify-center cursor-pointer text-sm font-medium text-blue-500"
                            onClick={() => {
                                setSelectCycle(null);  // 선택된 일정 초기화
                                setIsModalOpen(true);
                            }}>
                            일정 생성
                        </button>
                    </div>

                    {/* Search Section */}
                    <div className="h-[50%] border-r-4 border-l-4 border-b-4">upcoming cycle 15
                        <div className="h-[50%] border-r-4 border-l-4 border-b-4 p-4">
                            <h2 className="text-xl font-bold mb-2">Upcoming Cycles</h2>
                            <ul>
                                {upcomingCycles.length > 0 ? (
                                    upcomingCycles.map((cycle, index) => (
                                        <li key={index} className="p-2 border-b border-gray-200">
                                            {cycle.title}
                                        </li>
                                    ))
                                ) : (
                                    <li className="p-2 text-gray-500">Upcoming cycles are not available.</li>
                                )}
                            </ul>
                        </div>
                    </div>

                    {/* Schedule Details */}
                    <div className="h-[40%] border-r-4 border-l-4 border-b-4 p-4">
                        <h2 className="text-xl font-bold mb-2">일정 디테일</h2>
                        {selectCycle ? (
                            <div>
                                <div><strong>제목:</strong> {selectCycle.title}</div>
                                <div><strong>내용:</strong> {selectCycle.content}</div>
                                <div><strong>시작 시간:</strong> {getDateEmailTime(new Date(selectCycle.startDate))}</div>
                                <div><strong>종료 시간:</strong> {getDateEmailTime(new Date(selectCycle.endDate))}</div>
                                {selectCycle.tag && selectCycle.tag.color && selectCycle.tag.name ? (
                                    <div className="flex items-center space-x-2">
                                        <div
                                            className="w-4 h-4 rounded-md"
                                            style={{ backgroundColor: selectCycle.tag.color }}
                                            aria-label={`Tag color: ${selectCycle.tag.name}`}
                                        />
                                        <span>{selectCycle.tag.name}</span>
                                    </div>
                                ) : null}
                                <div className="flex flex-col justify-end items-end h-full">
                                    <div className="flex mt-4">
                                        {/* 일정 수정 버튼 */}
                                        <button
                                            className="px-2 py-1 text-blue-500 hover:bg-blue-100 mr-2 rounded-md border border-blue-300 text-xs transition-colors duration-300"
                                            disabled={!selectCycle}
                                            onClick={() => selectCycle && setIsModalOpen(true)}>
                                            일정 수정
                                        </button>

                                        {/* 삭제 버튼 */}
                                        <button
                                            className="px-2 py-1 text-red-500 hover:bg-red-100 rounded-md border border-red-300 text-xs transition-colors duration-300"
                                            disabled={!selectCycle}
                                            onClick={() => selectCycle && handleDeleteSchedule(selectCycle.id)}>
                                            삭제
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ) : null}
                    </div>
                </div>

                {isModalOpen && (
                    <div className="fixed top-0 left-0 right-0 bottom-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
                        <div className="bg-white p-4 rounded-lg w-[400px]">
                            <div className="text-lg font-bold border-b-2 mb-4">
                                {selectCycle ? '일정 수정' : '일정 생성'}
                            </div>
                            <div className="flex flex-col gap-4">
                                <label>
                                    제목:
                                    <input
                                        type="text"
                                        defaultValue={selectCycle ? title : ''}
                                        onChange={(e) => setTitle(e.target.value)}
                                        className="w-full p-2 border border-gray-300 rounded"
                                        required />
                                </label>
                                <label>
                                    내용:
                                    <textarea
                                        defaultValue={selectCycle ? content : ''}
                                        onChange={(e) => setContent(e.target.value)}
                                        className="w-full p-2 border border-gray-300 rounded"
                                        rows={4}
                                        required />
                                </label>
                                <label>
                                    시작 날짜 및 시간:
                                    <input
                                        type="datetime-local"
                                        defaultValue={selectCycle ? startDateInput : ''}
                                        onChange={(e) => setStartDateInput(e.target.value)}
                                        className="w-full p-2 border border-gray-300 rounded"
                                        required />
                                </label>
                                <label>
                                    종료 날짜 및 시간:
                                    <input
                                        type="datetime-local"
                                        defaultValue={selectCycle ? endDateInput : ''}
                                        onChange={(e) => setEndDateInput(e.target.value)}
                                        className="w-full p-2 border border-gray-300 rounded"
                                        required />
                                </label>
                                <label>
                                    태그:
                                    <input
                                        type="text"
                                        defaultValue={selectCycle ? tags.join(', ') : ''}
                                        onChange={(e) => setTags(e.target.value.split(',').map(tag => tag.trim()))}
                                        className="w-full p-2 border border-gray-300 rounded"
                                        placeholder="태그를 입력하세요 (쉼표로 구분)" />
                                </label>
                                <label>
                                    색상:
                                    <input
                                        type="color"
                                        defaultValue={selectCycle ? color : '#000000'}
                                        onChange={(e) => setColor(e.target.value)}
                                        className="w-full p-2 border border-gray-300 rounded" />
                                </label>
                                <div className="flex gap-4">
                                    <label>
                                        <input
                                            type="radio"
                                            name="status"
                                            defaultValue="0"
                                            checked={statusid === 0}
                                            onChange={() => setStatusid(0)} />
                                        개인
                                    </label>
                                    <label>
                                        <input
                                            type="radio"
                                            name="status"
                                            defaultValue="1"
                                            checked={statusid === 1}
                                            onChange={() => setStatusid(1)} />
                                        그룹
                                    </label>
                                    <label>
                                        <input
                                            type="radio"
                                            name="status"
                                            defaultValue="2"
                                            checked={statusid === 2}
                                            onChange={() => setStatusid(2)} />
                                        팀 (미구현)
                                    </label>
                                </div>
                                <div className="flex justify-end gap-2 mt-4">
                                    <button
                                        type="button"
                                        onClick={() => setIsModalOpen(false)}
                                        className="px-4 py-2 bg-gray-400 text-white rounded-md">
                                        닫기
                                    </button>
                                    <button
                                        type="button"
                                        onClick={handleSaveSchedule}
                                        className="px-4 py-2 bg-blue-500 text-white rounded-md">
                                        저장
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {/* Tag Modal */}
                {isTagModalOpen && selectedTag && (
                    <div className="fixed top-0 left-0 right-0 bottom-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
                        <div className="bg-white p-4 rounded-lg w-[400px]">
                            <div className="text-lg font-bold border-b-2 mb-4">
                                태그 수정
                            </div>
                            <div className="flex flex-col gap-4">
                                <label>
                                    태그 이름:
                                    <input
                                        type="text"
                                        value={selectedTag.name}
                                        onChange={(e) => setSelectedTag({ ...selectedTag, name: e.target.value })}
                                        className="w-full p-2 border border-gray-300 rounded"
                                        required />
                                </label>
                                <label>
                                    태그 색상:
                                    <input
                                        type="color"
                                        value={selectedTag.color}
                                        onChange={(e) => setSelectedTag({ ...selectedTag, color: e.target.value })}
                                        className="w-full p-2 border border-gray-300 rounded" />
                                </label>
                                <div className="flex justify-end gap-2 mt-4">
                                    <button
                                        type="button"
                                        onClick={() => setIsTagModalOpen(false)}
                                        className="px-4 py-2 bg-gray-400 text-white rounded-md">
                                        닫기
                                    </button>
                                    <button
                                        type="button"
                                        onClick={handleSaveTag}
                                        className="px-4 py-2 bg-blue-500 text-white rounded-md">
                                        저장
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </Main>
    );
}