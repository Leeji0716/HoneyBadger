"use client"; // 클라이언트 전용 컴포넌트로 설정

import React, { useState, useEffect } from "react";
import { getUser, createSchedule, fetchSchedules } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";

export default function Cycle() {
    const [user, setUser] = useState<any>(null);
    const ACCESS_TOKEN = typeof window === 'undefined' ? null : localStorage.getItem('accessToken');
    const [isClientLoading, setClientLoading] = useState(true);
    const [selectedDateIndex, setSelectedDateIndex] = useState<number | null>(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectDate, setSelectDate] = useState(new Date());
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [startDateInput, setStartDateInput] = useState<string>("");
    const [endDateInput, setEndDateInput] = useState<string>("");
    const [schedules, setSchedules] = useState<PersonalCycleDTO[]>([]);

    interface PersonalCycleDTO {
        id: number;
        title: string;
        content: string;
        startDate: number;
        endDate: number;
        tag: string[];
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
    }, [ACCESS_TOKEN, selectDate]);

    const loadSchedules = async () => {
        try {
            const { startDate, endDate } = getFetchDateRange();
            // 콘솔에 날짜 범위 출력
            console.log(`Fetching schedules from: ${startDate.toISOString()} to: ${endDate.toISOString()}`);
            const data = await fetchSchedules(startDate, endDate);
            setSchedules(data.personalCycles);
        } catch (error) {
            console.error("Failed to fetch schedules:", error);
        }
    };

    const getFetchDateRange = () => {
        const headerDates = getHeaderDates();
        const startDate = new Date(headerDates[0].dateStr); // 첫 번째 날짜
        const endDate = new Date(headerDates[headerDates.length - 1].dateStr); // 마지막 날짜
    
        // endDate를 하루 더 추가하여 범위에 포함되도록 함
        endDate.setDate(endDate.getDate() + 1);
    
        // 날짜 범위 콘솔 로그
        console.log(`Date range for fetching schedules: ${startDate.toISOString()} to ${endDate.toISOString()}`);
    
        return { startDate, endDate };
    };

    const year = selectDate.getFullYear();
    const month = selectDate.getMonth();

    const firstDate = new Date(year, month, 1);
    const startDate = new Date(firstDate.getTime() - firstDate.getDay() * 1000 * 60 * 60 * 24);
    const endDate = new Date(startDate.getTime() + 7 * 6 * 60 * 60 * 24 * 1000);

    const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

    const changeMonth = (delta: number) => {
        setSelectDate(new Date(selectDate.getFullYear(), selectDate.getMonth() + delta, 1));
        setSelectedDateIndex(null);
    };

    const handleDateClick = (index: number) => {
        setSelectedDateIndex(index);
    };

    function DateColumn({ index }: { index: number }) {
        const date = new Date(startDate.getTime() + index * 1000 * 60 * 60 * 24);
        const now = new Date();
        const isToday = date.toDateString() === now.toDateString();
        const isSelected = selectedDateIndex !== null && selectedDateIndex === index;
        const isAdjacentToSelected = selectedDateIndex !== null && index >= selectedDateIndex - 2 && index <= selectedDateIndex + 2;

        return (
            <div
                onClick={() => handleDateClick(index)}
                className={`w-[51px] h-[41px] rounded-2xl flex items-center justify-center cursor-pointer
                    ${date.getMonth() !== selectDate.getMonth() ? 'opacity-25' : 'opacity-100'}
                    ${isToday ? 'bg-blue-500 text-white' : ''}
                    ${isSelected ? 'border-4 border-red-500' : ''}
                    ${isAdjacentToSelected && !isSelected ? 'border-4 border-gray-500' : ''}`}
            >
                {date.getDate()}
            </div>
        );
    }

    function getHeaderDates() {
        const result = [];
        const selectedDate = new Date(startDate.getTime() + (selectedDateIndex ?? 0) * 1000 * 60 * 60 * 24);
        const today = new Date();
        for (let i = -2; i <= 2; i++) {
            const date = new Date(selectedDate.getTime() + i * 1000 * 60 * 60 * 24);
            const day = date.toLocaleDateString('en-US', { weekday: 'short' });
            const dayOfMonth = date.getDate();
            result.push({
                dateStr: `${date.toISOString().split('T')[0]}`, // YYYY-MM-DD 형식
                isToday: date.getFullYear() === today.getFullYear() && date.getMonth() === today.getMonth() && date.getDate() === today.getDate()
            });
        }
        return result;
    }

    function getTimeSlots() {
        return Array.from({ length: 24 }, (_, i) => `${i}:00`);
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!title || !content || !startDateInput || !endDateInput) {
            alert("모든 필드를 입력해 주세요.");
            return;
        }

        try {
            await createSchedule({
                title,
                content,
                startDate: new Date(startDateInput),
                endDate: new Date(endDateInput),
            });
            alert("일정이 성공적으로 생성되었습니다!");
            setIsModalOpen(false); // 제출 후 모달 닫기
            loadSchedules(); // 새로 생성된 일정 포함하여 스케줄 새로 고침
        } catch (error) {
            console.error("일정 생성 실패:", error);
            alert("오류가 발생했습니다. 다시 시도해 주세요.");
        }
    };

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
                        <tbody className="text-center border-b-4">
                            {[0, 1, 2, 3, 4, 5].map((week) => (
                                <tr key={week}>
                                    {[0, 1, 2, 3, 4, 5, 6].map((dayIndex) => {
                                        const index = week * 7 + dayIndex;
                                        return (
                                            <td key={dayIndex}>
                                                <DateColumn index={index} />
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
                        <table className="w-full border-collapse">
                            <thead>
                                <tr>
                                    <th className="p-2 border">Time</th>
                                    {getHeaderDates().map((headerDate, index) => (
                                        <th key={index} className={`p-2 border ${headerDate.isToday ? 'bg-red-500 text-white' : ''}`}>
                                            {new Date(headerDate.dateStr).toLocaleDateString('en-US', { weekday: 'short', day: 'numeric' })}
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
                                                {/* 일정 내용 표시 */}
                                                {(schedules ?? []).filter(schedule => {
                                                    const scheduleDate = new Date(schedule.startDate);
                                                    return scheduleDate.toDateString() === new Date(headerDate.dateStr).toDateString() &&
                                                        scheduleDate.getHours() === slotIndex;
                                                }).map(schedule => (
                                                    <div key={schedule.id}>
                                                        {schedule.title}
                                                    </div>
                                                ))}
                                            </td>
                                        ))}
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="border-t-4 w-[20%]">
                    <div
                        className="h-[10%] border-b-4 border-r-4 border-l-4 flex items-center justify-center cursor-pointer text-sm font-medium text-blue-500"
                        onClick={() => setIsModalOpen(true)}>일정 생성
                    </div>
                    <div className="h-[30%] border-r-4 border-l-4 border-b-4">검색</div>
                    <div className="h-[60%] border-r-4 border-l-4 border-b-4">일정 디테일</div>
                </div>
            </div>

            {/* 모달 부분 */}
            {isModalOpen && (
                <div className="fixed top-0 left-0 right-0 bottom-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
                    <div className="bg-white p-4 rounded-lg w-[400px]">
                        <div className="text-lg font-bold border-b-2 mb-4">일정 생성</div>
                        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                            <label>
                                제목:
                                <input
                                    type="text"
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                    className="w-full p-2 border border-gray-300 rounded"
                                    required />
                            </label>
                            <label>
                                내용:
                                <textarea
                                    value={content}
                                    onChange={(e) => setContent(e.target.value)}
                                    className="w-full p-2 border border-gray-300 rounded"
                                    rows={4}
                                    required />
                            </label>
                            <label>
                                시작 날짜 및 시간:
                                <input
                                    type="datetime-local"
                                    value={startDateInput}
                                    onChange={(e) => setStartDateInput(e.target.value)}
                                    className="w-full p-2 border border-gray-300 rounded"
                                    required />
                            </label>
                            <label>
                                종료 날짜 및 시간:
                                <input
                                    type="datetime-local"
                                    value={endDateInput}
                                    onChange={(e) => setEndDateInput(e.target.value)}
                                    className="w-full p-2 border border-gray-300 rounded"
                                    required />
                            </label>
                            <div className="flex justify-end gap-2 mt-4">
                                <button
                                    type="button"
                                    className="bg-gray-500 text-white p-2 rounded hover:bg-gray-600"
                                    onClick={() => setIsModalOpen(false)}>취소
                                </button>
                                <button
                                    type="submit"
                                    className="bg-blue-500 text-white p-2 rounded hover:bg-blue-600">일정 생성
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </Main>
    );
}