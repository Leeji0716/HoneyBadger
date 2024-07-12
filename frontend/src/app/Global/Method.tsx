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
    return phone?.slice(0, 3) + "-" + phone?.slice(3, 7) + "-" + phone?.slice(7);
}
export function checkInput(check: any, pattern: string, True: () => void, False: () => void) {
    if (new RegExp(pattern).test(check.target.value))
        True();
    else
        False();
}
export function PhoneNumberCheck(e: any) {
    const input = e.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
    if (input.value.length > 3 && input.value.charAt(3) != '-') {
        const value = input.value;
        input.value = value.slice(0, 3) + '-' + value.slice(3);
    }
    if (input.value.length > 8 && input.value.charAt(8) != '-') {
        const value = input.value;
        input.value = value.slice(0, 8) + '-' + value.slice(8);
    }
    if (input.value.length > 13)
        input.value = input.value.slice(0, 13);
    if (input.value.lastIndexOf('-') == input.value.length - 1)
        input.value = input.value.slice(0, input.value.length - 1);

}
export function Check(pattern: string, test: string) {
    return new RegExp(pattern).test(test);
}
export function getDate(data: any) {
    const date = new Date(data);
    return date.getFullYear() + '.' + (date.getMonth() + 1) + '.' + date.getDate();
}
export function getDateTime(data: any) {
    const date = new Date(data);
    return date.getFullYear() + "" + (date.getMonth() + 1) + "" + date.getDate() + "" + date.getHours() + "" + date.getMinutes();
}
export function getDateTimeFormat(data: any) {
    const date = new Date(data);
    return date.getFullYear() + "-" + (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + "-" + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + " " + (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());
}
export function getDateTimeFormatInput(data: any) {
    const date = new Date(data);
    return date.getFullYear() + "-" + (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + "-" + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + "T" + (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());
}
export function transferLocalTime(date: Date) {
    return new Date(date.getTime() + 9 * 1000 * 60 * 60);

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
    
        const formattedTime = amPm + " " + (formattedHour < 10 ? '0' + formattedHour : formattedHour) + ":" 
        + (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());
    
        if (isToday) {
            // 오늘 날짜인 경우 시간만 반환
            return formattedTime;
        } else {
            // 오늘이 아닌 경우 날짜와 시간 모두 반환
            return date.getFullYear() + "-" + 
            (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + "-" 
            + (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + " " 
            + formattedTime;
        }
}