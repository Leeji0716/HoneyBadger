"use client";
import { getUser } from "@/app/API/UserAPI";
import Main from "@/app/Global/Layout/MainLayout";
import { useEffect, useState } from "react";

export default function Cycle() {
    const [user, setUser] = useState(null as any);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [isClientLoading, setClientLoading] = useState(true);
    const [selectedDateIndex, setSelectedDateIndex] = useState<number | null>(null); // 상태 변수 추가

    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
            }).catch(e => { setClientLoading(false); console.log(e); });
        else
            location.href = '/';
    }, [ACCESS_TOKEN])

    const [selectDate, setSelectDate] = useState(new Date());
    const year = selectDate.getFullYear();
    const month = selectDate.getMonth();

    const firstDate = new Date(year, month, 1);
    const startDate = new Date(firstDate.getTime() - firstDate.getDay() * 1000 * 60 * 60 * 24); // 데이터 받아오기용
    const endDate = new Date(startDate.getTime() + 7 * 6 * 60 * 60 * 24 * 1000); // 데이터 받아오기용

    const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

    const changeMonth = (delta: number) => {
        setSelectDate(new Date(selectDate.getFullYear(), selectDate.getMonth() + delta, 1));
        setSelectedDateIndex(null); // 달이 변경될 때 선택된 날짜 초기화
    };

    const handleDateClick = (index: number) => {
        setSelectedDateIndex(index);
    };

    function DateColumn({ index }: { index: number }) {
        const date = new Date(startDate.getTime() + index * 1000 * 60 * 60 * 24);
        const now = new Date;
        const isToday = date.getFullYear() === now.getFullYear() && date.getMonth() === now.getMonth() && date.getDate() === now.getDate();
        const isSelected = selectedDateIndex === index;
        const isAdjacentToSelected = selectedDateIndex !== null && (index >= selectedDateIndex - 2 && index <= selectedDateIndex + 2);

        return (
            <div
                onClick={() => handleDateClick(index)}
                className={`w-[51px] h-[41px] rounded-2xl flex items-center border-radius justify-center cursor-pointer
                    ${date.getMonth() !== selectDate.getMonth() ? 'opacity-25' : 'opacity-100'}
                    ${isToday ? 'official-color' : ''}
                    ${isSelected ? 'border-2 border-red-500' : ''}
                    ${isAdjacentToSelected && !isSelected ? 'border-2 border-gray-500' : ''}`}>
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
                dateStr: `${day} ${dayOfMonth}`,
                isToday: date.getFullYear() === today.getFullYear() && date.getMonth() === today.getMonth() && date.getDate() === today.getDate()
            });
        }
        return result;
    }

    function getTimeSlots() {
        return Array.from({ length: 24 }, (_, i) => `${i}:00`);
    }

    return (
        <Main user={user} isClientLoading={isClientLoading}>
            <div className="flex bg-white w-full p-6">
                <div className="w-[20%] border-2 rounded-tl-lg rounded-bl-lg">
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
                        <tbody className="text-center border-b-2">
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

                <div className="border-t-2 border-b-2 w-[60%]">
                    <div className="flex justify-start items-center p-4">
                        <span className="text-4xl">
                            <span className="font-bold text-4xl">{monthNames[month]}</span> {year}
                        </span>
                    </div>
                    <div className="overflow-y-auto max-h-[780px]">
                        <table className="w-full border-collapse">
                            <thead>
                                <tr>
                                    <th className="p-2 border">Time</th>
                                    {getHeaderDates().map((headerDate, index) => (
                                        <th key={index} className={`p-2 border ${headerDate.isToday ? 'bg-red-500 text-white' : ''}`}>
                                            {headerDate.dateStr}
                                        </th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody className="text-center border-b-2">
                                {getTimeSlots().map((slot, slotIndex) => (
                                    <tr key={slotIndex}>
                                        <td className="border">{slot}</td>
                                        {getHeaderDates().map((headerDate, dateIndex) => (
                                            <td key={`${dateIndex}-${slotIndex}`} className="border h-[60px]">
                                                {/* 일정 내용 추가, 현재는 빈 칸 */}
                                            </td>
                                        ))}
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>

                <div className="w-[20%] border-2 rounded-tr-lg rounded-br-lg">
                    <div>검색</div>
                    <div>일정 생성</div>
                    <div>일정 디테일</div>
                </div>
            </div>
        </Main>
    );
}