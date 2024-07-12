import { headers } from 'next/headers';
import { getAPI } from './AxiosAPI';
import { access } from 'fs';
import exp from 'constants';


export const UserApi = getAPI();

UserApi.interceptors.request.use(
    (config) => {
        const TOKEN_TYPE = localStorage.getItem('tokenType');
        const ACCESS_TOKEN = localStorage.getItem('accessToken');
        const REFRESH_TOKEN = localStorage.getItem('refreshToken');
        config.headers['Authorization'] = `${TOKEN_TYPE} ${ACCESS_TOKEN}`;
        config.headers['REFRESH_TOKEN'] = REFRESH_TOKEN;
        return config;
    },
    (error) => {
        console.log(error);
        return Promise.reject(error);
    }
);
// 토큰 유효성 검사
UserApi.interceptors.response.use((response) => {
    return response;
}, async (error) => {
    const originalRequest = error.config;
    if (!originalRequest._retry)
        if (error.response.status === 401 && error.response.data == 'refresh') {
            await refreshAccessToken();
            return UserApi(originalRequest);

        } else if (error.response.status === 403 && error.response.data == 'logout') {
            localStorage.clear();
            window.location.href = '/';
            return;
        }
    return Promise.reject(error);
});

// 토큰 갱신
const refreshAccessToken = async () => {
    const response = await UserApi.get('/api/auth/refresh');
    const TOKEN_TYPE = localStorage.getItem('tokenType');
    const ACCESS_TOKEN = response.data;
    localStorage.setItem('accessToken', ACCESS_TOKEN);
    UserApi.defaults.headers.common['Authorization'] = `${TOKEN_TYPE} ${ACCESS_TOKEN}`;
}

export const getUser = async () => {
    const response = await UserApi.get('/api/user');
    return response.data;
}
interface UpdateProps {
    name: string,
    email: string,
    phoneNumber: string,
    nickname: string,
    password: string,
    newPassword: string,
    url: string
}

interface SendEmail {
    title: string,
    content: string,
    receiverIds: string[],
    sendTime?: Date | null
}

interface SendEmail2 {
    title: string,
    content: string,
    receiverIds: string[]
}

interface chatroomResponseDTO {
    name?: string,
    users: string[]
}

interface noticeRequestDTO{
    chatroomId: number,
    messageId: number
}

export const updateUser = async (data: UpdateProps) => {
    const response = await UserApi.put('/api/user', data);
    return response.data;
}
export const getEmail = async (status: number) => {
    const response = await UserApi.get('/api/email/list', {
        headers:
        {
            status: status
        }
    });
    return response.data;
}

export const sendEmail = async (data: SendEmail2) => {
    console.log(data);
    const response = await UserApi.post('/api/email', data);
    return response.data;
}

export const getChat = async () => {
    const response = await UserApi.get('/api/chatroom/list');
    return response.data;
}


export const getChatDetail = async (chatroomId: number) => {
    const response = await UserApi.get('/api/chatroom', { headers: { chatroomId: chatroomId } });
    return response.data;
}

export const reservationEmail = async (data: SendEmail) => {
    const response = await UserApi.post('/api/emailReservation/schedule', data);
    return response.data;
}

export const readEmail = async ({ emailId, readerId }: { emailId: number, readerId: string }) => {

    const data = {
        emailId: emailId,
        readerId: readerId
    };
    const response = await UserApi.post('/api/email/read', data);
    return response.data;
}


export const mailCancel = async (mailId: number) => {
    const response = await UserApi.delete('/api/email/cancel', {
        headers: {
            id: mailId
        }
    });
    return response.data;
}


export const mailImage = async (formData: any) => {
    const response = await UserApi.post('/api/email/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
    return response.data;
}

export const mailDelete = async (mailId: number) => {
    const response = await UserApi.delete('/api/email/delete', {
        headers: {
            id: mailId
        }
    });
    return response.data;
}

export const mailUpdate = async ({ mailId, email }: { mailId: number, email: SendEmail }) => {
    const response = await UserApi.put('/api/email/delete', email, {
        headers: {
            id: mailId
        }
    });
    return response.data;
}


export const chatExit = async (data: { chatroomId: number, username: string }) => {
    const response = await UserApi.delete('/api/participant', { headers: data });
    return response.data;
}

export const addUser = async ({ chatroomId, username }: { chatroomId: number, username: string }) => {
    const data = {
        chatroomId: chatroomId,
        username: username
    };
    const response = await UserApi.post('/api/participant', data);
    return response.data;
}

export const editChatroom = async ({ chatroomId, chatroomResponseDTO }: { chatroomId: number, chatroomResponseDTO: chatroomResponseDTO }) => {
    const response = await UserApi.put('/api/chatroom', chatroomResponseDTO, {
        headers:
        {
            chatroomId:chatroomId
        }
});
    return response.data;
}



export const notification = async (data: noticeRequestDTO) => {
    const response = await UserApi.put('/api/chatroom/notification', data);    
    return response.data;
}

export const getUsers = async () => {
    const response = await UserApi.get('/api/user/usernames');
    return response.data;
}


export const emailFiles = async ({ attachments, emailId }: { attachments: FormData, emailId: number }) => {
    const response = await UserApi.post('/api/email/files', attachments, {
        headers: {
            'Content-Type': 'multipart/form-data',
            email_id: emailId
        }
    });
    return response.data;
}


export const reservationFiles = async ({ attachments, emailId }: { attachments: FormData, emailId: number }) => {

    const response = await UserApi.post('/api/emailReservation/files', attachments, {
        headers: {
            'Content-Type': 'multipart/form-data',
            email_id: emailId
        }
    });
    return response.data;
}

export const putProfileImage = async (form: FormData) => {
    const response = await UserApi.put('/api/user/profile_image', form, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
    return response.data;
}

export const deleteProfileImage = async () => {
    const response = await UserApi.delete('/api/user/profile_image');
    return response.data;
}
export const updatePassword = async (prePassword: string, newPassword: string) => {
    const response = await UserApi.put('/api/user', { prePassword: prePassword, newPassword: newPassword });
    return response.data;
}
