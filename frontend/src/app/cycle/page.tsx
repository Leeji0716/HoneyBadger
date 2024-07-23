"use client";

import React, { useState, useEffect } from "react";
import { getUser, createSchedule, fetchSchedules } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import { eontransferLocalTime, getDateEmailTime } from "../Global/Method";

export default function Cycle() {
    const [user, setUser] = useState<any>(null);
    const ACCESS_TOKEN = typeof window === 'undefined' ? null : localStorage.getItem('accessToken');
    const [isClientLoading, setClientLoading] = useState(true);
    const [selectedDate, setSelectedDate] = useState<Date | null>(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [startDateInput, setStartDateInput] = useState<string>("");
    const [endDateInput, setEndDateInput] = useState<string>("");
    const [schedules, setSchedules] = useState([] as any[]);
    const [daySchedules, setDaySchedules] = useState<PersonalCycleDTO[]>();
    const [selectCycle, setSelectCycle] = useState<PersonalCycleDTO>();
    const [cycleStartDate, setCycleStartDate] = useState<Date>(new Date());
    const [cycleEndDate, setCycleEndDate] = useState<Date>(new Date());

    const formatDateToISO = (date: Date): string => {
        return date.toISOString().split('T')[0]; // YYYY-MM-DD 형식으로 반환
    };

    const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setStartDateInput(e.target.value);
    };

    interface PersonalCycleDTO {
        id: number;
        title: string;
        content: string;
        startDate: number;
        endDate: number;
        tag: string[];
    }
    interface List {
        personalCycleDTOList: PersonalCycleDTO[],
        holiday: boolean,
        holidayTitle: string
    }

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
    }, [ACCESS_TOKEN, selectedDate]);

    const loadSchedules = async () => {
        if (selectedDate) {
            try {
                const { startDate, endDate } = getFetchDateRange();
                console.log(`Fetching schedules from: ${formatDateToISO(startDate)} to: ${formatDateToISO(endDate)}`);
                const data = await fetchSchedules(startDate, endDate);
                setSchedules(data);
            } catch (error) {
                console.error("Failed to fetch schedules:", error);
            }
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

    const year = selectedDate ? selectedDate.getFullYear() : new Date().getFullYear();
    const month = selectedDate ? selectedDate.getMonth() : new Date().getMonth();

    const firstDate = new Date(year, month, 1);
    const startDate = new Date(firstDate.getTime() - firstDate.getDay() * 1000 * 60 * 60 * 24);
    const endDate = new Date(startDate.getTime() + 7 * 6 * 60 * 60 * 24 * 1000);

    const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

    const changeMonth = (delta: number) => {
        setSelectedDate(new Date(year, month + delta, 1));
    };

    const handleDateClick = (date: Date) => {
        setSelectedDate(date);
    };

    function DateColumn({ date }: { date: Date }) {
        const now = new Date();
        const isToday = date.toDateString() === now.toDateString();
        const isSelected = selectedDate && date.toDateString() === selectedDate.toDateString();
        const isAdjacentToSelected = selectedDate &&
            date >= new Date(selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate() - 2) &&
            date <= new Date(selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate() + 2);

        return (
            <div
                onClick={() => { console.log(date); handleDateClick(date); }}
                className={`w-[51px] h-[41px] rounded-2xl flex items-center justify-center cursor-pointer
                    ${date.getMonth() !== month ? 'opacity-25' : 'opacity-100'}
                    ${isToday ? 'bg-blue-500 text-white' : ''}
                    ${isSelected ? 'border-4 border-red-500' : ''}`}>
                {date.getDate()}
            </div>
        );
    }

    function getHeaderDates() {
        if (selectedDate) {
            const result = [];
            const start = new Date(selectedDate);
            start.setDate(selectedDate.getDate() - 2);

            for (let i = 0; i < 5; i++) {
                const date = new Date(start);
                date.setDate(start.getDate() + i);
                result.push({
                    dateStr: date.toISOString().split('T')[0], // YYYY-MM-DD 형식
                    isToday: date.toDateString() === new Date().toDateString(),
                    dayOfMonth: date.getDate()
                });
            }
            return result;
        }
        return [];
    }

    function getTimeSlots() {
        return Array.from({ length: 24 }, (_, i) => `${i}:00`);
    }

    const handleCreateSchedule = async () => {
        if (!title || !content || !startDateInput || !endDateInput) {
            alert("모든 필드를 입력해 주세요.");
            return;
        }

        try {
            await createSchedule({
                title,
                content,
                startDate: eontransferLocalTime(new Date(startDateInput)),
                endDate: eontransferLocalTime(new Date(endDateInput))
            });
            alert("일정이 성공적으로 생성되었습니다!");
            setIsModalOpen(false); // 제출 후 모달 닫기
            loadSchedules(); // 새로 생성된 일정 포함하여 스케줄 새로 고침
        } catch (error) {
            console.error("일정 생성 실패:", error);
            alert("오류가 발생했습니다. 다시 시도해 주세요.");
        }
    };

    function ScheduleTable() {
        return (
            <table className="w-full border-collapse">
                <thead>
                    <tr>
                        <th className="p-2 border">Time</th>
                        {getHeaderDates().map((headerDate, index) => (
                            <th key={index} className={`p-2 border ${headerDate.isToday ? 'bg-red-500 text-white' : ''}`}>
                                {new Date(headerDate.dateStr).toLocaleDateString('ko-KR', { weekday: 'short', day: 'numeric' })}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody className="text-center">
                    {getTimeSlots().map((slot, slotIndex) => (
                        <tr key={slotIndex}>
                            <td className="border">{slot}</td>
                            {getHeaderDates().map((headerDate, dateIndex) => (
                                <td key={`${dateIndex}-${slotIndex}`} className="border h-[60px]">
                                    {(schedules ?? []).flatMap((schedule: List) => {
                                        return schedule.personalCycleDTOList.filter(cycle => {
                                            const cycleStart = new Date(cycle.startDate);
                                            const cycleEnd = new Date(cycle.endDate);
                                            const date = new Date(headerDate.dateStr);
                                            return cycleStart.toDateString() === date.toDateString() &&
                                                cycleStart.getHours() === slotIndex;
                                        });
                                    }).map((cycle: PersonalCycleDTO, index: number) => (
                                        <div key={index} className="cursor-pointer" onClick={() => { setSelectCycle(cycle); setCycleStartDate(new Date(cycle.startDate)); setCycleEndDate(new Date(cycle.endDate)); }}>
                                            {cycle.title}
                                        </div>
                                    ))}
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    }

    return (
        <Main user={user} isClientLoading={isClientLoading}>
            <div className="flex bg-white w-full p-2">
                <div className="w-[20%] border-4 rounded-tl-lg rounded-bl-lg">
                    <div className="w-[370px] flex justify-end items-center p-2">
                        <button className="font-bold text-xl" onClick={() => changeMonth(-1)}>▴</button>
                        <button className="font-bold text-xl" onClick={() => changeMonth(1)}>▾</button>
                    </div>
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
                                        const isToday = date.toDateString() === new Date().toDateString(); // 현재 날짜 비교
                                        const isWithinRange = cycleStartDate <= date && cycleEndDate >= date;

                                        return (
                                            <td
                                                key={dayIndex}
                                                className={`p-0 border border-gray-200 rounded transition-colors duration-200 ${isWithinRange ? "bg-purple-300 text-white" : "bg-white text-gray-800"
                                                    } hover:bg-purple-100 hover:text-gray-800`}
                                                style={{ minWidth: "16px", minHeight: "16px", fontSize: "0.8rem", lineHeight: "1" }}
                                            >
                                                <DateColumn date={date} />
                                            </td>
                                        );
                                    })}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

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

                <div className="border-t-4 w-[20%]">
                    <div
                        className="h-[10%] border-b-4 border-r-4 border-l-4 flex items-center justify-center cursor-pointer text-sm font-medium text-blue-500"
                        onClick={() => setIsModalOpen(true)}>일정 생성
                    </div>
                    <div className="h-[30%] border-r-4 border-l-4 border-b-4">검색</div>
                    <div className="h-[60%] border-r-4 border-l-4 border-b-4 p-4">
                        <h2 className="text-xl font-bold mb-2">일정 디테일</h2>
                        {selectCycle ?
                            <div>
                                <p><strong>제목:</strong>{selectCycle?.title} </p>
                                <p><strong>내용:</strong>{selectCycle?.content} </p>
                                <p><strong>시작 시간:</strong>{getDateEmailTime(selectCycle?.startDate)} </p>
                                <p><strong>종료 시간:</strong>{getDateEmailTime(selectCycle?.endDate)} </p>
                                <div className="flex mt-4">
                                    <button className="px-4 py-2 bg-yellow-500 text-white rounded-md mr-2">수정</button>
                                    <button className="px-4 py-2 bg-red-500 text-white rounded-md">삭제</button>
                                </div>
                            </div>
                            :
                            <></>
                        }
                    </div>
                </div>
            </div>

            {isModalOpen && (
                <div className="fixed top-0 left-0 right-0 bottom-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
                    <div className="bg-white p-4 rounded-lg w-[400px]">
                        <div className="text-lg font-bold border-b-2 mb-4">일정 생성</div>
                        <div className="flex flex-col gap-4">
                            <label>
                                제목:
                                <input
                                    type="text"
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                    className="w-full p-2 border border-gray-300 rounded"
                                    required
                                />
                            </label>
                            <label>
                                내용:
                                <textarea
                                    value={content}
                                    onChange={(e) => setContent(e.target.value)}
                                    className="w-full p-2 border border-gray-300 rounded"
                                    rows={4}
                                    required
                                />
                            </label>
                            <label>
                                시작 날짜 및 시간:
                                <input
                                    type="datetime-local"
                                    value={startDateInput}
                                    onChange={(e) => setStartDateInput(e.target.value)}
                                    className="w-full p-2 border border-gray-300 rounded"
                                    required
                                />
                            </label>
                            <label>
                                종료 날짜 및 시간:
                                <input
                                    type="datetime-local"
                                    value={endDateInput}
                                    onChange={(e) => setEndDateInput(e.target.value)}
                                    className="w-full p-2 border border-gray-300 rounded"
                                    required
                                />
                            </label>
                            <div className="flex justify-end gap-2 mt-4">
                                <button
                                    type="button"
                                    className="bg-gray-500 text-white p-2 rounded hover:bg-gray-600"
                                    onClick={() => setIsModalOpen(false)}
                                >
                                    취소
                                </button>
                                <button
                                    type="button"
                                    className="bg-blue-500 text-white p-2 rounded hover:bg-blue-600"
                                    onClick={handleCreateSchedule}
                                >
                                    일정 생성
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </Main>
    );
}
