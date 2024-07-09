package com.team.HoneyBadger.Service;

import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Enum.MessageType;
import com.team.HoneyBadger.Exception.DataDuplicateException;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.HoneyBadgerApplication;
import com.team.HoneyBadger.Security.CustomUserDetails;
import com.team.HoneyBadger.Security.JWT.JwtTokenProvider;
import com.team.HoneyBadger.Service.Module.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MultiService {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ParticipantService participantService;
    private final ChatroomService chatroomService;
    private final EmailService emailService;
    private final EmailReceiverService emailReceiverService;
    private final MessageService messageService;
    private final MessageReservationService messageReservationService;

    /**
     * Auth
     */

    public TokenDTO checkToken(String accessToken) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        String username = null;
        String error_message = null;
        if (accessToken != null && accessToken.length() > 7) {
            String token = accessToken.substring(7);
            if (this.jwtTokenProvider.validateToken(token)) {
                httpStatus = HttpStatus.OK;
                username = this.jwtTokenProvider.getUsernameFromToken(token);
            } else {
                httpStatus = HttpStatus.UNAUTHORIZED;
                error_message = "refresh";
            }
        } else error_message = "not login";


        return TokenDTO.builder().httpStatus(httpStatus).username(username).error_message(error_message).build();
    }

    @Transactional
    public String refreshToken(String refreshToken) {
        if (this.jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            SiteUser user = userService.get(username);
            if (user != null) {
                return this.jwtTokenProvider.generateAccessToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
            }
        }
        return null;
    }

    @Transactional
    public AuthResponseDTO login(AuthRequestDTO requestDto) {
        SiteUser user = this.userService.get(requestDto.username());
        if (user == null) {
            throw new IllegalArgumentException("username");
        }
        if (!this.userService.isMatch(requestDto.password(), user.getPassword()))
            throw new IllegalArgumentException("password");
        String accessToken = this.jwtTokenProvider //
                .generateAccessToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
        String refreshToken = this.jwtTokenProvider //
                .generateRefreshToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
        return AuthResponseDTO.builder().tokenType("Bearer").accessToken(accessToken).refreshToken(refreshToken).build();
    }

    /*
     * User
     */
    @Transactional
    public void signup(SignupRequestDTO signupRequestDTO) throws DataDuplicateException {
        userService.save(signupRequestDTO);
    }

    public UserResponseDTO getProfile(String username) {
        SiteUser user = userService.get(username);
        return getUserResponseDTo(user);
    }

    private UserResponseDTO getUserResponseDTo(SiteUser user) {
        return UserResponseDTO.builder() //
                .role(user.getRole().ordinal())//
                .createDate(dateTimeTransfer(user.getCreateDate()))//
                .modifyDate(dateTimeTransfer(user.getModifyDate()))//
                .phoneNumber(user.getPhoneNumber())//
                .username(user.getUsername())//
                .name(user.getName()) //
                .url(null) //
                .build();
    }

    /*
     * ChatRoom
     */

    @Transactional
    public ChatroomResponseDTO getChatRoomType(ChatroomRequestDTO chatroomRequestDTO) {
        ChatroomResponseDTO chatroomResponseDTO;
        int userCount = chatroomRequestDTO.users().size();
        // 1:1 채팅 처리
        if (userCount == 2) {
            chatroomResponseDTO = this.existence(chatroomRequestDTO); // 기존 채팅방 확인
            if (chatroomResponseDTO == null) { // 기존 채팅방이 없으면 새로 생성
                chatroomResponseDTO = createChatroom(chatroomRequestDTO);
            }
        } else if (userCount >= 3) { // 단체 채팅방 처리
            chatroomResponseDTO = createChatroom(chatroomRequestDTO);
        } else {
            return null;
        }
        return chatroomResponseDTO;
    }

    @Transactional
    public ChatroomResponseDTO existence(ChatroomRequestDTO chatroomRequestDTO) {
        List<Participant> participantList = participantService.getAll();

        // 모든 참가자들을 채팅방 ID 별로 그룹화
        Map<Long, List<Participant>> chatrooms = participantList.stream().collect(Collectors.groupingBy(p -> p.getChatroom().getId()));

        for (Map.Entry<Long, List<Participant>> entry : chatrooms.entrySet()) {
            List<Participant> chatroomParticipants = entry.getValue();

            // 각 채팅방이 정확히 두 명의 참가자를 가지고 있는지 확인
            if (chatroomParticipants.size() == 2) {
                List<String> chatroomUsernames = chatroomParticipants.stream().map(p -> p.getUser().getUsername()).collect(Collectors.toList());

                // 요청된 사용자 목록과 동일여부
                if (new HashSet<>(chatroomRequestDTO.users()).containsAll(chatroomUsernames)) {
                    Chatroom chatroom = chatroomParticipants.get(0).getChatroom();

                    // 채팅방이 존재할 경우 ChatroomResponseDTO 생성하여 반환
                    return ChatroomResponseDTO.builder().id(chatroom.getId()).name(chatroom.getName()).users(chatroomUsernames).build();
                }
            }
        }

        // 채팅방이 존재하지 않을 경우 null 반환
        return null;
    }

    @Transactional
    public ChatroomResponseDTO createChatroom(ChatroomRequestDTO chatroomRequestDTO) {
        // Chatroom 생성
        Chatroom chatroom = chatroomService.create(chatroomRequestDTO.name());
        // Participant 생성 및 저장
        for (String username : chatroomRequestDTO.users()) {
            SiteUser user = userService.get(username);
            participantService.save(user, chatroom);
        }

        return getChatRoom(chatroom);
    }

    @Transactional
    public ChatroomResponseDTO getChatRoom(Long chatroomId) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        return getChatRoom(chatroom);
    }

    @Transactional
    public List<ChatroomResponseDTO> getChatRoomListByUser(String username) {
        SiteUser siteUser = userService.get(username);
        List<Chatroom> chatroomList = chatroomService.getChatRoomListByUser(siteUser);
        List<ChatroomResponseDTO> chatroomResponseDTOList = new ArrayList<>();
        for(Chatroom chatroom : chatroomList){
            chatroomResponseDTOList.add(getChatRoom(chatroom));
        }
        return chatroomResponseDTOList;
    }

    @Transactional
    private ChatroomResponseDTO getChatRoom(Chatroom chatroom) {
        List<String> users = chatroom.getParticipants().stream().map(participant -> participant.getUser().getUsername()).toList();
        List<MessageResponseDTO> messageResponseDTOList = messageService.getMessageList(chatroom.getMessageList());
        return ChatroomResponseDTO.builder().id(chatroom.getId()).name(chatroom.getName()).users(users).messageResponseDTOList(messageResponseDTOList).build();
    }

    @Transactional
    public void deleteChatroom(Long chatroomId) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        chatroomService.delete(chatroom);
    }

    @Transactional
    public ChatroomResponseDTO updateChatroom(Long chatroomId, ChatroomRequestDTO chatroomRequestDTO) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        chatroom = chatroomService.updateChatroom(chatroom, chatroomRequestDTO.name());
        return getChatRoom(chatroom);
    }

    @Transactional
    public ChatroomResponseDTO plusParticipant(ParticipantRequestDTO participantRequestDTO) {
        Chatroom chatroom = chatroomService.getChatRoomById(participantRequestDTO.chatroomId());
        SiteUser siteUser = userService.get(participantRequestDTO.username());
        participantService.save(siteUser, chatroom);
        return getChatRoom(chatroom);
    }

    @Transactional
    public ChatroomResponseDTO minusParticipant(ParticipantRequestDTO participantRequestDTO) {
        Chatroom chatroom = chatroomService.getChatRoomById(participantRequestDTO.chatroomId());
        SiteUser siteUser = userService.get(participantRequestDTO.username());
        Participant participant = participantService.get(siteUser, chatroom);
        chatroom.getParticipants().remove(participant);
        participantService.delete(participant);

        chatroomService.save(chatroom);
        return getChatRoom(chatroom);
    }

    /*
     * Email
     */
    public Email sendEmail(String title, String content, String senderId, List<String> receiverIds) {
        SiteUser sender = userService.get(senderId);
        Email email = emailService.save(title, content, sender);

        for (String receiverId : receiverIds) {
            SiteUser receiver = userService.get(receiverId);
            emailReceiverService.save(email, receiver);
        }
        return email;
    }

    public List<EmailResponseDTO> getEmailsForUser(String username) {
        List<EmailResponseDTO> list = new ArrayList<>();
        List<Email> emails = emailReceiverService.getEmailsForUser(username);
        for (Email email : emails) {
            List<String> receivers = new ArrayList<>();
            for (EmailReceiver receiver : email.getReceiverList())
                receivers.add(receiver.getReceiver().getUsername());
            list.add(EmailResponseDTO.builder()//
                    .id(email.getId()) //
                    .title(email.getTitle()) //
                    .content(email.getContent()) //
                    .senderId(email  //
                            .getSender() //
                            .getUsername()) //
                    .senderName(email //
                            .getSender() //
                            .getUsername()) //
                    .receiverIds(receivers) //
                    .build());
        }
        return list;
    }

    private EmailResponseDTO GetEmail(Email email) {
        return EmailResponseDTO.builder().id(email.getId()).title(email.getTitle()).senderName(email.getSender().getName()).build();
    }

    public void markEmailAsRead(Long emailId, String receiverId) {
        emailReceiverService.markEmailAsRead(emailId, receiverId);
    }

    /*
     * Message or Chat
     */

    public MessageResponseDTO sendMessage(Long id, MessageRequestDTO messageRequestDTO, String username) {
        Chatroom chatroom = chatroomService.getChatRoomById(id);

        SiteUser siteUser = userService.get(username);
        MessageType messageType;

    private MessageType getMessageType(int MessageTypeInt) {
        MessageType messageType;
        switch (MessageTypeInt) {
            case 0:
                messageType = MessageType.TEXT;
                return messageType;
            case 1:
                messageType = MessageType.IMAGE;
                return messageType;
            case 2:
                messageType = MessageType.LINK;
                return messageType;
            case 3:
                messageType = MessageType.FILE;
                return messageType;
            default:
                throw new IllegalArgumentException("Unknown message type: " + MessageTypeInt);
        }
    }

    public MessageResponseDTO sendMessage(Long id, MessageRequestDTO messageRequestDTO, String username) {
        Chatroom chatroom = chatroomService.getChatRoomById(id);
        SiteUser siteUser = userService.get(username);
        MessageType messageType = this.getMessageType(messageRequestDTO.messageType());

        Message message = Message.builder()
                .message(messageRequestDTO.message())
                .sender(siteUser)
                .chatroom(chatroom)
                .messageType(messageType)
                .build();


        return GetMessage(messageService.save(message));
    }

    private MessageResponseDTO GetMessage(Message message) {
        return MessageResponseDTO.builder().id(message.getId()).sendTime(message.getCreateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).username(message.getSender().getUsername()).message(message.getMessage()).build();
    }

    public void deleteMessage(Long messageId) {
        Message message = messageService.getMessageById(messageId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime send = message.getCreateDate();

        // 메시지의 createDate가 now 기준으로 5분 이내인지 확인
        if (Duration.between(send, now).toMinutes() <= 5) {
            // 메시지를 삭제하는 로직을 추가합니다.
            messageService.deleteMessage(message);

            // 삭제된 메시지에 대한 응답을 생성합니다.
            System.out.println("Message deleted");
            // throw new RuntimeException("Message deleted");
        } else {
            // 메시지가 5분을 초과했을 때의 로직을 추가합니다.
            System.out.println("Cannot delete message older than 5 minutes");
            // throw new RuntimeException("Cannot delete message older than 5 minutes");
        }
    }

    public String fileUpload(Long roomId, MultipartFile file) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        UUID uuid = UUID.randomUUID();
        String fileName = "/chatroom/" + roomId.toString() + "/" + uuid.toString() + ".";// IMAGE
        switch (file.getContentType().split("/")[0]) {
            case "image" -> fileName += file.getContentType().split("/")[1];
            case "text" -> fileName += "txt";
            case "application" -> {
                String value = file.getContentType().split("/")[1];
                if (value.contains("presentation") && value.contains("12")) fileName += "pptm";
                else if (value.equals("zip"))
                    fileName += "zip";
                else if (value.contains("spreadsheetml"))
                    fileName += "xlsx";
                else {
                    throw new DataNotFoundException("not support");
                }
            }
            default -> {
                throw new DataNotFoundException("not support");
            }
        }
        File dest = new File(path + fileName);

        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);

        return fileName;
    }

    /*
     * MessageReservation or ChatReservation
     */
    public MessageReservationResponseDTO reservationMessage(MessageReservationRequestDTO messageReservationRequestDTO, String username) {
        Chatroom chatroom = chatroomService.getChatRoomById(messageReservationRequestDTO.chatroomId());
        SiteUser sender = userService.get(username);
        MessageReservation messageReservation = MessageReservation.builder()
                .chatroom(chatroom)
                .message(messageReservationRequestDTO.message())
                .sender(sender)
                .sendDate(messageReservationRequestDTO.sendTime())
                .messageType(getMessageType(messageReservationRequestDTO.messageType()))
                .build();
    /*
     * Time
     */
    private Long dateTimeTransfer(LocalDateTime dateTime) {
        return dateTime == null ? 0 : dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
        return getMessageReservation(messageReservation);
    }

    private MessageReservationResponseDTO getMessageReservation(MessageReservation messageReservation) {
        return MessageReservationResponseDTO.builder()
                .id(messageReservation.getId())
                .chatroomId(messageReservation.getChatroom().getId())
                .message(messageReservation.getMessage())
                .username(messageReservation.getSender().getUsername())
                .sendTime(messageReservation.getSendDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }
}
