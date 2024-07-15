"use client";
import { useEffect, useState } from "react";
import Main from "../Global/Layout/MainLayout";
import DropDown, { Direcion } from "../Global/DropDown";
import Modal from "../Global/Modal";
import { Tooltip } from 'react-tooltip';

import { chatExit, getChat, getUser, getChatDetail, notification, editChatroom, getUsers, addUser, makeChatroom, deleteMessage } from "../API/UserAPI";
import { getChatDateTimeFormat } from "../Global/Method";
import { getSocket } from "../API/SocketAPI";

export default function Chat() {
    interface messageResponseDTO {
        id?: number,
        username: string,
        message: string,
        messageType: number,
        sendTime: number,
        name: string
    }

    interface chatroomResponseDTO {
        id: number,
        name: string,
        users: string[]
        latestMessage: messageResponseDTO
        notification: messageResponseDTO
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
    const [isReady, setReady] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isModalOpen1, setIsModalOpen1] = useState(false);
    const [isModalOpen2, setIsModalOpen2] = useState(false);
    const [userList, setUserList] = useState([] as any[])
    const [selectedUsers, setSelectedUsers] = useState(new Set<string>());
    const [chatroomName, setChatroomName] = useState('');

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
                setUser(r);
                getUsers().then(r => {
                    setUserList(r);

                }).catch(e => console.log(e))
                getChat().then(r => {
                    setChatrooms(r);
                }).catch(e => console.log(e))
            }).catch(e => console.log(e));
        else
            window.location.href = "/";
    }, [ACCESS_TOKEN])

    useEffect(() => {
        setSocket(getSocket([], () => setReady(true)));
    }, [])

    useEffect(() => {
        if (temp) {
            setMessageList([...messageList, temp]);
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

    function ChatList({ Chatroom, ChatDetail }: { Chatroom: chatroomResponseDTO, ChatDetail: messageResponseDTO }) {
        // console.log("ASDASD");
        // console.log(Chatroom);

        const joinMembers: number = Chatroom.users.length;
        function getValue(confirm: number) {
            // console.log("asdsaas");
            // console.log(joinMembers);
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
                if (chatroom) {
                    socket.unsubscribe("/api/sub/message/" + chatroom.chatroom?.id);
                }
                setChatroom(Chatroom);
                // url 통해서 messageList 요청 -> 요청().then(r=> setMessageList(r)).catch(e=>console.log(e));
                socket.subscribe("/api/sub/message/" + Chatroom?.id, (e: any) => {
                    const message = JSON.parse(e.body).body;
                    // console.log(message); // Type -> 숫자로 변경
                    const temp = { id: message?.id, message: message?.message, sendTime: message?.sendTime, username: message?.username, messageType: 0 } as messageResponseDTO; // 위에꺼 확인해보고 지우세요
                    setTemp(temp);
                });

                getChatDetail(Chatroom?.id).then(r => {
                    // setMessageList(r);
                    setMessageList(r);

                    const timer = setInterval(() => {
                        document.getElementById((r?.length - 1).toString())?.scrollIntoView();
                        clearInterval(timer);
                    }, 100);
                }).catch(e => console.log(e));

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
                <div className="flex justify-between mt-2">
                    <p className="text-black text-sm">{Chatroom?.latestMessage?.message}</p>
                </div>
            </div>
            <div className="w-3/12 h-full flex flex-col justify-end items-end mr-4">
                <div>
                    <p className="text-gray-300 whitespace-nowrap">{getChatDateTimeFormat(Chatroom?.latestMessage?.sendTime)}</p>
                </div>
                <div className="bg-red-500 rounded-full w-[20px] h-[20px] flex justify-center items-center mt-2">
                    <p className="text-white text-sm">1</p>
                </div>
            </div>
        </div>
    }

    function ChatDetil({ Chatroom, chatDetail }: { Chatroom: chatroomResponseDTO, chatDetail: messageResponseDTO }) {
        const joinMembers = Array.isArray(chatroom.users) ? chatroom.users.length : 0;
        const [message, setMessage] = useState('');
        const [roomName, setRoomName] = useState(chatroom?.name);
        // console.log("==========");
        // console.log(chatroom);
        // console.log(chatroom.messageResponseDTOList);
        // console.log("시시간간");
        return <div>
            <div className="flex w-full justify-between border-b-2">
                <div className="text-black flex w-[50%]">
                    <img src="/pig.png" className="m-2 w-[70px] h-[70px] rounded-full" />
                    <div className="flex flex-col justify-center">
                        <div className="flex">
                            <p className="text-black font-bold text-3xl mb-1 whitespace-nowrap">
                                {chatroom?.name ? (
                                    <p>{chatroom.name}</p>
                                ) : (
                                    chatroom?.users
                                        .filter((username: any) => username !== user?.username) // 현재 사용자 제외
                                        .map((username: any, index: number, array: []) => (
                                            <span key={username}>
                                                {username}
                                                {index < array.length - 1 && ", "}
                                            </span>
                                        ))
                                )}
                            </p>
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
                <div className="official-color w-[59%] h-[70px] rounded-md flex items-center fixed z-50 absolute" style={{ opacity: 0.8 }}>
                    <img src="/noti.png" className="w-[60px] h-[60px] mr-2" alt="" />
                    <p className="w-full text-white" style={{ opacity: 1 }}>
                        {Chatroom?.notification?.message || '공지가 안 뜹니다'}
                    </p>
                    <button className="h-full flex items-start mr-2 text-white text-3xl" style={{ opacity: 1 }}>
                        ⨯
                    </button>
                </div>
            </div>

            <div className="h-[650px] w-[100%] overflow-x-hidden overflow-y-scroll">
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
                                            {t?.message}
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
                                            {t?.message}
                                        </p>
                                        <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">{getChatDateTimeFormat(t?.sendTime)}</p>
                                        <p className="text-sm text-gray-300 ml-3 mt-5 whitespace-nowrap">읽음</p>
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

                        onKeyDown={e => {
                            if (e.key === "Enter" && !e.shiftKey) { // Shift + Enter를 누를 경우는 줄바꿈
                                e.preventDefault(); // 폼 제출 방지
                                if (isReady) {
                                    socket.publish({
                                        destination: "/api/pub/message/" + chatroom?.id,
                                        body: JSON.stringify({ username: user?.username, message: message, messageType: 0 })
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

                        <button id="file">
                            <img src="/file.png" data-tip="파일" className="file w-[25px] h-[25px] items-center justify-center m-1" />
                        </button>
                        <Tooltip anchorSelect="#file" clickable>
                            <button>파일 전송</button>
                        </Tooltip>

                    </div>
                    <button id="sendMessage">
                        <img src="/send.png" className="send w-[40px] h-[40px] items-center justify-center m-1" onClick={() => {
                            if (isReady)
                                socket.publish({
                                    destination: "/api/pub/message/" + chatroom?.id,
                                    body: JSON.stringify({ username: user?.username, message: message, messageType: 0 })
                                });
                        }} />
                    </button>
                </div>
            </div>
        </div >
    }

    return <Main user={user}>
        <div className="w-4/12 flex items-center justify-center h-screen">
            {/* 왼 쪽 부분 */}
            <div className=" h-11/12 w-11/12 mt-10 bg-white h-[95%] shadow">
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
                        <input type="text" placeholder="대화방, 참여자 검색" className="bolder-0 outline-none bg-white text-black w-[80%] " />
                        <button className="text-gray-300 whitespace-nowrap">
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
                    <div className="w-full justify-end h-[590px] overflow-x-hidden overflow-y-scroll">
                        {chatrooms?.map((chatroom: chatroomResponseDTO, index: number) => <ChatList key={index} Chatroom={chatroom} ChatDetail={chatDetail} />)}
                    </div>
                </div>
            </div>
        </div>

        {/* 오른쪽 부분 */}
        <div className="w-8/12 flex items-center justify-center">
            <div className="h-11/12 w-11/12 mt-10 bg-white h-[95%] flex flex-col shadow">
                {chatroom != null ? <ChatDetil Chatroom={chatroom} chatDetail={chatDetail} /> : <></>}
            </div>
        </div>
    </Main>
}

