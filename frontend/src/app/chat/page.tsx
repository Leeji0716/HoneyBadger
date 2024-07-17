"use client";
import { useEffect, useRef, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import Modal from "../Global/Modal";
import { Tooltip } from 'react-tooltip';

import { chatExit, getChat, getUser, getChatDetail, notification, editChatroom, getUsers, addUser, makeChatroom, deleteMessage, chatUploadFile, getUpdateMessageList } from "../API/UserAPI";
import { getChatDateTimeFormat } from "../Global/Method";
import { getChatShowDateTimeFormat } from "../Global/Method";
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
        users: string[]
        latestMessage: messageResponseDTO
        notification: messageResponseDTO
        alarmCount: number
    }

    interface chatroomRequestDTO {
        name: string;
        users: string[];
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
    const ACCESS_TOKEN = typeof window == 'undefined' ? null : localStorage.getItem('accessToken');
    const [socket, setSocket] = useState(null as any);
    const [temp, setTemp] = useState(null as any);
    const [isReady, setIsReady] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isModalOpen1, setIsModalOpen1] = useState(false);
    const [isModalOpen2, setIsModalOpen2] = useState(false);
    const [userList, setUserList] = useState([] as any[])
    const [selectedUsers, setSelectedUsers] = useState(new Set<string>());
    const [chatroomName, setChatroomName] = useState('');
    const file = useRef(null as any);
    const [keyword, setKeyword] = useState('');
    const [page, setPage] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [maxPage, setMaxPage] = useState(0);
    const chatBoxRef = useRef<HTMLDivElement>(null);

    function handleOpenModal() {
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

    useEffect(() => {
        if (ACCESS_TOKEN)
            getUser().then(r => {
                console.log("Sdfsdfgfhcvbfcvbcvb");
                setUser(r);
                getUsers().then(r => {
                    setUserList(r);

                }).catch(e => console.log(e))
                getChat(keyword, page).then(r => {
                    console.log('ddddddddddddddddddd');
                    console.log(r);
                    setChatrooms(r.content);
                }).catch(e => console.log(e))
            }).catch(e => console.log(e));
        else
            window.location.href = "/";
    }, [ACCESS_TOKEN])

    useEffect(() => {
        console.log("dfdfdfdfdfdf");
        setSocket(getSocket([], () => setIsReady(true)));
    }, [])

    useEffect(() => {
        if (temp) {
            const test: messageResponseDTO[] = [...messageList];
            test.push(temp);
            console.log("test");
            console.log(test);
            setMessageList(test);
            // setChatDetail(temp);
            setTemp(null);
        }
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


    const loadPage = () => {
        const chatBox = chatBoxRef.current;


        if (chatBox != null) {
            const scrollLocation = chatBox?.scrollTop;
            const maxScroll = chatBox.scrollHeight - chatBox.clientHeight;
    

            if (!isLoading && scrollLocation >= maxScroll && page < maxPage - 1) {

                console.log("aaaaaaaaaaaaaa")
                console.log("bbbbb" + maxPage)
                console.log(page)

                setIsLoading(true);  // 로딩 시작

                getChatDetail(chatroom.id, page + 1)
               
                    .then(response => {
                        console.log("설마 여기가 타니?????? 너???????")
                        // 데이터가 있는 경우 새 메시지 리스트에 추가
                        if (response.content.length > 0) {
                            console.log(response.content);
                            console.log(messageList);
                            const newMessageList = [...messageList, ...response.content];
                            setMessageList(newMessageList);

                            setMaxPage(response.totalPages);
                            setPage(page + 1);
                        }
                        setIsLoading(false);  // 로딩 완료
                    })
                    .catch(error => {
                        console.error(error);
                        setIsLoading(false);  // 에러 시 로딩 중지
                    });
            }
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
        if (chatroomName && selectedUsers.size > 0) {
            const users = Array.from(selectedUsers);

            if (user && !users.includes(user.username)) {
                users.push(user.username);
            }

            const chatroomRequest: chatroomRequestDTO = { name: chatroomName, users };
            makeChatroom(chatroomRequest)
                .then(r => {
                    setIsModalOpen2(false);
                    setSelectedUsers(new Set());
                    setChatroomName('');
                })
                .catch(e => {
                    console.error(e);
                });
        } else {
            console.error("이름이나 유저를 선택해주세요");
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

    function ChatList({ Chatroom, ChatDetail }: { Chatroom: chatroomResponseDTO, ChatDetail: messageResponseDTO }) {
        const joinMembers: number = Chatroom.users.length;
        const [updateMessageList, setUpdateMessageList] = useState<messageResponseDTO[]>([]);
        function getValue(confirm: number) {

            switch (joinMembers) {
                case 2: return <img src="/pin.png" className="m-2 w-[80px] h-[80px] rounded-full" />;
                case 3: return <div className="m-2 w-[80px] h-[80px] flex flex-col justify-center items-center ">
                    <div className="w-[80px] h-[40px] flex">
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full ml-2 mt-2" />
                    </div>
                    <div className="w-[80px] h-[40px] flex justify-end">
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full mr-2 mb-2" />
                    </div>
                </div>
                case 4: return <div className="m-2 w-[80px] h-[80px] flex flex-col justify-center items-center ">
                    <div className="w-[80px] h-[40px] flex justify-center">
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                    </div>
                    <div className="w-[80px] h-[40px] flex">
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                        <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                    </div>
                </div>
                default:
                    return <div className="m-2 w-[80px] h-[80px] flex flex-col justify-center items-center ">
                        <div className="w-[80px] h-[40px] flex">
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                        </div>
                        <div className="w-[80px] h-[40px] flex">
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                            <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
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
                    socket.unsubscribe("/api/sub/message/" + Chatroom?.id);
                    socket.unsubscribe("/api/sub/read/" + Chatroom?.id);
                    socket.unsubscribe("/api/sub/updateChatroom/" + Chatroom?.id);
                }
                setChatroom(Chatroom);
            

                getChatDetail(Chatroom?.id, nowPage).then(r => {

                    setMessageList(r.content);
                    setMaxPage(r.totalPages);

                    // url 통해서 messageList 요청 -> 요청().then(r=> setMessageList(r)).catch(e=>console.log(e));
                    socket.subscribe("/api/sub/message/" + Chatroom?.id, (e: any) => {
                        const message = JSON.parse(e.body).body;
                        const temp = { id: message?.id, message: message?.message, sendTime: message?.sendTime, username: message?.username, messageType: message.messageType } as messageResponseDTO; // 위에꺼 확인해보고 지우세요
                        setTemp(temp);

                        socket.publish({
                            destination: "/api/pub/read/" + Chatroom?.id,
                            body: JSON.stringify({ username: user?.username })
                        });

                        socket.publish({
                           
                            destination: "/api/pub/updateChatroom/" + Chatroom?.id,
                            body: JSON.stringify({ username: user?.username})
                        });
                        

                    });

                    socket.subscribe("/api/sub/read/" + Chatroom?.id, (e: any) => {
                        const data = JSON.parse(e.body);

                        getUpdateMessageList(Chatroom?.id).then((updateMessageList => {
                            const index = r.content.findIndex((e: messageResponseDTO) => e.id === updateMessageList[0].id);
                            const qweqwe = [...r.content];
                            // qweqwe.splice(index, qweqwe.length - 1, updateMessageList);
                            qweqwe.splice(index, qweqwe.length - index, ...updateMessageList);
                            // setUpdateMessageList(qweqwe);
                            setMessageList(qweqwe);
                        }));


                    }, JSON.stringify({ username: user?.username }));

                }).catch(e => console.log(e));

                getChat(keyword, nowPage).then(resp => {
                    console.log("여기부터 :" + resp);
                    console.log(resp);

                    socket.subscribe("/api/sub/updateChatroom/" + Chatroom?.id, (e: any) => {
                        const data = JSON.parse(e.body);
                        console.log("aaaaaaasdasedwqadas");
                        console.log(data);


                        console.log("assad?????????????asa");
                        console.log(data.body.id);
                        // console.log(resp.content.id);
                        const index = resp.content.findIndex((e:any) => e.id == data.body.id);
                        
                        chatroom[index]=data.body;
                        setChatroom([...chatroom]);

                    }, JSON.stringify({ username: user?.username }));
                    
                })
                
                
            }
        }}>
            {getValue(joinMembers)}

            <div className="w-full m-2 flex flex-col">
                <div className="text-black font-bold">
                    {Chatroom?.name ? (
                        <span>{Chatroom.name}</span>
                    ) : (
                        Chatroom?.users
                            .filter((username: any) => username !== user?.username) // 현재 사용자 제외
                            .map((username: any, index: number, array: any[]) => (
                                <span key={username}>
                                    {username}
                                    {index < array.length - 1 && ", "}
                                </span>
                            ))
                    )}
                </div>
                <div className="flex justify-between mt-2 text-black">
                    {Chatroom?.latestMessage?.messageType === 0
                        ? Chatroom?.latestMessage?.message
                        : <p>사진을 보냈습니다.</p> // todo:타입이 추가되면 설정해야 한다
                    }
                </div>
            </div>
            <div className="w-3/12 h-full flex flex-col justify-end items-end mr-4">
                <div>
                    <p className="text-gray-300 whitespace-nowrap">{getChatShowDateTimeFormat(Chatroom?.latestMessage?.sendTime)}</p>
                </div>
                {Chatroom?.alarmCount == 0 ? "" : <div className="bg-red-500 rounded-full w-[20px] h-[20px] flex justify-center items-center mt-2">
                    <p className="text-white text-sm">{Chatroom?.alarmCount}</p>
                </div>}

            </div>
        </div>
    }

    function ChatDetil({ Chatroom, messageList }: { Chatroom: chatroomResponseDTO, messageList: messageResponseDTO[] }) {
        const joinMembers = Array.isArray(chatroom.users) ? chatroom.users.length : 0;
        const [message, setMessage] = useState('');
        const [roomName, setRoomName] = useState(chatroom?.name);
        const [messageType, setMessageType] = useState(0);
        return <div>
            <div className="flex w-full justify-between border-b-2">
                <div className="text-black flex w-[50%]">
                    <img src="/pig.png" className="m-2 w-[70px] h-[70px] rounded-full" />
                    <div className="flex flex-col justify-center">
                        <div className="flex">
                            <div className="text-black font-bold text-3xl mb-1 whitespace-nowrap">
                                {chatroom?.name ? (
                                    <span>{chatroom.name}</span>
                                ) : (
                                    chatroom?.users
                                        ?.filter((username: any) => username !== user?.username) // 현재 사용자 제외
                                        ?.map((username: any, index: number, array: []) => (
                                            <span key={username}>
                                                {username}
                                                {index < array.length - 1 && ", "}
                                            </span>
                                        ))
                                )}
                            </div>
                            <button onClick={handleOpen1Modal}> 이름편집</button>
                        </div>
                        <div className="flex items-center gap-1">
                            <button onClick={handleOpenModal}>
                                <img src="/people.png" className="w-[30px] h-[30px]" />
                            </button>
                            <p className="flex items-end text-xl w-[30px] h-[30px] text-official-color">
                                {joinMembers}
                            </p>
                        </div>
                    </div>
                </div>
                <Modal open={isModalOpen} onClose={handleCloseModal} escClose={true} outlineClose={true}>
                    <div className="overflow-auto">
                        <p className="font-bold text-3xl m-3 mb-8 flex justify-center">멤버 추가하기</p>
                        <ul className="m-3">
                            {userList.map((user, index) => (
                                <li key={index} className="flex justify-between items-center mb-5">
                                    <span className="w-[50px] h-[50px]"><img src="/pin.png" alt="" /></span>
                                    <span className="font-bold text-md m-3">{user.name}</span>
                                    <span className=" text-md m-3">부서</span>
                                    <span className="text-md m-3">역할</span>
                                    <button onClick={() => {
                                        addUser({ chatroomId: chatroom.id, username: user.username }).then(r => {
                                            console.log("완료")

                                        }).catch(e => {
                                            console.log(e)
                                        })
                                    }} className="font-bold text-3xl m-3">+</button>
                                </li>
                            ))}
                        </ul>
                    </div>
                </Modal>
                <Modal open={isModalOpen1} onClose={handleClose1Modal} escClose={true} outlineClose={true}>
                    <div className="flex flex-col items-cnete justify-center m-3">
                        <p className="flex items-center justify-center font-bold">이름 편집</p>
                        <input type="text" placeholder={chatroom?.name} value={roomName} onChange={e => { console.log(e.target.value); setRoomName(e.target.value) }}
                        />
                        <button onClick={() => {
                            const updatedChatroom = {
                                ...chatroom,
                                name: roomName
                            };

                            editChatroom({ chatroomId: chatroom.id, chatroomResponseDTO: updatedChatroom }).then(r => {
                                setChatrooms(prev => prev.map(room => room.id === chatroom.id ? r : room));
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
                            })
                        }}>나가기</button>
                        <button>사진/동영상</button>
                        <button>파일</button>
                        <button></button>
                    </DropDown>
                </div>
            </div>
            {/* 공지 */}
            <div className="w-full flex justify-center">
                <div className="bg-[#abcdae] w-[59%] h-[70px] rounded-md flex items-center fixed z-50 absolute p-4">
                    <img src="/noti.png" className="w-[60px] h-[60px] mr-2" alt="" />
                    <p className="w-full text-white" style={{ opacity: 1 }}>
                        {Chatroom?.notification?.message || '공지가 안 뜹니다'}
                    </p>
                    <button className="h-full flex items-start mr-2 text-white text-3xl" style={{ opacity: 1 }}>
                        ⨯
                    </button>
                </div>
            </div>

            <div ref={chatBoxRef} onScroll={loadPage} className="h-[600px] w-[100%] overflow-x-hidden overflow-y-scroll">
                {/* 날짜 */}
                <div className="flex justify-center">
                    <div className="inline-flex bg-gray-400 rounded-full text-white font-bold px-4 py-2 text-sm justify-center mt-2 bg-opacity-55">
                        2024년 07월 05일 금요일
                    </div>
                </div>
                {/* 채팅 */}
                {messageList?.map((t, index) => <div key={index} className="w-full flex flex-col items-start m-1">
                    {
                        t.username == user?.username ?
                            <div className="flex w-full justify-end" id={index.toString()}>
                                <div className="w-6/12 flex justify-end mr-2">

                                    <p className="text-sm text-red-600 ml-3 mt-5 whitespace-nowrap"> {joinMembers - (t?.readUsers ?? 0)}</p>

                                    <button
                                        className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap"
                                        onClick={() => {

                                            notification({ chatroomId: chatroom?.id, messageId: Number(t?.id) })
                                                .then((r) => {

                                                    chatrooms[(chatrooms)?.findIndex(room => room.id == r?.id)] = r;
                                                    setChatrooms([...chatrooms]);
                                                    setChatroom(r);

                                                    console.log(Chatroom);
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
                                                    console.log("----")
                                                    setMessageList(prevMessageList => prevMessageList.filter(message => message.id !== t.id));
                                                })
                                                .catch((e) => {
                                                    console.error("Error deleting message:", e);
                                                });
                                        }}>삭제</button>
                                    <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getChatDateTimeFormat(t?.sendTime)}</p>
                                    <div className="inline-flex rounded-2xl text-sm text-white justify-center m-2 official-color">
                                        <div className="mt-2 mb-2 ml-3 mr-3">
                                            {t?.messageType == 0
                                                ? <><div>{t?.message}</div></>
                                                : <img src={'http://www.벌꿀오소리.메인.한국:8080' + t?.message} />
                                            }
                                        </div>
                                    </div>
                                </div>
                            </div>
                            :
                            <div className="flex w-6/12 ml-2 mb-3" id={index.toString()}>
                                <img src="/pigp.png" className="w-[40px] h-[40px] rounded-full" />
                                <div className="flex flex-col ml-2">
                                    <p className="text-black font-bold ml-2">
                                        {t?.name}
                                    </p>
                                    <div className="w-full flex">
                                        <p className="text-black ml-2">
                                            {t?.messageType == 0
                                                ? t?.message
                                                : <img src={'http://www.벌꿀오소리.메인.한국:8080' + t?.message} />
                                            }
                                        </p>
                                        <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getChatDateTimeFormat(t?.sendTime)}</p>
                                        <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">삭제</p>

                                        <button
                                            className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap"
                                            onClick={() => {

                                                notification({ chatroomId: chatroom?.id, messageId: Number(t?.id) })
                                                    .then((r) => {
                                                        console.log('-=------====');
                                                        // setNotificationMessage(r);

                                                        chatrooms[(chatrooms)?.findIndex(room => room.id == r?.id)] = r;
                                                        setChatrooms([...chatrooms]);
                                                        setChatroom(r);

                                                        console.log(Chatroom);
                                                    })
                                                    .catch((e) => {
                                                        console.error(e);
                                                    });
                                            }}
                                        >
                                            공지 설정
                                        </button>
                                        <p className="text-sm text-red-600 ml-3 mt-5 whitespace-nowrap"> {joinMembers - (t?.readUsers ?? 0)}</p>
                                    </div>
                                </div>
                            </div>
                    }
                </div>)
                }
            </div>
            <div className="flex flex-col border-2 border-gray-300 rounded-md w-[100%] h-[6/12] items-start">
                <div className="h-[100px] m-2 w-[98%]">
                    <textarea placeholder="내용을 입력하세요" className="bolder-0 outline-none bg-white text-black w-full h-full" onChange={e => setMessage(e.target.value)}
                        value={message}

                        onKeyDown={e => {
                            if (e.key === "Enter" && !e.shiftKey) { // Shift + Enter를 누를 경우는 줄바꿈
                                console.log("============> message Type");
                                e.preventDefault(); // 폼 제출 방지
                                if (isReady) {
                                    socket.publish({
                                        destination: "/api/pub/message/" + chatroom?.id,
                                        body: JSON.stringify({ username: user?.username, message: message, messageType: messageType })
                                    });

                                    setMessage(''); // 메시지 전송 후 입력 필드 초기화        
                                }
                            }
                        }} />
                </div>

                {/* 툴팁 부분 */}
                <div className="flex w-[98%] justify-between font-bold text-gray-500">
                    <div className="flex">

                        <button id="emoticon">
                            <img src="/emoticon.png" className="w-[25px] h-[25px] items-center justify-center m-1" />
                        </button>
                        <Tooltip anchorSelect="#emoticon" clickable>
                            <button>이모티콘</button>
                        </Tooltip>

                        <button id="book">
                            <img src="/book.png" className="w-[25px] h-[25px] items-center justify-center m-1" />
                        </button>
                        <Tooltip anchorSelect="#book" clickable>
                            <button>예약 전송</button>
                        </Tooltip>

                        <button id="file" onClick={() => { file.current?.click() }}>
                            <img src="/file.png" data-tip="파일" className="file w-[25px] h-[25px] items-center justify-center m-1" />
                            <input ref={file} type="file" hidden onChange={e => {
                                if (e.target.files && e.target.files[0]) {
                                    const selectedFile = e.target.files[0];
                                    if (selectedFile instanceof File) { // File 인스턴스 확인
                                        chatUploadFile({ chatroomId: chatroom?.id, file: selectedFile })
                                            .then(r => { console.log('============>'); console.log(r); setMessage(r); setMessageType(1); })
                                            .catch(e => console.log(e));
                                    }
                                }
                            }} />
                        </button>
                        <Tooltip anchorSelect="#file" clickable>
                            <button >파일 전송</button>
                        </Tooltip>
                    </div>
                    <button id="sendMessage">
                        <img src="/send.png" className="send w-[40px] h-[40px] items-center justify-center m-1" onClick={() => {
                            if (isReady)
                                socket.publish({
                                    destination: "/api/pub/message/" + chatroom?.id,
                                    body: JSON.stringify({ username: user?.username, message: message, messageType: messageType })
                                });

                        }} />
                    </button>
                </div>
            </div>
        </div >
    }

    return <Main user={user}>
        <div className="w-4/12 flex items-center justify-center h-full pt-10 pb-4">
            {/* 왼쪽 부분 */}
            <div className=" h-11/12 w-11/12 bg-white h-full shadow">
                <div className="flex justify-start text-xl ml-5 mr-5 mt-5 mb-5 text-black">
                    <button className="font-bold" id="button1" onClick={() => { setOpen(!open), setFilter(!filter) }}>채팅{open ? '▴' : '▾'}</button>
                    <DropDown open={open} onClose={() => setOpen(false)} className="bg-white border-2 rounded-md" defaultDriection={Direcion.DOWN} width={100} height={100} button="button1">
                        <button>개인</button>
                        <button>단체</button>
                    </DropDown>
                </div>
                <button onClick={handleOpen2Modal} className="fixed bottom-5 left-10 w-[50px] h-[50px] rounded-full bg-blue-300 text-xl font-bold text-white">
                    +
                </button>
                <Modal open={isModalOpen2} onClose={handleClose2Modal} escClose={true} outlineClose={true}>
                    <div className="overflow-auto w-full">
                        <p className="font-bold text-3xl m-3 mb-8 flex justify-center">채팅방 만들기</p>
                        <div className="flex flex-row border-2 border-gray-300 rounded-md w-[400px] h-[40px] m-2">
                            <input
                                type="text"
                                placeholder="채팅방 이름을 입력해주세요"
                                className="bolder-0 outline-none bg-white text-black"
                                value={chatroomName}
                                onChange={e => setChatroomName(e.target.value)}
                            />
                        </div>
                        <ul className="m-3">
                            {userList.map((user, index) => (
                                <li key={index} className="flex justify-between items-center mb-5">
                                    <span className="w-[50px] h-[50px]"><img src="/pin.png" alt="" /></span>
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
                        <div className="w-full flex justify-center">
                            <button onClick={handleCreateChatroom} className="login-button flex items-center m-2">
                                채팅방 생성
                            </button>
                        </div>
                    </div>
                </Modal>


                <div className="flex flex-col items-center">
                    <div className="flex justify-items-center flex-row border-2 border-gray rounded-full w-[90%] h-[50px] mb-5">
                        <img src="/searchg.png" className="w-[30px] h-[30px] m-2" alt="검색 사진" />
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
                        <p className="font-bold ml-3 text-gray-300">
                            내 프로필
                        </p>
                        <div className="flex hover:bg-gray-400 text-white rounded-md">
                            <img src="/pin.png" className="m-2 w-[80px] h-[80px] rounded-full" />
                            <div className="w-full m-2 flex flex-col">
                                <div className="flex justify-between">
                                    <p className="text-black font-bold">{user?.name}</p>

                                </div>
                                <div className="flex flex-col mt-2">
                                    <p className="text-black">{user?.username}</p>
                                    <p className="text-black text-sm">:)</p>
                                </div>
                            </div>
                            <div className="w-3/12 h-full flex flex-col justify-end items-end mr-4">
                            </div>
                        </div>
                        <p className="font-bold ml-3 text-gray-300 mt-3">
                            대화 목록
                        </p>
                    </div>
                    <div className="w-full justify-end h-[550px] overflow-x-hidden overflow-y-scroll">
                        {chatrooms?.map((chatroom: chatroomResponseDTO, index: number) => <ChatList key={index} Chatroom={chatroom} ChatDetail={chatDetail} />)}
                    </div>
                </div>
            </div>
        </div>

        {/* 오른쪽 부분 */}
        <div className="w-8/12 flex items-center justify-center pt-10 pb-4">
            <div className="h-11/12 w-11/12 bg-white h-full flex flex-col shadow">
                {chatroom != null ? <ChatDetil Chatroom={chatroom} messageList={messageList} /> : <></>}
            </div>
        </div>
    </Main>
}

