"use client";
import React, { useEffect, useRef, useState, RefObject, use } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import Modal from "../Global/Modal";
import { Tooltip } from 'react-tooltip';

import {
    chatExit, getChat, getUser, getChatDetail, notification, editChatroom, getUsers, addUser, makeChatroom, deleteMessage,
    chatUploadFile, getUpdateMessageList, createMessageReservation, getMessageReservationList, deleteMessageReservation,
    unsubscribeChatroom, messageFileList, messageImageList, messageLinkList, searchUsers
} from "../API/UserAPI";
import { getChatDateTimeFormat } from "../Global/Method";
import { getChatShowDateTimeFormat, getFileIcon } from "../Global/Method";
import { getSocket } from "../API/SocketAPI";

export default function Chat() {
    interface messageResponseDTO {
        id?: number,
        username: string,
        message: string,
        messageType: number,
        sendTime: number,
        name: string,
        readUsers?: number
    }

    interface chatroomResponseDTO {
        id: number,
        name: string,
        users: any[],
        latestMessage: messageResponseDTO,
        notification: messageResponseDTO,
        alarmCount: number
        createDate: number
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

    interface chatroomRequestDTO {
        name: string;
        users: string[];
    }

    interface messageReservationResponseDTO {
        id: number
        chatroomId: number
        message: string
        username: string
        name: string
        reservationDate: Date | null
        messageType: number

    }

    interface messageReservationRequestDTO {
        chatroomId: number
        message: string
        messageType: number
        reservationDate: Date | null
    }

    const [open, setOpen] = useState(false);
    const [open1, setOpen1] = useState(false);
    const [filter, setFilter] = useState(false);
    const [drop, setDrop] = useState(false);
    // const [chat, setChat] = useState("채팅▾");
    const [chatrooms, setChatrooms] = useState([] as any[]);
    const [chatroom, setChatroom] = useState(null as any);
    const [user, setUser] = useState(null as any);
    const [chatDetail, setChatDetail] = useState(null as any);
    const [messageList, setMessageList] = useState<messageResponseDTO[]>([]);
    const [reservationMessageList, setReservationMessageList] = useState<messageReservationResponseDTO[]>([]);
    const [reservationMessages, setReservationMessages] = useState([] as any[]);
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [socket, setSocket] = useState(null as any);
    const [temp, setTemp] = useState(null as any);
    const [isReady, setIsReady] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isModalOpen1, setIsModalOpen1] = useState(false);
    const [isModalOpen2, setIsModalOpen2] = useState(false);
    const [isModalOpen3, setIsModalOpen3] = useState(false);
    const [isModalOpen4, setIsModalOpen4] = useState(false);
    const [isModalOpen5, setIsModalOpen5] = useState(false);
    const [isModalOpen6, setIsModalOpen6] = useState(false);
    const [userList, setUserList] = useState<userResponseDTO[]>([]);
    const [selectedUsers, setSelectedUsers] = useState(new Set<string>());
    const [chatroomName, setChatroomName] = useState('');
    const file = useRef(null as any);
    const image = useRef(null as any);
    const [searchedUsers, setSearchedUsers] = useState([] as any[]);

    const [isClientLoading, setClientLoading] = useState(true);
    const [keyword, setKeyword] = useState('');
    const [userKeyword, setUserKeyword] = useState('');
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [maxPage, setMaxPage] = useState(0);
    const [tempChatroom, setTempChatroom] = useState(null as any);
    const chatBoxRef = useRef<HTMLDivElement>(null);
    const [currentScrollLocation, setCurrentScrollLocation] = useState(0);
    const [updateMessageList, setUpdateMessageList] = useState<messageResponseDTO[]>([]);
    const [imageList, setImageList] = useState<messageResponseDTO[]>([]);
    const [linkList, setLinkList] = useState<messageResponseDTO[]>([]);
    const [fileList, setFileList] = useState<messageResponseDTO[]>([]);
    const [preChatroomId, setPreChatroomId] = useState(0);
    const [messageSub, setMessageSub] = useState<any>(null);
    const [readSub, setReadSub] = useState<any>(null);
    const [updateSub, setUpdateSub] = useState<any>(null);
    const [editingReservations, setEditingReservations] = useState({});
    const [editingContents, setEditingContents] = useState({});
    const addUsersInputRef = useRef<HTMLInputElement | null>(null);
    const [showNotification, setShowNotification] = useState(false);
    const toggleNotification = () => {
        setShowNotification(!showNotification);
    };



    function handleOpenModal() {
        addUsersInputRef.current?.focus();
        setIsModalOpen(true);
    }

    function handleCloseModal() {
        setIsModalOpen(false);
    }

    function handleOpen1Modal() {
        setIsModalOpen1(true);
    }

    function handleClose1Modal() {
        setIsModalOpen1(false);
    }

    function handleOpen2Modal() {
        setIsModalOpen2(true);
    }

    function handleClose2Modal() {
        setIsModalOpen2(false);
    }

    function handleOpen3Modal() {
        setIsModalOpen3(true);
    }

    function handleClose3Modal() {
        setIsModalOpen3(false);
    }

    function handleOpen4Modal() {
        setIsModalOpen4(true);
    }

    function handleClose4Modal() {
        setIsModalOpen4(false);
    }

    function handleOpen5Modal() {
        setIsModalOpen5(true);
    }

    function handleClose5Modal() {
        setIsModalOpen5(false);
    }

    function handleOpen6Modal() {
        setIsModalOpen6(true);
    }

    function handleClose6Modal() {
        setIsModalOpen6(false);
    }

    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                setUser(r);
                getUsers().then(r => {
                    setUserList(r);

                }).catch(e => console.log(e))
                getChat(keyword, page).then(r => {
                    setChatrooms(r.content);
                    const interval = setInterval(() => { setClientLoading(false); clearInterval(interval); }, 1000);
                }).catch(e => { console.log(e); setClientLoading(false); })
            }).catch(e => console.log(e));
        else
            window.location.href = "/";
    }, [ACCESS_TOKEN])

    useEffect(() => {
        setSocket(getSocket([], () => setIsReady(true)));
    }, [])

    useEffect(() => {

    }, [chatBoxRef])

    useEffect(() => {
        if (tempChatroom) {
            const index = chatrooms.findIndex((e: any) => e.id == tempChatroom?.id);
            chatrooms[index] = tempChatroom;
            setChatrooms([...chatrooms]);
        }
    }, [tempChatroom])

    useEffect(() => {
        if (updateMessageList) {
            const beforeMessageList = [...messageList];


            updateMessageList.forEach((updateItem) => { //업데이트 메세지 돌리기
                const index = beforeMessageList.findIndex(msgItem => msgItem.id === updateItem.id); //업데이트 범위 시작 인덱스 찾기


                // if (index !== -1) {
                //     beforeMessageList[index] = updateItem;
                //     console.log(`메시지 업데이트됨: ${updateItem.id}`);
                //     console.log(beforeMessageList[index]);
                // }

                if (index !== -1) {
                    beforeMessageList[index] = updateItem;
                } else {
                    beforeMessageList.push(updateItem);
                }
            });
            setMessageList(beforeMessageList);
        }
    }, [updateMessageList]);


    useEffect(() => {
        if (temp) {
            const test: messageResponseDTO[] = [...messageList];
            test.push(temp);

            setMessageList(test);

            setTemp(null);
        }

        // setChatDetail(temp);
    }, [temp])

    useEffect(() => {
        const fetchData = async () => {
            try {
                const usersData = await getUsers();
                setUserList(usersData);
            } catch (error) {
                console.error(error);
            }
        };
        fetchData();
    }, []);

    const unsubscribe = () => {
        unsubscribeChatroom(user.username);
    }

    useEffect(() => {
        window.addEventListener("beforeunload", unsubscribe);
        window.addEventListener("popstate", unsubscribe);
        window.addEventListener('unload', unsubscribe)
        return (() => {
            window.removeEventListener("beforeunload", unsubscribe);
            window.removeEventListener("popstate", unsubscribe);
            window.removeEventListener('unload', unsubscribe);
        });
    }, [chatroom]);

    useEffect(() => {
        if (isModalOpen5) {
            getMessageReservationList(page)
                .then(r => {
                    if (Array.isArray(r.content)) {
                        setReservationMessageList(r.content);
                    } else {
                        console.error('API 응답이 배열이 아닙니다:', r.content);
                        setReservationMessageList([]); // 비어있는 배열로 초기화
                    }
                    setMaxPage(r.totalPages);
                })
                .catch(e => console.error(e));
        }
    }, [isModalOpen5, page]);

    useEffect(() => {
        if (isModalOpen6) {
            messageImageList(chatroom.id).then(setImageList);
            messageFileList(chatroom.id).then(setFileList);
            messageLinkList(chatroom.id).then(setLinkList);
        }
    }, [isModalOpen6, chatroom?.id]);



    const loadPage = () => {
        const chatBox = chatBoxRef.current;

        if (chatBox != null) {
            const scrollLocation = chatBox?.scrollTop;
            const maxScroll = chatBox.scrollHeight - chatBox.clientHeight;


            if (!isLoading && scrollLocation <= 0 && page < maxPage - 1) {
                getChatDetail(chatroom.id, page + 1).then(r => {

                    const result = [...r.content, ...messageList];
                    setMessageList(result);
                    setPage(page + 1);
                    setIsLoading(false);
                })
                //setIsLoading(true);  // 로딩 시작
            }
            //     getChatDetail(chatroom.id, page + 1)

            //         .then(response => {
            //             // 데이터가 있는 경우 새 메시지 리스트에 추가
            //             if (response.content.length > 0) {
            //                 console.log(response.content);
            //                 console.log(messageList);
            //                 const newMessageList = [...messageList, ...response.content];
            //                 const reverseNewMessageList = [...newMessageList].reverse();

            //                 setMessageList(reverseNewMessageList);

            //                 setMaxPage(response.totalPages);
            //                 setPage(page + 1);
            //                 console.log("------------maxScroll - beforeMax");
            //                 console.log(maxScroll);
            //                 setCurrentScrollLocation(maxScroll);

            //                     // scrollHeight : 300
            //                     // maxScroll : 300
            //                     // scrollLocation: 0
            //                     // scrollheight : 1000
            //                     // 
            //             }
            //             setIsLoading(false);  // 로딩 완료
            //         })
            //         .catch(error => {
            //             console.error(error);
            //             setIsLoading(false);  // 에러 시 로딩 중지
            //         });
            // }
        }
    };

    const handleCheckboxChange = (username: string) => {
        setSelectedUsers(prevSelectedUsers => {
            const newSelectedUsers = new Set(prevSelectedUsers);
            if (newSelectedUsers.has(username)) {
                newSelectedUsers.delete(username);
            } else {
                newSelectedUsers.add(username);
            }
            return newSelectedUsers;
        });
    };

    const handleCreateChatroom = () => {
        // 선택된 사용자가 있는지 확인
        if (selectedUsers.size > 0) {
            // 선택된 사용자의 Set 객체를 배열로 변환
            const users = Array.from(selectedUsers);

            // 현재 사용자가 배열에 포함되어 있는지 확인하고, 포함되지 않았으면 추가
            if (user && !users.includes(user.username)) {
                users.push(user.username);
            }

            let roomName = chatroomName;
            if (!roomName) {
                // userList에서 사용자 이름을 찾아 결합
                roomName = userList.filter(u => users.includes(u.username))
                    .map(u => u.name)
                    .join(', '); // 이름들을 쉼표로 구분
            }

            // 채팅방 요청 객체 생성
            const chatroomRequest: chatroomRequestDTO = { name: roomName, users };

            // 채팅방 생성 API 호출
            makeChatroom(chatroomRequest)
                .then(r => {
                    // 채팅방 생성 성공 시 모달 닫기, 선택된 사용자 및 채팅방 이름 초기화
                    setIsModalOpen2(false);
                    setSelectedUsers(new Set());
                    setChatroomName('');
                    setMessageList([]);
                    getChat(keyword, page).then(r => { setChatrooms(r.content); setMaxPage(r.totalPages); }).catch(e => console.log(e));
                })
                .catch(e => {
                    // 채팅방 생성 실패 시 콘솔에 오류 출력
                    console.error(e);
                });
        } else {
            // 선택된 사용자가 없을 경우 콘솔에 오류 출력
            console.error("유저를 선택해주세요");
        }
    };
    const handleSearch = () => {
        setPage(0);
        getChat(keyword, page).then(r => {
            setChatrooms(r.content);
        }).catch(error => {
            console.error("Search error:", error);
        });
    };

    const handleUserSearch = () => {
        setPage(0);
        setSize(10);
        searchUsers(userKeyword, page, size).then(r => {
            setSearchedUsers(r.content);
        }).catch(error => {
            console.error("Search error:", error);
        });
    };

    function ChatList({ Chatroom, ChatDetail, innerRef }: { Chatroom: chatroomResponseDTO, ChatDetail: messageResponseDTO, innerRef: RefObject<HTMLDivElement> }) {
        const joinMembers = Chatroom.users;

        // 채팅방 프로필
        function getValue() {
            const targets = joinMembers.filter(f => f?.name != user?.username)


            switch (joinMembers.length) {
                case 2: return <img src={targets[0]?.url ? targets[0]?.url : "/pin.png"} className="m-2 w-[5rem] h-[5rem] rounded-full" />;

                case 3: return <div className="m-2 w-[5rem] h-[5rem] flex flex-col justify-center items-center ">
                    <div className="w-[5rem] h-[2.5rem] flex">
                        <img src={targets[0]?.url ? targets[0].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full ml-2 mt-2" />
                    </div>
                    <div className="w-[5rem] h-[2.5rem] flex justify-end">
                        <img src={targets[1]?.url ? targets[1].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full mr-2 mb-2" />
                    </div>
                </div>

                case 4: return <div className="m-2 w-[5rem] h-[5rem] flex flex-col justify-center items-center ">
                    <div className="w-[5rem] h-[2.5rem] flex justify-center">
                        <img src={targets[0]?.url ? targets[0].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full" />
                    </div>
                    <div className="w-[5rem] h-[2.5rem] flex">
                        <img src={targets[1]?.url ? targets[1].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full" />
                        <img src={targets[2]?.url ? targets[2].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full" />
                    </div>
                </div>
                default:
                    return <div className="m-2 w-[5rem] h-[5rem] flex flex-col justify-center items-center ">
                        <div className="w-[5rem] h-[2.5rem] flex">
                            <img src={targets[0]?.url ? targets[0].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full" />
                            <img src={targets[1]?.url ? targets[1].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full" />
                        </div>
                        <div className="w-[5rem] h-[2.5rem] flex">
                            <img src={targets[2]?.url ? targets[2].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full" />
                            <img src={targets[3]?.url ? targets[3].url : "/pigp.png"} className="w-[2.5rem] h-[2.5rem] rounded-full" />
                        </div>
                    </div>
            }
        }

        return <div className="flex hover:bg-gray-400 text-white rounded-md cursor-pointer" onClick={() => {
            if (isReady) {
                let nowPage = page;

                if (Chatroom) {
                    setPage(0);
                    nowPage = 0;
                    setShowNotification(false);
                    if (messageSub) {
                        socket.unsubscribe(messageSub.id)
                    }
                    if (readSub) {
                        socket.unsubscribe(readSub.id);
                    }

                    if (updateSub) {
                        socket.unsubscribe(updateSub.id);
                    }
                }
                setChatroom(Chatroom);


                getChatDetail(Chatroom?.id, nowPage).then(r => {
                    setMessageList([...r.content].reverse());
                    setMaxPage(r.totalPages);
                    // url 통해서 messageList 요청 -> 요청().then(r=> setMessageList(r)).catch(e=>console.log(e));
                    const messageSub = socket.subscribe("/api/sub/message/" + Chatroom?.id, (e: any) => {
                        const message = JSON.parse(e.body).body;
                        const temp = { id: message?.id, message: message?.message, sendTime: message?.sendTime, name: message?.name, username: message?.username, messageType: message?.messageType, readUsers: message?.readUsers } as messageResponseDTO; // 위에꺼 확인해보고 지우세요
                        setTemp(temp);
                        // getUpdateMessageList(Chatroom?.id).then((updateMessageList => {

                        //     setUpdateMessageList(updateMessageList);

                        // }));

                        socket.publish({
                            destination: "/api/pub/check/" + Chatroom?.id,
                            body: JSON.stringify({ username: user?.username })

                        });

                        socket.publish({
                            destination: "/api/pub/updateChatroom/" + Chatroom?.id,
                            body: JSON.stringify({ username: user?.username })
                        });

                        const readSub = socket.subscribe("/api/sub/check/" + Chatroom?.id, (e: any) => {
                            const data = JSON.parse(e.body);

                        }, JSON.stringify({ username: user?.username }));
                        //들어오자마자 읽고 바꾸기
                        getUpdateMessageList(Chatroom?.id).then((r => {
                            setUpdateMessageList(r);
                        }));



                    });



                    setMessageSub(messageSub);


                    setReadSub(readSub);

                }).catch(e => console.log(e));


                const updateSub1 = socket.subscribe("/api/sub/updateChatroom/" + Chatroom?.id, (e: any) => {
                    const data = JSON.parse(e.body);
                    setTempChatroom(data.body);
                }, JSON.stringify({ username: user?.username }));
                setUpdateSub(updateSub1);

            }
        }}>
            {getValue()}

            <div className="w-full m-2 flex flex-col">
                <div className="text-black font-bold">
                    {Chatroom?.name ? (
                        <span>{Chatroom.name}</span>
                    ) : (
                        Chatroom?.users
                            .filter((u: any) => u?.username !== user?.username) // 현재 사용자 제외
                            .map((u: any, index: number, array: any[]) => (
                                <span key={u?.username}>
                                    {u?.username}
                                    {index < array.length - 1 && ", "}
                                </span>
                            ))
                    )}
                </div>
                <div className="flex justify-between mt-2 text-black">
                    {
                        Chatroom?.latestMessage?.messageType === 0 ? (
                            <div>{Chatroom?.latestMessage?.message}</div>
                        ) : Chatroom?.latestMessage?.messageType === 1 ? (
                            <div>사진을 보냈습니다.</div>
                        ) : Chatroom?.latestMessage?.messageType === 2 ? (
                            <div>링크를 보냈습니다.</div>
                        ) : Chatroom?.latestMessage?.messageType === 3 ? (
                            <div>파일을 보냈습니다.</div>
                        ) : (
                            <div></div>
                        )
                    }
                </div >
            </div >
            <div className="w-3/12 h-full flex flex-col justify-end items-end mr-4">
                <div>
                    {Chatroom?.latestMessage?.sendTime == null ? (
                        <div className="text-gray-300 whitespace-nowrap">{getChatDateTimeFormat(Chatroom?.createDate)}</div>
                    ) : (
                        <div className="text-gray-300 whitespace-nowrap">{getChatShowDateTimeFormat(Chatroom?.latestMessage?.sendTime)}</div>
                    )}
                </div>
                {Chatroom?.alarmCount == 0 ? "" : <div className="bg-red-500 rounded-full w-[1.25rem] h-[1.25rem] flex justify-center items-center mt-2">
                    <div className="text-white text-sm">{Chatroom?.alarmCount}</div>
                </div>}

            </div>
        </div >
    }
    const joinMembers = Array.isArray(chatroom?.users) ? chatroom.users.length : 0;
    const [message, setMessage] = useState('');
    const [roomName, setRoomName] = useState(chatroom?.name);
    const [messageType, setMessageType] = useState(0);
    const [sendDate, setSendDate] = useState<Date | null>(null);
    // const [messageListTmp, setMessageListTmp] = useState<messageResponseDTO[]>([]);
    const [activeTab, setActiveTab] = useState('photos');
    function getChatroomNameById(chatroomId: number) {
        const chatroom = chatrooms.find((room) => room.id === chatroomId);
        return chatroom ? chatroom.name : 'Unknown Chatroom';
    }
    useEffect(() => {
        if (chatBoxRef.current) {
            if (currentScrollLocation == 0) {
                chatBoxRef.current.scrollTop = chatBoxRef.current.scrollHeight;
            }
            else {
                chatBoxRef.current.scrollTop = currentScrollLocation;
            }
        }
    }, [messageList])

    function ChatDetail({ Chatroom, messageList, innerRef, currentScrollLocation }: { Chatroom: chatroomResponseDTO, messageList: messageResponseDTO[], innerRef: RefObject<HTMLDivElement>, currentScrollLocation: number }) {


        // useEffect(() => {
        //     setMessageListTmp(messageList);
        // }, []);

        // useEffect(() => {
        //     if (innerRef.current) {
        //         if (currentScrollLocation == 0) {
        //             innerRef.current.scrollTop = innerRef.current.scrollHeight;
        //         }
        //         else {
        //             innerRef.current.scrollTop = currentScrollLocation;
        //         }
        //     }
        // }, [messageListTmp])


        return <></>;
    }

    return <Main user={user} isClientLoading={isClientLoading}>
        <div className="flex pt-10 pb-12 w-full h-full">

            <div className="w-4/12 flex flex-col items-center justify-center">
                {/* 왼쪽 부분 */}
                <div className=" w-11/12 bg-white h-full shadow relative">
                    <div className="flex justify-start text-xl ml-5 mr-5 mt-5 mb-5 text-black">
                        <button className="font-bold" id="button1" onClick={() => { setOpen(!open), setFilter(!filter) }}>채팅{open ? '▴' : '▾'}</button>
                        <DropDown open={open} onClose={() => setOpen(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button="button1">
                            <button>개인</button>
                            <button>단체</button>
                        </DropDown>
                    </div>
                    <button onClick={handleOpen2Modal} className="absolute bottom-5 left-5 w-[3.125rem] h-[3.125rem] rounded-full bg-blue-300 text-xl font-bold text-white">
                        +
                    </button>
                    <Modal open={isModalOpen2} onClose={handleClose2Modal} escClose={true} outlineClose={true}>
                        <div >
                            <div className="font-bold text-3xl m-3 mb-8 flex justify-center">채팅방 만들기</div>
                            <div className="flex flex-row border-2 border-gray-300 rounded-md w-[25rem] h-[2.5rem] m-2">
                                <input
                                    type="text"
                                    placeholder="채팅방 이름을 입력해주세요"
                                    className="bolder-0 outline-none bg-white text-black"
                                    value={chatroomName}
                                    onChange={e => setChatroomName(e.target.value)}
                                />

                            </div>
                            <div className="font-bold ml-2">추가 인원 선택</div>
                            <div className="overflow-auto w-full h-[31.25rem]">
                                <ul className="m-3">

                                    {userList.map((user, index) => (
                                        <li key={index} className="flex justify-between items-center mb-5">
                                            <span className="w-[3.125rem] h-[3.125rem]"><img src="/pin.png" alt="" /></span>
                                            <span className="font-bold text-md m-3">{user.name}</span>
                                            <span className=" text-md m-3">부서</span>
                                            <span className="text-md m-3">역할</span>
                                            {/* 체크박스 */}
                                            <input
                                                type="checkbox"
                                                checked={selectedUsers.has(user.username)}
                                                onChange={() => handleCheckboxChange(user.username)}
                                            />
                                        </li>
                                    ))}
                                </ul>
                            </div>
                            <div className="w-full flex justify-center">
                                <button onClick={handleCreateChatroom} className="login-button flex items-center m-2">
                                    채팅방 생성
                                </button>
                            </div>
                        </div>
                    </Modal>


                    <div className="flex flex-col items-center">
                        <div className="flex justify-items-center flex-row border-2 border-gray rounded-full w-[90%] h-[3.125rem] mb-5">
                            <img src="/searchg.png" className="w-[1.875rem] h-[1.875rem] m-2" alt="검색 사진" />
                            <input
                                type="text"
                                placeholder="대화방, 참여자 검색"
                                className="bolder-0 outline-none bg-white text-black w-[80%]"
                                value={keyword}
                                onChange={e => setKeyword(e.target.value)}
                            />
                            <button className="text-gray-300 whitespace-nowrap"
                                onClick={handleSearch} >
                                검색
                            </button>
                        </div>
                        <div className="justify-start w-full">
                            <div className="font-bold ml-3 text-gray-300">
                                내 프로필
                            </div>
                            <div className="flex hover:bg-gray-400 text-white rounded-md">
                                <img src={user?.url ? user.url : "/pin.png"} className="m-2 w-[5rem] h-[5rem] rounded-full" />
                                <div className="w-full m-2 flex flex-col">
                                    <div className="flex justify-between">
                                        <div className="text-black font-bold">{user?.name}</div>

                                    </div>
                                    <div className="flex flex-col mt-2">
                                        <div className="text-black">{user?.username}</div>
                                        <div className="text-black text-sm">:)</div>
                                    </div>
                                </div>
                                <div className="w-3/12 h-full flex flex-col justify-end items-end mr-4">
                                </div>
                            </div>
                            <div className="font-bold ml-3 text-gray-300 mt-3">
                                대화 목록
                            </div>
                        </div>
                        <div className="w-full justify-end h-[28.125rem] HD:h-[20rem] SD:h-[16rem] overflow-x-hidden overflow-y-scroll">
                            {chatrooms?.map((chatroom: chatroomResponseDTO, index: number) => <ChatList key={index} Chatroom={chatroom} ChatDetail={chatDetail} innerRef={chatBoxRef} />)}
                        </div>
                    </div>
                </div>
            </div>


            {/* 오른쪽 부분 */}
            <div className="w-8/12 flex flex-col items-center justify-center">
                <div className="w-11/12 bg-white h-full shadow">
                    {/* {chatroom != null ? <ChatDetail key={chatroom.id} Chatroom={chatroom} messageList={messageList} innerRef={chatBoxRef} currentScrollLocation={currentScrollLocation} /> : <></>} */}
                    <>

                        <div className={'h-full flex flex-col relative' + (chatroom != null ? '' : ' hidden')}>
                            <div className="flex w-full justify-between border-b-2">
                                <div className="text-black flex w-[50%]">
                                    <img src="/pig.png" className="m-2 w-[4.375rem] h-[4.375rem] rounded-full" />
                                    <div className="flex flex-col justify-center">
                                        <div className="flex">
                                            <div className="text-black font-bold text-3xl mb-1 whitespace-nowrap">
                                                {chatroom?.name ? (
                                                    <span>{chatroom.name}</span>
                                                ) : (
                                                    chatroom?.users
                                                        .filter((u: any) => u?.username !== user?.username) // 현재 사용자 제외
                                                        .map((u: any, index: number, array: any[]) => (
                                                            <span key={u?.username}>
                                                                {u?.username}
                                                                {index < array.length - 1 && ", "}
                                                            </span>
                                                        ))
                                                )}
                                            </div>
                                            <button onClick={handleOpen1Modal}> 이름편집</button>
                                        </div>
                                        <div className="flex items-center gap-1">
                                            <button onClick={handleOpenModal}>
                                                <img src="/people.png" className="w-[1.875rem] h-[1.875rem]" />
                                            </button>
                                            <div className="flex items-end text-xl w-[1.875rem] h-[1.875rem] text-official-color">
                                                {joinMembers}
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <Modal open={isModalOpen1} onClose={handleClose1Modal} escClose={true} outlineClose={true}>
                                    <div className="flex flex-col items-cnete justify-center m-3">
                                        <div className="flex items-center justify-center font-bold">이름 편집</div>
                                        <input type="text" placeholder={chatroom?.name} value={roomName} onChange={e => { setRoomName(e.target.value) }}
                                        />
                                        <button onClick={() => {
                                            const updatedChatroom = {
                                                ...chatroom,
                                                name: roomName
                                            };

                                            editChatroom({ chatroomId: chatroom?.id, chatroomResponseDTO: updatedChatroom }).then(r => {
                                                setChatrooms(prev => prev.map(room => room.id === chatroom?.id ? r : room));
                                                setChatroom(null);
                                                handleClose1Modal();
                                            }).catch(e => {
                                                console.error(e);
                                            });
                                        }}>변경</button>

                                    </div>
                                </Modal>
                                <div className="mr-5 w-[50%] flex justify-end items-center">
                                    <button className="hamburger1" id="burger" onClick={() => { setOpen1(!open1), setDrop(!drop) }}>
                                        <span></span>
                                        <span></span>
                                        <span></span>
                                    </button>
                                    <DropDown open={open1} onClose={() => setOpen1(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button="burger">

                                        <button onClick={() => {
                                            chatExit({ chatroomId: chatroom.id, username: user.username }).then((r) => {
                                                // setChatrooms(r);
                                                setChatroom(null);
                                                getChat(keyword, page).then(r => { setChatrooms(r.content); setMaxPage(r.totalPages); }).catch(e => console.log(e));
                                            })
                                        }}>나가기</button>
                                        <button onClick={handleOpen6Modal}>보관함</button>
                                        <button></button>
                                    </DropDown>

                                    <Modal open={isModalOpen6} onClose={handleClose6Modal} escClose={true} outlineClose={true}>
                                        <div className="official-color h-[3.125rem] flex justify-center">

                                            <div className="text-1xl flex justify-center gap-20 mt-1">
                                                <button className={` ${activeTab === 'photos' ? 'bg-white w-[6.25rem] rounded-t-2xl' : 'w-[6.25rem]'}`} onClick={() => setActiveTab('photos')}>사진</button>
                                                <button className={` ${activeTab === 'files' ? 'bg-white w-[6.25rem] rounded-t-2xl' : 'w-[6.25rem]'}`} onClick={() => setActiveTab('files')}>파일</button>
                                                <button className={` ${activeTab === 'links' ? 'bg-white w-[6.25rem] rounded-t-2xl' : 'w-[6.25rem]'}`} onClick={() => setActiveTab('links')}>링크</button>
                                            </div>
                                        </div>
                                        <div className="content">
                                            {activeTab === 'photos' && (
                                                <div className="flex items-center flex-col w-[30rem] h-[50rem] overflow-x-hidden overflow-y-scroll">

                                                    <div className="font-bold flex-wrap: wrap text-3xl m-3">첨부한 이미지</div>
                                                    <div className="flex w-[28.125rem] flex-wrap">
                                                        {imageList.map(image => (
                                                            <div key={image.id} className="w-[9.375rem]">
                                                                <div className="h-[9.375rem] flex justify-center flex-col m-2">
                                                                    <img className="w-full h-[8.125rem] border-2 border-gray-300" src={image?.message} />
                                                                    <span className="w-full h-[1.25rem] font-bold flex justify-center text-sm">{image.name}
                                                                        <div className="text-gray-400 ml-2 text-xs mt-1">
                                                                            {getChatDateTimeFormat(image.sendTime)}
                                                                        </div>
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                            {activeTab === 'files' && (
                                                <div className="flex items-center flex-col w-[30rem] h-[50rem] overflow-x-hidden overflow-y-scroll">

                                                    <div className="font-bold flex-wrap: wrap text-3xl m-3">첨부한 파일</div>
                                                    <div className="flex w-[28.125rem] flex-wrap">
                                                        {fileList.map(file => (
                                                            <div key={file.id} className="w-[9.375rem]">
                                                                <div className="h-[9.375rem] flex justify-center items-center flex-col m-2 border-2 border-gray-300 m-3">
                                                                    <a href={file?.message} download target="_blank" rel="noopener noreferrer">
                                                                        <img src={getFileIcon(file?.message)} className="w-[6.25rem] h-[6.25rem] mr-2" alt="" />
                                                                        {/* {file?.message} */}
                                                                    </a>

                                                                    <span className="w-full h-[1.25rem] font-bold flex justify-center text-sm">{file.name}
                                                                        <div className="text-gray-400 ml-2 text-xs mt-1">
                                                                            {getChatDateTimeFormat(file.sendTime)}
                                                                        </div>
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                            {activeTab === 'links' && (
                                                <div className="flex items-center flex-col w-[37.5rem] h-[50rem] overflow-x-hidden overflow-y-scroll">

                                                    <div className="font-bold text-3xl m-3">첨부한 링크</div>
                                                    <div className="flex flex-col">
                                                        {linkList.map(link => (
                                                            <div key={link.id} className="">
                                                                <div className="flex justify-center flex m-2 w-[31.25rem]">
                                                                    <a href={link?.message} className="w-[25rem]" target="_blank" rel="noopener noreferrer">{link?.message}</a>
                                                                    <span className="w-[12.5rem] h-[1.25rem] font-bold flex justify-center text-sm">{link.name}
                                                                        <div className="text-gray-400 ml-2 text-xs mt-1 w-[6.25rem]">
                                                                            {getChatDateTimeFormat(link.sendTime)}
                                                                        </div>
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                        </div>

                                    </Modal>
                                </div>
                            </div>
                            {/* 공지 */}
                            <div className={chatroom?.notification?.message ? '' : ' hidden'}>
                                {!showNotification && (
                                    <div className="w-full flex justify-start ml-3" onClick={toggleNotification}>
                                        <div className="bg-[#abcdae] w-[4.375rem] h-[4.375rem] rounded-full flex items-center fixed p-4">
                                            <img src="/noti.png" className="w-[3.75rem] h-[3.75rem] mr-2" alt="Notification" />
                                        </div>
                                    </div>
                                )}

                                {showNotification && (
                                    <div className="w-full flex justify-center">
                                        <div className="bg-[#abcdae] w-[59%] h-[4.375rem] rounded-md flex items-center fixed p-4">
                                            <img onClick={() => setShowNotification(false)} src="/noti.png" className="w-[3.75rem] h-[3.75rem] mr-2" alt="" />
                                            <div className="w-full text-white" style={{ opacity: 1 }}>
                                                {chatroom?.notification?.message}
                                            </div>
                                            <button
                                                onClick={() => setShowNotification(false)}
                                                className="h-full flex items-start mr-2 text-white text-3xl" style={{ opacity: 1 }}>
                                                ⨯
                                            </button>
                                        </div>
                                    </div>
                                )}
                            </div>

                            <div ref={chatBoxRef} onScroll={loadPage} className="SD:h-[20rem] HD:h-[25rem] h-[35.9375rem] w-[100%] overflow-x-hidden overflow-y-auto">
                                {/* 날짜 */}
                                <div className="flex justify-center">
                                    {/* <div className="inline-flex bg-gray-400 rounded-full text-white font-bold px-4 py-2 text-sm justify-center mt-2 bg-opacity-55">
                                    2024년 07월 05일 금요일
                                </div> */}
                                </div>

                                {/* 채팅 */}
                                {/* {messageListTmp?.map((t, index) => <div key={t.id} className="w-full flex flex-col items-start m-1"> */}
                                {messageList?.map((t, index) => <div key={t.id + "message"} className="w-full flex flex-col items-start m-1">
                                    {
                                        t.username == user?.username ?
                                            <div className="flex w-full justify-end" id={index.toString()}>
                                                <div className="w-6/12 flex justify-end mr-2">

                                                    <div className="text-sm text-red-600 ml-3 mt-5 whitespace-nowrap" >{t?.readUsers}, {joinMembers - (t?.readUsers ?? 0)}</div>

                                                    <button
                                                        className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap"
                                                        onClick={() => {
                                                            notification({ chatroomId: chatroom?.id, messageId: Number(t?.id) })
                                                                .then((r) => {
                                                                    chatrooms[(chatrooms)?.findIndex(room => room.id == r?.id)] = r;
                                                                    setChatrooms([...chatrooms]);
                                                                    setChatroom(r);

                                                                })
                                                                .catch((e) => {
                                                                    console.error(e);
                                                                });
                                                        }}
                                                    >
                                                        공지 설정
                                                    </button>
                                                    <button className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap"
                                                        onClick={() => {
                                                            deleteMessage(Number(t?.id))
                                                                .then(() => {
                                                                    setMessageList(prevMessageList => prevMessageList.filter(message => message.id !== t.id));
                                                                })
                                                                .catch((e) => {
                                                                    console.error("Error deleting message:", e);
                                                                });
                                                        }}>삭제</button>
                                                    <div className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getChatDateTimeFormat(t?.sendTime)}</div>
                                                    <div className="inline-flex rounded-2xl text-sm text-white justify-center m-2 official-color">
                                                        <div className="mt-2 mb-2 ml-3 mr-3">
                                                            {
                                                                t?.messageType === 0 ? (
                                                                    <div>{t?.message}</div>
                                                                ) : t?.messageType === 1 ? (
                                                                    <img src={t?.message} />
                                                                ) : t?.messageType === 2 ? (
                                                                    <a href={t?.message} target="_blank" rel="noopener noreferrer">{t?.message}</a>
                                                                ) : t?.messageType === 3 ? (
                                                                    <a href={t?.message} download target="_blank" rel="noopener noreferrer" className="flex items-center">
                                                                        <img src={getFileIcon(t?.message)} className="w-[50px] h-[50px] mr-2" alt="" />
                                                                        {t?.message}
                                                                    </a>
                                                                ) : (
                                                                    <></>
                                                                )
                                                            }
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            :
                                            <div className="flex w-6/12 ml-2 mb-3" id={index.toString()}>
                                                <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                                                <div className="flex flex-col ml-2">
                                                    <div className="text-black font-bold ml-2">
                                                        {t?.name}
                                                    </div>
                                                    <div className="w-full flex">
                                                        <div className="text-black ml-2">
                                                            <div className="mt-2 mb-2 ml-3 mr-3">
                                                                {
                                                                    t?.messageType === 0 ? (
                                                                        <div>{t?.message}</div>
                                                                    ) : t?.messageType === 1 ? (
                                                                        <img src={t?.message} />
                                                                    ) : t?.messageType === 2 ? (
                                                                        <a href={t?.message} target="_blank" rel="noopener noreferrer">{t?.message}</a>
                                                                    ) : t?.messageType === 3 ? (
                                                                        <a href={t?.message} download target="_blank" rel="noopener noreferrer">{t?.message}</a>
                                                                    ) : (
                                                                        <div></div>
                                                                    )
                                                                }
                                                            </div>
                                                        </div>
                                                        <div className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getChatDateTimeFormat(t?.sendTime)}</div>
                                                        <div className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">삭제</div>

                                                        <button
                                                            className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap"
                                                            onClick={() => {
                                                                notification({ chatroomId: chatroom?.id, messageId: Number(t?.id) })
                                                                    .then((r) => {
                                                                        chatrooms[(chatrooms)?.findIndex(room => room.id == r?.id)] = r;
                                                                        setChatrooms([...chatrooms]);
                                                                        setChatroom(r);

                                                                    })
                                                                    .catch((e) => {
                                                                        console.error(e);
                                                                    });
                                                            }}
                                                        >
                                                            공지 설정
                                                        </button>
                                                        <button className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap"
                                                            onClick={() => {
                                                                deleteMessage(Number(t?.id))
                                                                    .then(() => {
                                                                        setMessageList(prevMessageList => prevMessageList.filter(message => message.id !== t.id));
                                                                    })
                                                                    .catch((e) => {
                                                                        console.error("Error deleting message:", e);
                                                                    });
                                                            }}>삭제</button>
                                                        <div className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getChatDateTimeFormat(t?.sendTime)}</div>
                                                        <div className="inline-flex rounded-2xl text-sm text-white justify-center m-2 official-color">
                                                            <div className="mt-2 mb-2 ml-3 mr-3">
                                                                {
                                                                    t?.messageType === 0 ? (
                                                                        <div>{t?.message}</div>
                                                                    ) : t?.messageType === 1 ? (
                                                                        <img src={t?.message} />
                                                                    ) : t?.messageType === 2 ? (
                                                                        <a href={t?.message} target="_blank" rel="noopener noreferrer">{t?.message}</a>
                                                                    ) : t?.messageType === 3 ? (
                                                                        <a href={t?.message} download target="_blank" rel="noopener noreferrer">{t?.message}</a>
                                                                    ) : (
                                                                        <div></div>
                                                                    )
                                                                }
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                :
                                                <div className="flex w-6/12 ml-2 mb-3" id={index.toString()}>
                                                    <img src="/pigp.png" className="w-[2.5rem] h-[2.5rem] rounded-full" />
                                                    <div className="flex flex-col ml-2">
                                                        <div className="text-black font-bold ml-2">
                                                            {t?.name}
                                                        </div>
                                                        <div className="w-full flex">
                                                            <div className="text-black ml-2">
                                                                <div className="mt-2 mb-2 ml-3 mr-3">
                                                                    {
                                                                        t?.messageType === 0 ? (
                                                                            <div>{t?.message}</div>
                                                                        ) : t?.messageType === 1 ? (
                                                                            <img src={t?.message} />
                                                                        ) : t?.messageType === 2 ? (
                                                                            <a href={t?.message} target="_blank" rel="noopener noreferrer">{t?.message}</a>
                                                                        ) : t?.messageType === 3 ? (
                                                                            <a href={t?.message} download target="_blank" rel="noopener noreferrer">{t?.message}</a>
                                                                        ) : (
                                                                            <div></div>
                                                                        )
                                                                    }
                                                                </div>
                                                            </div>
                                                            <div className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getChatDateTimeFormat(t?.sendTime)}</div>
                                                            <div className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">삭제</div>

                                                            <button
                                                                className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap"
                                                                onClick={() => {

                                                                    notification({ chatroomId: chatroom?.id, messageId: Number(t?.id) })
                                                                        .then((r) => {


                                                                            chatrooms[(chatrooms)?.findIndex(room => room.id == r?.id)] = r;
                                                                            setChatrooms([...chatrooms]);
                                                                            setChatroom(r);

                                                                        })
                                                                        .catch((e) => {
                                                                            console.error(e);
                                                                        });
                                                                }}
                                                            >
                                                                공지 설정
                                                            </button>
                                                            <div className="text-sm text-red-600 ml-3 mt-5 whitespace-nowrap"> {t.readUsers}{joinMembers - (t?.readUsers ?? 0)}</div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                    }
                                </div>)
                                }
                            </div>

                            <div className="absolute bottom-0 flex flex-col border-2 border-gray-300 rounded-md w-[100%] h-[9.375rem] items-start">
                                <div className="h-full m-2 w-[98%]">
                                    <textarea id="chat_input" key={chatroom?.id} autoFocus={!isModalOpen && !isModalOpen2} placeholder="내용을 입력하세요" className="resize-none bolder-0 outline-none bg-white text-black w-full h-full" onChange={e => setMessage(e.target.value)}
                                        value={message}
                                        onKeyDown={e => {
                                            if (e.key === "Enter" && !e.shiftKey) { // Shift + Enter를 누를 경우는 줄바꿈
                                                e.preventDefault(); // 폼 제출 방지
                                                if (isReady) {
                                                    document.getElementById('send')?.click();

                                                    setMessage(''); // 메시지 전송 후 입력 필드 초기화
                                                }
                                            }
                                        }} />
                                </div>

                                {/* 툴팁 부분 */}
                                <div className="flex w-[98%] justify-between font-bold text-gray-500">
                                    <div className="flex">

                                        <button id="emoticon">
                                            <img src="/emoticon.png" className="w-[1.5625rem] h-[1.5625rem] items-center justify-center m-1" />
                                        </button>
                                        <Tooltip anchorSelect="#emoticon" clickable>
                                            <button>이모티콘</button>
                                        </Tooltip>

                                        <button id="book">
                                            <img src="/book.png" className="w-[1.5625rem] h-[1.5625rem] items-center justify-center m-1" />
                                        </button>
                                        <Tooltip anchorSelect="#book" clickable>
                                            <div className="flex flex-col">

                                                <button id="reservationMessage" onClick={handleOpen4Modal}>예약 전송</button>
                                                <button id="reservationMessageList" onClick={handleOpen5Modal}>메시지 예약함</button>
                                            </div>
                                        </Tooltip>
                                        <Modal open={isModalOpen4} onClose={handleClose4Modal} escClose={true} outlineClose={true}>
                                            <div className="flex flex-col items-cnete justify-center m-3">
                                                <div className="flex items-center justify-center font-bold">예약 메시지 보내기</div>
                                                <div className="flex flex">
                                                    <div className="m-2">예약 시간 입력 : </div>
                                                    <input type="datetime-local" onChange={e => setSendDate(e.target.value ? new Date(e.target.value) : null)} />
                                                </div>
                                                <textarea className="h-[25rem] " placeholder="내용을 입력하세요" onChange={e => setMessage(e.target.value)}
                                                    value={message}

                                                    onKeyDown={e => {
                                                        if (e.key === "Enter" && !e.shiftKey) { // Shift + Enter를 누를 경우는 줄바꿈
                                                            e.preventDefault(); // 폼 제출 방지
                                                            setMessage(''); // 메시지 전송 후 입력 필드 초기화        
                                                        }
                                                    }
                                                    } />

                                                <button onClick={() => {

                                                    const messageReservationRequestDTO = { chatroomId: chatroom?.id, message: message, messageType: 0, reservationDate: sendDate };

                                                    createMessageReservation(messageReservationRequestDTO).then(r => {
                                                        setReservationMessageList(r.content);

                                                        handleClose4Modal();
                                                    }).catch(e => {
                                                        console.error(e);
                                                    });
                                                }}>예약하기</button>
                                            </div>
                                        </Modal>
                                        <Modal open={isModalOpen5} onClose={handleClose5Modal} escClose={true} outlineClose={true}>
                                            <div className="flex flex-col items-center m-3">
                                                <div className="flex items-center justify-center font-bold text-xl mb-3 w-[62.5rem]">예약 메시지 리스트</div>
                                                <div className="overflow-auto h-[31.25rem] w-full border border-gray-300 rounded-lg">
                                                    <div className="flex justify-between items-center font-bold text-lg m-2">
                                                        <div className="w-1/5 text-center whitespace-nowrap">채팅방 이름</div>
                                                        <div className="w-1/5 text-center whitespace-nowrap">예약 시간</div>
                                                        <div className="w-2/5 text-center whitespace-nowrap">보낸 메시지</div>
                                                        <div className="w-1/5">편집</div>
                                                    </div>
                                                    <ul className="m-3">
                                                        {reservationMessageList && reservationMessageList.map((reservationMessage, index) => (
                                                            <li key={index} className="flex justify-between items-center mb-5 border-b pb-2">
                                                                <span className="text-md w-1/5 text-center">{getChatroomNameById(reservationMessage.chatroomId)}</span>
                                                                <span className="text-md w-1/5 text-center">{getChatDateTimeFormat(reservationMessage.reservationDate)}</span>
                                                                <span className="text-md w-2/5 text-center">{reservationMessage.message}</span>
                                                                <div className="flex w-1/5">
                                                                    <button className="btn w-1/2 whitespace-nowrap">수정</button>
                                                                    <button className="btn w-1/2 whitespace-nowrap"
                                                                        onClick={() => {
                                                                            deleteMessageReservation(reservationMessage.id).then(r => {
                                                                                setReservationMessageList(r.content);
                                                                            })
                                                                        }}>삭제</button>
                                                                </div>
                                                            </li>
                                                        ))}
                                                    </ul>
                                                </div>
                                            </div>
                                        </Modal>

                                        <button id="file" onClick={() => { file.current?.click() }}>
                                            <img src="/file.png" data-tip="파일" className="file w-[1.5625rem] h-[1.5625rem] items-center justify-center m-1" />
                                            <input ref={file} type="file" hidden onChange={e => {
                                                if (e.target.files && e.target.files[0]) {
                                                    const selectedFile = e.target.files[0];
                                                    if (selectedFile instanceof File) { // File 인스턴스 확인
                                                        chatUploadFile({ chatroomId: chatroom?.id, file: selectedFile })
                                                            .then(r => { setMessage(r); setMessageType(3); const interval = setInterval(() => { document.getElementById('send')?.click(); clearInterval(interval); }, 100) })
                                                            .catch(e => console.log(e));
                                                    }
                                                    e.target.value = '';
                                                }
                                            }} />
                                        </button>
                                        <Tooltip anchorSelect="#file" clickable>
                                            <button >파일 전송</button>
                                        </Tooltip>
                                        <button id="image" onClick={() => { image.current?.click() }}>
                                            <img src="/image.png" data-tip="이미지" className="image w-[1.5625rem] h-[1.5625rem] items-center justify-center m-1" />
                                            <input ref={image} type="file" hidden onChange={e => {
                                                if (e.target.files && e.target.files[0]) {
                                                    const selectedFile = e.target.files[0];
                                                    if (selectedFile instanceof File) { // File 인스턴스 확인
                                                        chatUploadFile({ chatroomId: chatroom?.id, file: selectedFile })
                                                            .then(r => { setMessage(r); setMessageType(1); const interval = setInterval(() => { document.getElementById('send')?.click(); clearInterval(interval); }, 100) })
                                                            .catch(e => console.log(e));
                                                    }
                                                    e.target.value = '';
                                                }
                                            }} />
                                        </button>

                                        <Tooltip anchorSelect="#image" clickable>
                                            <button >이미지 전송</button>
                                        </Tooltip>

                                        <button id="link" onClick={handleOpen3Modal}>
                                            <img src="/link.png" className="w-[1.5625rem] h-[1.5625rem] items-center justify-center m-1" />
                                        </button>

                                        <Tooltip anchorSelect="#link" clickable>
                                            <button>링크 전송</button>
                                        </Tooltip>


                                        <Modal open={isModalOpen3} onClose={handleClose3Modal} escClose={true} outlineClose={true}>
                                            <div className="overflow-auto w-full m-2">
                                                <div className="font-bold text-3xl m-3 mb-8 flex justify-center">링크 삽입</div>
                                                <input placeholder="링크를 입력해주세요"
                                                    className="bolder-0 outline-none bg-white text-black"
                                                    onChange={e => setMessage(e.target.value)}
                                                    value={message}

                                                    onKeyDown={e => {
                                                        setMessageType(2);
                                                        if (e.key === "Enter" && !e.shiftKey) { // Shift + Enter를 누를 경우는 줄바꿈
                                                            e.preventDefault(); // 폼 제출 방지
                                                            if (isReady) {
                                                                socket.publish({
                                                                    destination: "/api/pub/message/" + chatroom?.id,
                                                                    body: JSON.stringify({ username: user?.username, message: message, messageType: messageType })
                                                                });
                                                                setMessage(''); // 메시지 전송 후 입력 필드 초기화        
                                                            }
                                                        }
                                                    }}

                                                />
                                                <button className="btn"
                                                    onClick={() => {
                                                        if (isReady) {

                                                            socket.publish({
                                                                destination: "/api/pub/message/" + chatroom?.id,
                                                                body: JSON.stringify({ username: user?.username, message: message, messageType: messageType })
                                                            });
                                                        }
                                                    }}

                                                >전송</button>
                                            </div>
                                        </Modal>
                                    </div>
                                    <button id="sendMessage">
                                        <img id="send" src="/send.png" className="send w-[2.5rem] h-[2.5rem] items-center justify-center m-1" onClick={() => {

                                            if (isReady && message) {
                                                socket.publish({
                                                    destination: "/api/pub/message/" + chatroom?.id,
                                                    body: JSON.stringify({ username: user?.username, message: message, messageType: messageType })
                                                });
                                                setMessage("");
                                                setMessageType(0);
                                                (document.getElementById("chat_input") as HTMLInputElement).value = '';
                                            }
                                        }} />
                                    </button>
                                </div>
                            </div>

                        </div >


                    </>
                    <Modal open={isModalOpen} onClose={handleCloseModal} escClose={true} outlineClose={true}>
                        <div>
                            <div className="font-bold text-3xl m-3 mb-8 flex justify-center">멤버 추가하기</div>
                            <div className="flex justify-items-center flex-row border-2 border-gray rounded-full w-[90%] h-[3.125rem] mb-5">
                                <img src="/searchg.png" className="w-[1.875rem] h-[1.875rem] m-2" alt="검색 사진" />
                                <input
                                    // ref={addUsersInputRef}
                                    type="text"
                                    placeholder="참여자 검색"
                                    className="bolder-0 outline-none bg-white text-black w-[80%]"
                                    value={userKeyword}
                                    onChange={e => { setUserKeyword(e.target.value); e.target.focus(); }}
                                />
                                <button className="text-gray-300 whitespace-nowrap"
                                    onClick={handleUserSearch} >
                                    검색
                                </button>
                            </div>
                            <div className="overflow-auto h-[31.25rem]">
                                <ul className="m-3">
                                    {searchedUsers.filter(user => !chatroom?.users.includes(user.username)).map((user, index) => (
                                        <li key={index} className="flex justify-between items-center mb-5">
                                            <span className="w-[3.125rem] h-[3.125rem]">
                                                <img src={user.url ? user.url : "/pin.png"} alt="User profile" className="w-[3.125rem] h-[3.125rem]" />
                                            </span>
                                            <span className="font-bold text-md m-3">{user.name}</span>
                                            <span className=" text-md m-3">부서</span>
                                            <span className="text-md m-3">역할</span>
                                            <button onClick={() => {
                                                addUser({ chatroomId: chatroom.id, username: user.username }).then(r => {

                                                }).catch(e => {
                                                    console.log(e)
                                                })
                                            }} className="font-bold text-3xl m-3">+</button>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </div>
                    </Modal>
                </div>
            </div>
        </div>
    </Main>
}

