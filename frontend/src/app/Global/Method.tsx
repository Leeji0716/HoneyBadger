import html2canvas from 'html2canvas-pro';
import KoreanLunarCalendar from "korean-lunar-calendar";

export function Move(id: string) {
    document.getElementById(id)?.focus();
}
export function KeyDownCheck({ preKey, setPreKey, e, pre, next }: { preKey: string, setPreKey: (value: any) => void, e: any, pre?: () => void, next?: () => void }) {
    if (pre && preKey != '' && e.key == 'Enter')
        pre();
    else if (next && preKey == '' && e.key == 'Enter')
        next();
    if (e.key == 'Shift')
        setPreKey('Shift');
    else if (preKey != null)
        setPreKey('');
}

export function PhoneString(phone: string) {
    return phone?.slice(0, 3) + '-' + phone?.slice(3, 7) + '-' + phone?.slice(7);
}
export function checkInput(check: any, pattern: string, True: () => void, False: () => void) {
    if (new RegExp(pattern).test(check.target.value))
        True();
    else
        False();
}
export function Check(pattern: string, test: string) {
    return new RegExp(pattern).test(test);
}
export function getDate(data: any) {
    const date = new Date(data);
    return date.getFullYear() + '.' + (date.getMonth() + 1) + '.' + date.getDate();
}
export function getDateKorean(data: any) {
    const date = new Date(data);
    return date.getFullYear() + '년 ' + (date.getMonth() + 1) + '월 ' + date.getDate() + '일';
}
export function getDateTimeKorean(data: any) {
    const date = new Date(data);
    return date.getFullYear() + '년 ' + (date.getMonth() + 1) + '월 ' + date.getDate() + '일 ' + date.getHours() + '시 ' + date.getMinutes() + '분 ' + date.getSeconds() + '초';
}
export function getDateTime(data: any) {
    const date = new Date(data);
    return date.getFullYear() + '' + (date.getMonth() + 1) + '' + date.getDate() + '' + date.getHours() + '' + date.getMinutes();
}
export function getDateTimeFormat(data: any) {
    const date = new Date(data);
    return date.getFullYear() + '-' + (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-' + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ' + (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':' + (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());
}
export function getDateTimeFormatInput(data: any) {
    const date = new Date(data);
    return date.getFullYear() + '-' + (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-' + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + 'T' + (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':' + (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());
}
export function getDateFormatInput(data: any) {
    const date = new Date(data);
    return date.getFullYear() + '-' + (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-' + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate());
}
export function transferLocalTime(date: Date) {
    return new Date(date.getTime() + 9 * 1000 * 60 * 60);

}

export function eontransferLocalTime(date: Date | null) {
    if (date != null) {
        return new Date(date.getTime() + 9 * 1000 * 60 * 60);
    } else {
        return null;
    }

}

export function eongetDateTimeFormat(data: any) {
    const date = new Date(data);
    return date;
}

export function getChatDateTimeFormat(data: any) {
    const date = new Date(data);
    const now = new Date();
    const isToday = date.getDate() === now.getDate() &&
        date.getMonth() === now.getMonth() &&
        date.getFullYear() === now.getFullYear();

    const hours = date.getHours();
    const amPm = hours >= 12 ? '오후' : '오전';
    const formattedHour = hours % 12 || 12;  // 0시는 12시로 표현

    const formattedTime = amPm + ' ' + (formattedHour < 10 ? '0' + formattedHour : formattedHour) + ':'
        + (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());

    if (isToday) {
        // 오늘 날짜인 경우 시간만 반환
        return formattedTime;
    } else {
        // 오늘이 아닌 경우 날짜와 시간 모두 반환
        return date.getFullYear() + '-' +
            (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-'
            + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' '
            + formattedTime;
    }
}

export function getChatShowDateTimeFormat(data: any) {
    const date = new Date(data);
    const now = new Date();
    const isToday = date.getDate() === now.getDate() &&
        date.getMonth() === now.getMonth() &&
        date.getFullYear() === now.getFullYear();

    const hours = date.getHours();
    const amPm = hours >= 12 ? '오후' : '오전';
    const formattedHour = hours % 12 || 12;  // 0시는 12시로 표현

    const formattedTime = amPm + ' ' + (formattedHour < 10 ? '0' + formattedHour : formattedHour) + ':'
        + (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());

    if (isToday) {
        // 오늘 날짜인 경우 시간만 반환
        return formattedTime;
    } else {
        // 오늘이 아닌 경우 날짜만 반환
        return date.getFullYear() + '-' +
            (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-'
            + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate());
    }
}

export function getDateEmailTime(data: any) {
    const date = new Date(data);
    const hours = date.getHours();
    const amPm = hours >= 12 ? '오후' : '오전';
    const formattedHour = hours % 12 || 12;  // 0시는 12시로 표현
    const formattedTime = amPm + " " + (formattedHour < 10 ? '0' + formattedHour : formattedHour) + ":"
        + (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());

    return date.getFullYear() + "년 " +
        (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + "월 "
        + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + "일 " + formattedTime;
}
export function getStorageDate(data: number) {
    const date = new Date(data);
    const now = new Date();
    
    if (now.getFullYear() == date.getFullYear() && now.getMonth() == date.getMonth() && now.getDate() == date.getDate())
        return "오늘 " + (date.getHours() < 12 ? '오전 ' + date.getHours() : '오후 ' + (date.getHours() - 12)) + ":" + date.getMinutes();
    else
        return date.getFullYear() + ". " + (date.getMonth() < 9 ? '0' : '') + (date.getMonth() + 1) + ". " + (date.getDate() < 9 ? '0' : 0) + date.getDate() + ". " + (date.getHours() < 12 ? '오전 ' + date.getHours() : '오후 ' + (date.getHours() - 12)) + ":" + date.getMinutes();


}
export function getDepartmentRole(role: number) {
    switch (role) {
        case 0: return '일반';
        case 1: return '인사'
    }
}
export function getRole(role: number) {
    switch (role) {
        case 0: return '사장'
        case 1: return '부사장'
        case 2: return '전무'
        case 3: return '상무'
        case 4: return '이사'
        case 5: return '사외 이사'
        case 6: return '고문'
        case 7: return '감사'
        case 8: return '부장'
        case 9: return '과장'
        case 10: return '대리'
        case 11: return '주임'
        case 12: return '직원'
        case 13: return '관리자'
    }
}
export function CardFront({ user }: { user: any }) {
    return <div className='border border-black w-[400px] h-[200px] flex flex-col cursor-pointer' onClick={(e) => {
        html2canvas(e.currentTarget as HTMLInputElement).then(canvas => {
            var el = document.createElement('a');
            el.href = canvas.toDataURL('image/png');
            el.download = '명함 앞면.png';
            el.click();
            // document.body.append(canvas)
        });
    }}>
        <div className='flex h-[150px] mt-auto'>
            <div className='mx-auto flex flex-col'>
                <div className='flex flex-col mt-4'>
                    <label className='font-bold text-[#8fbee9] cursor-pointer'>HoneyBadger</label>
                    <label className='text-xxs text-center text-gray-500 cursor-pointer'>Don't hold back. Be brave</label>
                </div>
                <div className='mt-auto flex flex-col h-[60px]'>
                    <div className='flex  items-center my-auto'>
                        <img src='/_phone.png' className='w-[20px] h-[20px] mr-2' />
                        <label className='text-xxs cursor-pointer'>{PhoneString(user?.phoneNumber)}</label>
                    </div>
                    <div className='flex items-center my-auto'>
                        <img src='/_call.png' className='w-[20px] h-[20px] mr-2' />
                        <label className='text-xxs cursor-pointer'>1312</label>
                    </div>
                    <div className='flex items-center my-auto'>
                        <img src='/_mail.png' className='w-[20px] h-[20px] mr-2' />
                        <label className='text-xxs cursor-pointer'>{user?.username}@honeybadger.com</label>
                    </div>
                </div>
            </div>
            <div className='mx-auto flex flex-col'>
                <div className='flex flex-col mt-4'>
                    <label className='font-bold text-[#8fbee9] cursor-pointer'>{user?.name}</label>
                    <label className='text-xs text-gray-500 cursor-pointer'>{getRole(user?.role)}</label>
                </div>

                <div className='mt-auto flex flex-col h-[60px]'>
                    <div className='flex items-center my-auto'>
                        <img src='/_web.png' className='w-[20px] h-[20px] mr-2' />
                        <label className='text-xxs cursor-pointer'>www.벌꿀오소리.메인.한국</label>
                    </div>
                    <div className='flex items-center my-auto'>
                        <img src='/_location.png' className='w-[20px] h-[20px] mr-2' />
                        <label className='text-xxs cursor-pointer'>대전광역시 서구 둔산로 52 3층</label>
                    </div>
                </div>
            </div>
        </div>
        <div className='w-full h-[20px] bg-[#8fbee9] mt-auto'></div>
    </div>
}
export function CardBack() {
    return <div className='border bg-[#8fbee9] border-black w-[400px] h-[200px] flex flex-col items-center justify-center relative cursor-pointer' onClick={(e) => {
        html2canvas(e.currentTarget as HTMLInputElement).then(canvas => {
            var el = document.createElement('a');
            el.href = canvas.toDataURL('image/png');
            el.download = '명함 뒷면.png';
            el.click();
            // document.body.append(canvas)
        });
    }}>
        <div className='flex'>
            <img src='/_logo.png' className='w-[75px] h-[50px] mr-2 bg-yellow-500 rounded-full p-2' />
            <div className='flex flex-col'>
                <label className='font-bold text-white text-lg cursor-pointer'>HoneyBadger</label>
                <label className='text-xxs text-center text-gray-300 cursor-pointer'>Don't hold back. Be brave</label>
            </div>
        </div>
        <div className='bottom-[16px] absolute text-white text-xs'>SEO | Web Devlopement | App Development </div>
    </div>
}
export function translateDex(num: number) {
    num = num % 256;
    if (num < 16)
        return '0' + num.toString(16);
    else
        return num.toString(16);
}

export function PhoneNumberCheck(value: string) {
    // const input = e.target as HTMLInputElement;
    value = value.replace(/[^0-9]/g, '');
    if (value.length > 3 && value.charAt(3) != '-')
        value = value.slice(0, 3) + '-' + value.slice(3);

    if (value.length > 8 && value.charAt(8) != '-')
        value = value.slice(0, 8) + '-' + value.slice(8);

    if (value.length > 13)
        value = value.slice(0, 13);
    if (value.lastIndexOf('-') == value.length - 1)
        value = value.slice(0, value.length - 1);
    return value;
}

export function getSoloarToLunarDate(date: Date) {
    const calendar = new KoreanLunarCalendar();
    calendar.setSolarDate(date.getFullYear(), date.getMonth() + 1, date.getDate());
    const lunar = calendar.getLunarCalendar();
    return { date: new Date(lunar.year, lunar.month - 1, lunar.day), intercalation: lunar.intercalation };
}

export function hasIntercalation(year: number, month: number) {
    const calendar = new KoreanLunarCalendar();
    calendar.setLunarDate(year, month, 1, false);
    const lunar = calendar.getSolarCalendar();
    const date = new Date(lunar.year, lunar.month, lunar.day - 1);
    calendar.setSolarDate(date.getFullYear(), date.getMonth() + 1, date.getDate());
    return calendar.getLunarCalendar().intercalation;
}

export function isHoliday(date: Date) {
    const solar = (date.getMonth() < 9 ? '0' : '') + (date.getMonth() + 1) + date.getDate();
    const lunarDate = getSoloarToLunarDate(date);
    const lunar = (lunarDate.date.getMonth() < 9 ? '0' : '') + (lunarDate.date.getMonth() + 1) + lunarDate.date.getDate() + (lunarDate.intercalation ? 'U' : '');
    const solarHoliday = [
        '0101', // 새해
        '0301', // 삼일절
        '0505', // 어린이날
        '0606', // 현충일
        '0815', // 광복절
        '1003', // 개천절
        '1009', // 한글날
        '1225', // 크리스마스
    ];

    const lunarHoliday = [
        '1231' + (hasIntercalation(lunarDate.date.getFullYear(), 12) ? 'U' : ''), '0101', '0102', // 설날
        '0408', // 부처님 오신 날
        '0814', '0815', '0816' // 추석
    ];
    return solarHoliday.includes(solar) || lunarHoliday.includes(lunar);
}

