package com.team.HoneyBadger.Service;

import com.team.HoneyBadger.Config.Exception.DataDuplicateException;
import com.team.HoneyBadger.Config.Exception.UnauthorizedException;
import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Enum.EmailStatus;
import com.team.HoneyBadger.Enum.KeyPreset;
import com.team.HoneyBadger.Enum.MessageType;
import com.team.HoneyBadger.HoneyBadgerApplication;
import com.team.HoneyBadger.Security.CustomUserDetails;
import com.team.HoneyBadger.Security.JWT.JwtTokenProvider;
import com.team.HoneyBadger.Service.Module.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private final EmailReservationService emailReservationService;
    private final FileSystemService fileSystemService;
    private final MultiKeyService multiKeyService;

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

        if (!this.userService.isMatch(requestDto.password(), user.getPassword())) {
            throw new IllegalArgumentException("password");
        }
        String accessToken = this.jwtTokenProvider.generateAccessToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
        String refreshToken = this.jwtTokenProvider.generateRefreshToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
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
        for (Chatroom chatroom : chatroomList) {
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
    public void emailFilesUpload(Long email_id, List<MultipartFile> files) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        String keyValue = KeyPreset.EMAIL_MULTI.getValue(email_id.toString());
        MultiKey key = multiKeyService.get(keyValue).orElseGet(() -> multiKeyService.save(keyValue));
        List<String> list = new ArrayList<>();
        for (MultipartFile file : files) {
            UUID uuid = UUID.randomUUID();
            String fileName = "/api/email/" + email_id.toString() + "/" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE

            String fileKey = KeyPreset.EMAIL.getValue(email_id.toString() + "_" + list.size());
            fileSystemService.save(fileKey, fileName);
            fileSystemService.save(KeyPreset.EMAIL_ORIGIN.getValue(fileKey), file.getOriginalFilename());
            list.add(fileKey);

            File dest = new File(path + fileName);
            if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
            file.transferTo(dest);
        }
        multiKeyService.updateAll(key, list);
    }

    public String emailContentUpload(String username, MultipartFile file) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        UUID uuid = UUID.randomUUID();
        String fileName = "/api/user/" + username + "/temp/" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE

        // 멀티키
        String keyString = KeyPreset.USER_TEMP_MULTI.getValue(username);
        Optional<MultiKey> _key = multiKeyService.get(keyString);
        MultiKey key = _key.orElseGet(() -> multiKeyService.save(keyString));

        // 파일 저장
        FileSystem fileSystem = fileSystemService.save(KeyPreset.USER_TEMP.getValue(key.getKeyValues().size() + ""), fileName);
        multiKeyService.updateOne(key, fileSystem.getK());

        File dest = new File(path + fileName);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);
        return fileName;
    }

    public EmailResponseDTO sendEmail(String title, String content, String senderId, List<String> receiverIds) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        SiteUser sender = userService.get(senderId);
        Email email = emailService.save(title, sender);
        emailService.update(email, content.replaceAll("/user/" + sender.getUsername(), "/email/" + email.getId().toString()));
        Optional<MultiKey> _key = multiKeyService.get(KeyPreset.USER_TEMP_MULTI.getValue(senderId));
        if (_key.isPresent()) {
            MultiKey key = _key.get();
            for (String k : key.getKeyValues()) {
                Optional<FileSystem> _fileSystem = fileSystemService.get(k);
                if (_fileSystem.isPresent()) {
                    FileSystem fileSystem = _fileSystem.get();
                    String value = path + fileSystem.getV();
                    Path prePath = Paths.get(value);
                    Path newPath = Paths.get(value.replaceAll("/user/" + sender.getUsername(), "/email/" + email.getId().toString()));
                    Files.move(prePath, newPath, StandardCopyOption.REPLACE_EXISTING);
                    fileSystemService.deleteByKey(fileSystem);
                }
            }
            multiKeyService.delete(key);
        }
        for (String receiverId : receiverIds) {
            SiteUser receiver = userService.get(receiverId);
            emailReceiverService.save(email, receiver);
        }
        return getEmailDTO(email);
    }

    public List<EmailResponseDTO> getEmailsForUser(String username, EmailStatus status) {
        List<Email> emails = switch (status) {
            case SENDER -> emailReceiverService.getEmailsForUser(username); //구분 해야 함
            case RECEIVER -> emailReceiverService.getEmailsForUser(username); //구분 해야 함
            case RESERVATION -> emailReceiverService.getEmailsForUser(username); //구분 해야 함
        };


        return emails.stream().map(this::getEmailDTO).toList();
    }

    public Boolean markEmailAsRead(Long emailId, String receiverId) {
        Boolean isRead = emailReceiverService.markEmailAsRead(emailId, receiverId);
        return isRead;
    }

    @Transactional
    public void deleteEmail(Long emailId, String username) {
        Email email = emailService.getEmail(emailId);
        emailService.findByUsernameDelete(email, username);
    }

    private EmailResponseDTO getEmailDTO(Email email) {
        List<FileResponseDTO> filePathList = new ArrayList<>();
        Optional<MultiKey> _multiKey = multiKeyService.get(KeyPreset.EMAIL_MULTI.getValue(email.getId().toString()));
        if (_multiKey.isPresent()) //
            for (String key : _multiKey.get().getKeyValues()) {
                FileResponseDTO.FileResponseDTOBuilder builder = FileResponseDTO.builder();
                fileSystemService.get(key).ifPresent(fileSystem -> builder.value(fileSystem.getV())); // url
                fileSystemService.get(KeyPreset.EMAIL_ORIGIN.getValue(key)).ifPresent(fileSystem -> builder.original_name(fileSystem.getV())); // original Name
                builder.key(key); // key
                filePathList.add(builder.build());
            }

        return EmailResponseDTO //
                .builder() //
                .id(email.getId()) //
                .title(email.getTitle()) //
                .content(email.getContent()) //
                .senderId(email.getSender().getUsername()) //
                .senderName(email.getSender().getUsername()) //
                .receiverIds(email.getReceiverList() //
                        .stream() //
                        .map(er -> er.getReceiver().getUsername()) //
                        .toList()) //
                .filePathList(filePathList) //
                .build();
    }


    public EmailResponseDTO getEmailDTO(Long emailId) {
        Email email = emailService.getEmail(emailId);
        return getEmailDTO(email);
    }

    public String fileUpload(Long roomId, MultipartFile file) throws IOException {

        String path = HoneyBadgerApplication.getOsType().getLoc();
        UUID uuid = UUID.randomUUID();
        String fileName = "/api/chatroom/" + roomId.toString() + "/" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE

        // 너굴맨이 해치우고 갔어요!
        File dest = new File(path + fileName);

        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);

        return fileName;
    }

    /*
     * Email Reservation
     */
    public void emailReservationFilesUpload(Long email_id, List<MultipartFile> files) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        String keyValue = KeyPreset.EMAIL_RESERVATION_MULTI.getValue(email_id.toString());
        MultiKey key = multiKeyService.get(keyValue).orElseGet(() -> multiKeyService.save(keyValue));
        List<String> list = key.getKeyValues();
        for (MultipartFile file : files) {
            UUID uuid = UUID.randomUUID();
            String fileName = "/api/user/email/" + email_id.toString() + "/" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE
            String fileKey = KeyPreset.EMAIL_RESERVATION.getValue(email_id.toString() + "_" + list.size());
            fileSystemService.save(fileKey, fileName);
            fileSystemService.save(KeyPreset.EMAIL_RESERVATION_ORIGIN.getValue(fileKey), file.getOriginalFilename());
            list.add(fileKey);
            File dest = new File(path + fileName);
            if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
            file.transferTo(dest);
        }
        multiKeyService.updateAll(key, list);
    }

    public void deleteEmailReservation(Long reservationId, String username) {
        EmailReservation emailReservation = emailReservationService.getEmailReservation(reservationId);
        emailReservationService.findByUsernameDelete(emailReservation, username);
    }

    public EmailReservationResponseDTO reservationEmail(EmailReservationRequestDTO emailReservationRequestDTO, String username) {
        SiteUser sender = userService.get(username);
        EmailReservation emailReservation = emailReservationService.save(emailReservationRequestDTO, sender);
        return getEmailReservationDTO(emailReservation);
    }

    private EmailReservationResponseDTO getEmailReservationDTO(EmailReservation reservation) {
        List<FileResponseDTO> fileslist = new ArrayList<>();
        Optional<MultiKey> _multiKey = multiKeyService.get(KeyPreset.EMAIL_RESERVATION_MULTI.getValue(reservation.getId().toString()));
        if (_multiKey.isPresent()) for (String key : _multiKey.get().getKeyValues()) {
            FileResponseDTO.FileResponseDTOBuilder builder = FileResponseDTO.builder();
            fileSystemService.get(key).ifPresent(file -> builder.value(file.getV()));
            fileSystemService.get(KeyPreset.EMAIL_RESERVATION_ORIGIN.getValue(key)).ifPresent(file -> builder.original_name(file.getV()));
            builder.key(key);
            fileslist.add(builder.build());
        }
        return EmailReservationResponseDTO.builder().title(reservation.getTitle()).content(reservation.getContent()).id(reservation.getId()).receiverIds(reservation.getReceiverList()).senderTime(this.dateTimeTransfer(reservation.getSendTime())).files(fileslist).build();
    }

    public EmailReservationResponseDTO updateEmailReservation(EmailReservationRequestDTO emailReservationRequestDTO, String username) {
        // 요청 DTO에서 ID를 사용하여 이메일 예약을 검색
        EmailReservation emailReservation = emailReservationService.getEmailReservation(emailReservationRequestDTO.id());
        // 예약의 발신자, 현재 사용자 username 일치 확인
        if (emailReservation != null && emailReservation.getSender().getUsername().equals(username)) {
            String keyValue = KeyPreset.EMAIL_RESERVATION_MULTI.getValue(emailReservationRequestDTO.id().toString());
            MultiKey multiKey = multiKeyService.get(keyValue).orElseGet(() -> multiKeyService.save(keyValue));
            List<String> values = new ArrayList<>();
            for (String key : emailReservationRequestDTO.files()) {
                Optional<FileSystem> _fileSystem = fileSystemService.get(key);
                _fileSystem.ifPresent(fileSystem -> values.add(fileSystem.getV()));
            }
            for (String key : multiKey.getKeyValues()) {
                Optional<FileSystem> _fileSystem = fileSystemService.get(key);
                _fileSystem.ifPresent(fileSystemService::deleteByKey);
            }
            multiKeyService.updateAll(multiKey, values);
            emailReservationService.update(emailReservation, emailReservationRequestDTO);
            return getEmailReservationDTO(emailReservation);
        } else {
            throw new UnauthorizedException("You are not authorized to update this reservation.");
        }
    }

    /*
     * Message or Chat
     */

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


    public MessageResponseDTO sendMessage(Long id, MessageRequestDTO messageRequestDTO) {
        Chatroom chatroom = chatroomService.getChatRoomById(id);
        SiteUser siteUser = userService.get(messageRequestDTO.username());
        MessageType messageType = this.getMessageType(messageRequestDTO.messageType());


        Message message = Message.builder().message(messageRequestDTO.message()).sender(siteUser).chatroom(chatroom).messageType(messageType).build();


        return GetMessage(messageService.save(message));
    }

    private MessageResponseDTO GetMessage(Message message) {
        Long sendTime = this.dateTimeTransfer(message.getCreateDate());

        return MessageResponseDTO.builder().id(message.getId()).sendTime(sendTime).username(message.getSender().getUsername()).name(message.getSender().getName()).message(message.getMessage()).messageType(message.getMessageType()).build();
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




    /*
     * Time
     */

    private Long dateTimeTransfer(LocalDateTime dateTime) {
        return dateTime == null ? 0 : dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /*
     * MessageReservation or ChatReservation
     */

    public MessageReservationResponseDTO reservationMessage(MessageReservationRequestDTO messageReservationRequestDTO, String username) {
        Chatroom chatroom = chatroomService.getChatRoomById(messageReservationRequestDTO.chatroomId());
        SiteUser sender = userService.get(username);
        MessageReservation messageReservation = MessageReservation.builder().chatroom(chatroom).message(messageReservationRequestDTO.message()).sender(sender).sendDate(messageReservationRequestDTO.sendTime()).messageType(messageReservationRequestDTO.messageType()).build();

        messageReservationService.save(messageReservation);
        return getMessageReservation(messageReservation);
    }

    private MessageReservationResponseDTO getMessageReservation(MessageReservation messageReservation) {
        Long sendTime = this.dateTimeTransfer(messageReservation.getSendDate());
        return MessageReservationResponseDTO.builder().id(messageReservation.getId()).chatroomId(messageReservation.getChatroom().getId()).message(messageReservation.getMessage()).username(messageReservation.getSender().getUsername()).sendDate(sendTime).messageType(messageReservation.getMessageType()).build();
    }


    public void deleteReservationMessage(Long reservationMessageId) {
        MessageReservation messageReservation = messageReservationService.getMessageReservation(reservationMessageId);
        messageReservationService.delete(messageReservation);
    }

    public MessageReservationResponseDTO updateReservationMessage(Long reservationMessageId, MessageReservationRequestDTO messageReservationRequestDTO, String username) {
        MessageReservation messageReservation = messageReservationService.getMessageReservation(reservationMessageId);
        if (messageReservation.getSender().getUsername().equals(username)) {
            messageReservationService.update(messageReservation, messageReservationRequestDTO.message(), messageReservation.getSendDate());
        }
        return getMessageReservation(messageReservation);
    }
}