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

interface SendEmail {
    title: string,
    content: string,
    receiverIds: string[],
    sendTime?: Date | null
}

interface CreateEmail {
    title: string,
    content: string,
    receiverIds: string[]
}

interface emailReservationUpdate {
    id: number,
    title: string,
    content: string,
    receiverIds: string[],
    sendTime?: Date | null,
    files: string[]
}

interface MailFile {
    key: string,
    original_name: string,
    value: string
}

interface chatroomResponseDTO {
    name?: string,
    users: string[]
}

interface noticeRequestDTO {
    chatroomId: number,
    messageId: number
}

interface chatroomRequestDTO {
    name: string,
    users: string[]
}

interface approvalRequestDTO {
    title: string,
    content: string,
    sender: string,
    approversname: string[],
    viewersname: string[]
}

export const getEmail = async (status: number, page: number) => {
    const response = await UserApi.get('/api/email/list', {
        headers:
        {
            status: status,
            Page: page
        }
    });
    return response.data;
}

export const sendEmail = async (data: CreateEmail) => {
    console.log(data);
    const response = await UserApi.post('/api/email', data);
    return response.data;
}

export const getChat = async (keyword: string, Page: number) => {
    const response = await UserApi.get('/api/chatroom/list',{
        headers: {
            keyword: encodeURIComponent(keyword),
            Page: Page
        }
    });
    return response.data;
}


export const getChatDetail = async (chatroomId: number, page: number) => {
    const response = await UserApi.get('/api/chatroom', 
        { 
            headers: { chatroomId: chatroomId, Page: page} 
        }
    );

    return response.data;
}

export const getUpdateMessageList = async (chatroomId: number) => {
    const response = await UserApi.get('/api/message/update', { headers: { chatroomId: chatroomId } });
    return response.data;
}

export const reservationEmail = async (data: SendEmail) => {
    const response = await UserApi.post('/api/emailReservation/schedule', data);
    return response.data;
}

export const readEmail = async ({ emailId, readerId }: { emailId: number, readerId: string }) => {

    const data = {
        emailId: emailId,
        receiverId: readerId
    };
    const response = await UserApi.put('/api/email/read', data);
    return response.data;
}


export const mailCancel = async (mailId: number) => {
    const response = await UserApi.delete('/api/emailReservation', {
        headers: {
            reservationId: mailId
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
    const response = await UserApi.delete('/api/email', {
        headers: {
            emailId: mailId
        }
    });
    return response.data;
}

export const mailUpdate = async (email: emailReservationUpdate) => {
    const response = await UserApi.put('/api/emailReservation', email);
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
    const response = await UserApi.post('/api/participant', null, { headers: data });
    return response.data;
}

export const editChatroom = async ({ chatroomId, chatroomResponseDTO }: { chatroomId: number, chatroomResponseDTO: chatroomResponseDTO }) => {
    const response = await UserApi.put('/api/chatroom', chatroomResponseDTO, {
        headers:
        {
            chatroomId: chatroomId
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

export const getDepartmentTopList = async () => {
    const response = await UserApi.get('/api/department');
    return response.data;
}
export const postDepartmentImage = async (formData: any) => {
    const response = await UserApi.post('/api/department/img', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
    return response.data;
}
interface postDepartmentProps {
    name: string,
    url: string,
    parentId: string,
    role: number
}
export const postDepartment = async (data: postDepartmentProps) => {
    const response = await UserApi.post('/api/department', data);
    return response.data;
}

export const deleteDepartment = async (DepartmentId: string) => {
    const response = await UserApi.delete('/api/department', { headers: { DepartmentId: encodeURIComponent(DepartmentId) } });
    return response.data;
}
export const getDepartmentUsers = async (DepartmentId?: string) => {
    const response = await UserApi.get('/api/department/users', { headers: { DepartmentId: DepartmentId ? encodeURIComponent(DepartmentId) : DepartmentId } });
    return response.data;
}
interface userInfo {
    username: string,
    name: string,
    role: number,
    password: string,
    phoneNumber: string,
    joinDate: any,
    department_id: string
}
export const updateUser = async (data: userInfo) => {
    const response = await UserApi.put('/api/user/info', data);
    return response.data;
}
export const updateActiveUser = async (data: string) => {
    const response = await UserApi.put('/api/user/status', {}, { headers: { Username: data } });
    return response.data;
}
export const postUser = async (data: userInfo) => {
    const response = await UserApi.post('/api/user', data);
    return response.data;
}


export const makeChatroom = async (chatroomRequestDTO: chatroomRequestDTO) => {
    const response = await UserApi.post('/api/chatroom', chatroomRequestDTO);
    return response.data;
}

export const deleteMessage = async (messageId: number) => {
    const response = await UserApi.delete('/api/message', {
        headers: {
            messageId: messageId
        }
    });
    return response.data;
};

export const chatUploadFile = async ({ chatroomId, file }: { chatroomId: number, file: File }) => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await UserApi.post('/api/message/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
            chatroomId: chatroomId,
        },

    });

    return response.data;
};

export const tempDelete = async () => {
    const response = await UserApi.delete('/api/user/temp');
    return response.data;
}


// Question
interface createQuestionProp {
    title: string,
    content: string,
    author: string,
    password: string,
    lock:boolean
}
export const createQuestion = async (data: createQuestionProp) => {
    const response = await UserApi.post('/api/question', data);
    return response.data;
};

export const getQuestions = async (page: number, keyword: string) => {
    const response = await UserApi.get('/api/question', {
        headers: {
            page: page,
            keyword: keyword ? encodeURIComponent(keyword) : ''
        }
    });
    return response.data;
};
export const checkQuestion = async (data: {password:string, id:number}) => {
    const response = await UserApi.post('/api/question/check', data);
    return response.data;
};
export const updateQuestionAnswer = async (data: {answer:string, id:number}) => {
    const response = await UserApi.put('/api/question', data);
    return response.data;
};

export const readUsersName = async (messageId:number, username:string)=>{
    const response = await UserApi.get('/api/message/readUsernames',{
        headers : {
            messageId : messageId,
            username : username
        }
    });
    return response.data;
}
interface getFileProps{
    Location:string,
    Page?:number;
}
export const getStorageFiles = async (data:getFileProps)=>{
    const response = await UserApi.get('/api/file/list',{
        headers : {...data}
    });
    return response.data;
}
export const getFileFolders = async (data:getFileProps)=>{
    const response = await UserApi.get('/api/file/folders',{
        headers : {...data}
    });
    return response.data;
}
export const getStorageFile = async (data:getFileProps)=>{
    const response = await UserApi.get('/api/file',{
        headers : {...data}
    });
    return response.data;
}

export const createApproval = async (approvalRequestDTO: approvalRequestDTO) => {
    const response = await UserApi.post('/api/approval', approvalRequestDTO);
    return response.data;
};