package com.team.HoneyBadger.Service;

import com.team.HoneyBadger.Exception.*;
import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Entity.*;
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
    private final LastReadMessageService lastReadMessageService;


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
        return getUserResponseDTO(user);
    }

    private UserResponseDTO getUserResponseDTO(SiteUser user) {
        Optional<FileSystem> _fileSystem = fileSystemService.get(KeyPreset.USER_PROFILE.getValue(user.getUsername()));
        return UserResponseDTO.builder() //
                .role(user.getRole().ordinal())//
                .createDate(dateTimeTransfer(user.getCreateDate()))//
                .joinDate(dateTimeTransfer(user.getJoinDate()))//
                .phoneNumber(user.getPhoneNumber())//
                .username(user.getUsername())//
                .name(user.getName()) //
                .url(_fileSystem.map(FileSystem::getV).orElse(null)) //
                .department(getDepartmentDTO(user.getDepartment())) //
                .build();
    }
    public UserResponseDTO updateProfile(String username, MultipartFile file) throws IOException {
        if (file == null || !file.getContentType().contains("image")) throw new InvalidFileTypeException("not image");
        String key = KeyPreset.USER_PROFILE.getValue(username);
        String path = HoneyBadgerApplication.getOsType().getLoc();
        Optional<FileSystem> _fileSystem = fileSystemService.get(key);
        if (_fileSystem.isPresent()) {
            FileSystem fileSystem = _fileSystem.get();
            File preFile = new File(path + fileSystem.getV());
            if (preFile.exists()) deleteFileWithFolder(preFile);
        }
        UUID uuid = UUID.randomUUID();
        String fileName = "/api/user/" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE
        fileSystemService.save(key, fileName);
        File dest = new File(path + fileName);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);
        SiteUser user = userService.get(username);
        return getUserResponseDTO(user);
    }

    public UserResponseDTO deleteProfile(String username) {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        Optional<FileSystem> _fileSystem = fileSystemService.get(KeyPreset.USER_PROFILE.getValue(username));
        if (_fileSystem.isPresent()) {
            FileSystem fileSystem = _fileSystem.get();
            File file = new File(path + fileSystem.getV());
            if (file.exists()) deleteFileWithFolder(file);
            fileSystemService.deleteByKey(fileSystem);
        }
        SiteUser user = userService.get(username);
        return getUserResponseDTO(user);
    }

    public UserResponseDTO getUser(String username) {
        return getUserResponseDTO(userService.get(username));
    }

    public List<UserResponseDTO> getAllUser(String username) {
        return userService.getUsernameAll(username).stream().map(this::getUserResponseDTO).toList();
    }

    public void changePassword(String username, PasswordChangeDTO passwordChangeDTO) {
        SiteUser user = userService.get(username);
        System.out.printf(passwordChangeDTO.prePassword() + " / " + passwordChangeDTO.newPassword() + " / " + user.getPassword());
        if (!userService.isMatch(passwordChangeDTO.prePassword(), user.getPassword()))
            throw new DataNotSameException("password");
        userService.update(user, passwordChangeDTO.newPassword());
    }

    /*
     * Department
     */
    private DepartmentResponseDTO getDepartmentDTO(Department department) {
        if (department == null) return null;
        return DepartmentResponseDTO.builder().name(department.getName()).parent(appendParent(department.getParent())).build();
    }

    private DepartmentResponseDTO appendParent(Department now) {
        if (now.getParent() == null) return DepartmentResponseDTO.builder().name(now.getName()).build();
        else return DepartmentResponseDTO.builder().name(now.getName()).parent(appendParent(now.getParent())).build();
    }

    /*
     * ChatRoom
     */

    @Transactional
    public ChatroomResponseDTO getChatRoomType(ChatroomRequestDTO chatroomRequestDTO, String loginUser) {
        ChatroomResponseDTO chatroomResponseDTO;
        int userCount = chatroomRequestDTO.users().size();
        // 1:1 채팅 처리
        if (userCount == 2) {
            chatroomResponseDTO = this.existence(chatroomRequestDTO); // 기존 채팅방 확인
            if (chatroomResponseDTO == null) { // 기존 채팅방이 없으면 새로 생성
                chatroomResponseDTO = createChatroom(chatroomRequestDTO, loginUser);
            }
        } else if (userCount >= 3) { // 단체 채팅방 처리
            chatroomResponseDTO = createChatroom(chatroomRequestDTO, loginUser);
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
    public ChatroomResponseDTO createChatroom(ChatroomRequestDTO chatroomRequestDTO, String loginUser) {
        // Chatroom 생성
        Chatroom chatroom = chatroomService.create(chatroomRequestDTO.name());
        // Participant 생성 및 저장
        for (String username : chatroomRequestDTO.users()) {
            SiteUser user = userService.get(username);
            participantService.save(user, chatroom);
        }

        return getChatRoom(chatroom, loginUser);
    }

    @Transactional
    public List<ChatroomResponseDTO> getChatRoomListByUser(String username, String keyword) {
        SiteUser siteUser = userService.get(username);
        List<Chatroom> chatroomList = chatroomService.getChatRoomListByUser(siteUser, keyword);
        List<ChatroomResponseDTO> chatroomResponseDTOList = new ArrayList<>();
        for (Chatroom chatroom : chatroomList) {
            chatroomResponseDTOList.add(getChatRoom(chatroom, username));
        }
        return chatroomResponseDTOList;
    }

    @Transactional
    public List<MessageResponseDTO> getMessageList(Long chatroomId) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        return messageService.getMessageList(chatroom.getMessageList());
    }

    @Transactional
    public List<MessageResponseDTO> updateMessageList(String username, Long chatroomId) {
        SiteUser user = userService.get(username);
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        LastReadMessage lastReadMessage = lastReadMessageService.get(user, chatroom);

        List<Message> updatedMessages;
        if (lastReadMessage != null) { // 특정 채팅방에서 유저의 startId 이후의 업데이트된 메시지 목록을 가져옴
            updatedMessages = messageService.getUpdatedList(chatroomId, lastReadMessage.getLastReadMessage());
        }else { // 특정 채팅방의 메세지 목록을 가져옴
            updatedMessages = chatroom.getMessageList();
        }

        Message lastMessage = updatedMessages.get(updatedMessages.size() - 1);
        Long lastMessageId = lastMessage.getId();

        this.saveLastMessage(user, chatroom, lastMessageId);

        return updatedMessages.stream().map(this::GetMessageDTO).toList();
    }

    private void saveLastMessage(SiteUser user, Chatroom chatroom, Long lastReadMessageId){
        LastReadMessage lastReadMessage = lastReadMessageService.get(user, chatroom);
        if (lastReadMessage == null){
            lastReadMessageService.create(user, chatroom, lastReadMessageId);
        }else {
            lastReadMessageService.updateMessage(lastReadMessage, lastReadMessageId);
        }
    }

    @Transactional
    private ChatroomResponseDTO getChatRoom(Chatroom chatroom, String username) {
        List<String> users = chatroom.getParticipants().stream().map(participant -> participant.getUser().getUsername()).toList();
        Message latestMessage = messageService.getLatesMessage(chatroom.getMessageList());

        //마지막 메세지
        MessageResponseDTO latestMessageDTO;
        if (latestMessage != null) {
            latestMessageDTO = GetMessageDTO(latestMessage);
        } else {
            latestMessageDTO = null;
        }

        //공지
        MessageResponseDTO notificationDTO;
        if (chatroom.getNotification() != null) {
            notificationDTO = GetMessageDTO(chatroom.getNotification());
        } else {
            notificationDTO = null;
        }


        SiteUser user = userService.get(username);

        LastReadMessage lastReadMessage = lastReadMessageService.get(user, chatroom);
        int alarmcnt;
        if (lastReadMessage == null){
            if (!chatroom.getMessageList().isEmpty()){
                lastReadMessage = lastReadMessageService.create(user, chatroom, chatroom.getMessageList().get(0).getId());
                alarmcnt = alarmCount(chatroom.getId(), lastReadMessage.getLastReadMessage()) + 1;
            }else {
                alarmcnt = 0;
            }
        }else {
            alarmcnt = alarmCount(chatroom.getId(), lastReadMessage.getLastReadMessage());
        }


        return ChatroomResponseDTO.builder().id(chatroom.getId()).name(chatroom.getName()).users(users).latestMessage(latestMessageDTO).notification(notificationDTO).alarmCount(alarmcnt).build();
    }

    @Transactional
    public ChatroomResponseDTO getChatRoomById(Long chatroomId, String username) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        return getChatRoom(chatroom, username);
    }

    @Transactional
    public void deleteChatroom(Long chatroomId) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        chatroomService.delete(chatroom);
    }

    @Transactional
    public ChatroomResponseDTO updateChatroom(Long chatroomId, ChatroomRequestDTO chatroomRequestDTO, String username) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        chatroom = chatroomService.updateChatroom(chatroom, chatroomRequestDTO.name());
        return getChatRoom(chatroom, username);
    }

    @Transactional
    public ChatroomResponseDTO plusParticipant(Long chatroomId, String username, String loginUser) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        SiteUser siteUser = userService.get(username);
        participantService.save(siteUser, chatroom);
        return getChatRoom(chatroom, loginUser);
    }

    @Transactional
    public ChatroomResponseDTO minusParticipant(Long chatroomId, String username, String loginUser) {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        SiteUser siteUser = userService.get(username);
        Participant participant = participantService.get(siteUser, chatroom);
        chatroom.getParticipants().remove(participant);

        chatroomService.save(chatroom);
        return getChatRoom(chatroom, loginUser);
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

    public Long sendEmail(String title, String content, String senderId, List<String> receiverIds) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        SiteUser sender = userService.get(senderId);
        Email email = emailService.save(title, sender);
        if (content != null)
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
        return email.getId();
    }

    public Object getEmailsForUser(String username, int statusIndex) {
//        List<?> emails;
//        SENDER, RECEIVER, RESERVATION
        switch (statusIndex) {
            case 0:
                List<Email> SenderEmail = emailReceiverService.getSentEmailsForUser(username);
                return SenderEmail.stream().map(this::getEmailDTO).collect(Collectors.toList());
            case 1:
                List<Email> ReceiverEmail = emailReceiverService.getReceivedEmailsForUser(username);
                return ReceiverEmail.stream().map(this::getEmailDTO).collect(Collectors.toList());
            case 2:
                List<EmailReservation> ReservationEmail = emailReservationService.getReservedEmailsForUser(username);
                return ReservationEmail.stream().map(this::getEmailReservationDTO).collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Invalid status index: " + statusIndex);
        }
    }

//    public Boolean markEmailAsRead(EmailReadRequestDTO emailReadRequestDTO) {
//        Boolean isRead = emailReceiverService.markEmailAsRead(emailReadRequestDTO.emailId(), emailReadRequestDTO.receiverId());
//        Email email = emailService.getEmail(emailReadRequestDTO.emailId());
//        Optional<EmailReceiver> emailReceiver = emailReceiverService.getEmailReceiver(emailReadRequestDTO.emailId());
//        emailReceiver.stat
//        EmailResponseDTO emailResponseDTO = getEmailDTO(email);
//        emailResponseDTO.isRead()
//        return isRead;
//    }

    public EmailReceiverResponseDTO read(EmailReadRequestDTO emailReadRequestDTO) {
        Boolean isRead = emailReceiverService.markEmailAsRead(emailReadRequestDTO.emailId(), emailReadRequestDTO.receiverId());
        EmailResponseDTO emailResponseDTO = getEmailDTO(emailReadRequestDTO.emailId());
        EmailReceiverResponseDTO emailReceiverResponseDTO = EmailReceiverResponseDTO.builder()
                .id(emailResponseDTO.id())
                .status(isRead)
                .emailResponseDTO(emailResponseDTO)
                .build();
        return emailReceiverResponseDTO;
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
                .senderTime(this.dateTimeTransfer(email.getCreateDate())) //
                .files(filePathList) //
                .build();
    }

    public EmailResponseDTO getEmailDTO(Long emailId) {
        Email email = emailService.getEmail(emailId);
        return getEmailDTO(email);
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

    public Long reservationEmail(EmailReservationRequestDTO emailReservationRequestDTO, String username) {
        SiteUser sender = userService.get(username);
        EmailReservation emailReservation = emailReservationService.save(emailReservationRequestDTO, sender);
        return emailReservation.getId();
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
            // KeyPreset을 사용하여 keyValue 생성
            String keyValue = KeyPreset.EMAIL_RESERVATION_MULTI.getValue(emailReservationRequestDTO.id().toString());

            // multiKey를 가져오거나, 없으면 새로 생성
            MultiKey multiKey = multiKeyService.get(keyValue).orElseGet(() -> multiKeyService.save(keyValue));

            // 새로운 파일 경로 리스트 생성
            List<String> values = new ArrayList<>();
            for (String key : emailReservationRequestDTO.files()) {
                Optional<FileSystem> _fileSystem = fileSystemService.get(key);
                _fileSystem.ifPresent(fileSystem -> values.add(fileSystem.getV()));
            }

            // 기존 multiKey의 keyValues를 순회하며, 파일 시스템에서 삭제
            for (String key : multiKey.getKeyValues()) {
                Optional<FileSystem> _fileSystem = fileSystemService.get(key);
                _fileSystem.ifPresent(fileSystemService::deleteByKey);
            }

            // multiKey를 새로운 값들로 업데이트
            multiKeyService.updateAll(multiKey, values);

            // 이메일 예약 정보 업데이트
            emailReservationService.update(emailReservation, emailReservationRequestDTO);

            // 업데이트된 이메일 예약 정보를 바탕으로 DTO 반환
            return getEmailReservationDTO(emailReservation);
        } else {
            throw new UnauthorizedException("You are not authorized to update this reservation.");
        }
    }

    /*
     * Message or Chat
     */

    @Transactional
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

    @Transactional
    public MessageResponseDTO sendMessage(Long id, MessageRequestDTO messageRequestDTO) {
        Chatroom chatroom = chatroomService.getChatRoomById(id);
        SiteUser siteUser = userService.get(messageRequestDTO.username());
        MessageType messageType = this.getMessageType(messageRequestDTO.messageType());

        return GetMessageDTO(messageService.save(messageRequestDTO.message(), siteUser, chatroom, messageType));
    }

    @Transactional
    private MessageResponseDTO GetMessageDTO(Message message) {
        Long sendTime = this.dateTimeTransfer(message.getCreateDate());

        return MessageResponseDTO.builder().id(message.getId()).sendTime(sendTime).username(message.getSender().getUsername()).name(message.getSender().getName()).message(message.getMessage()).messageType(message.getMessageType().ordinal()).build();
    }

    @Transactional
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
//            throw new RuntimeException("Message deleted");
        } else {
            // 메시지가 5분을 초과했을 때의 로직을 추가합니다.
            System.out.println("Cannot delete message older than 5 minutes");
            throw new RuntimeException("Cannot delete message older than 5 minutes");
        }
    }

    @Transactional
    public String fileUpload(Long chatroomId, MultipartFile file) throws IOException {

        String path = HoneyBadgerApplication.getOsType().getLoc();
        UUID uuid = UUID.randomUUID();
        String fileName = "/api/chatroom/" + chatroomId.toString() + "/" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE

        // 너굴맨이 해치우고 갔어요!
        File dest = new File(path + fileName);

        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);

        return fileName;
    }

    @Transactional
    public String fileUpload(String username, MultipartFile file) throws IOException {

        String path = HoneyBadgerApplication.getOsType().getLoc();
        UUID uuid = UUID.randomUUID();
        String fileName = "/api/user/" + username + "/temp/" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE

        File dest = new File(path + fileName);

        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);

        return fileName;
    }

    @Transactional
    public void readMessage(Long chatroomId, String username) { //메세지 읽기 & 채팅방 접속
        SiteUser reader = userService.get(username);
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);

        LastReadMessage lastReadMessage = lastReadMessageService.get(reader, chatroom);
        Long startId = (lastReadMessage != null) ? lastReadMessage.getLastReadMessage() : null; //마지막 메세지가 있으면 startId, 없으면 null

        for (Message message : startId != null ? messageService.getList(startId) : chatroom.getMessageList()) { //읽음처리
            HashSet<String> sets = new HashSet<>(message.getReadUsers());
            sets.add(reader.getUsername());
            messageService.updateRead(message, sets.stream().toList());
        }
//        return messageService.getUpdatedList(chatroom_id, messageReadDTO.end()).stream().map(this::GetMessageDTO).toList();
    }

    /*
     * MessageReservation or ChatReservation
     */

    @Transactional
    public MessageReservationResponseDTO reservationMessage(MessageReservationRequestDTO messageReservationRequestDTO, String username) {
        Chatroom chatroom = chatroomService.getChatRoomById(messageReservationRequestDTO.chatroomId());
        SiteUser sender = userService.get(username);
        MessageReservation messageReservation = MessageReservation.builder().chatroom(chatroom).message(messageReservationRequestDTO.message()).sender(sender).sendDate(messageReservationRequestDTO.sendDate()).messageType(messageReservationRequestDTO.messageType()).build();

        messageReservationService.save(messageReservation);
        return getMessageReservation(messageReservation);
    }

    @Transactional
    private MessageReservationResponseDTO getMessageReservation(MessageReservation messageReservation) {
        Long sendTime = this.dateTimeTransfer(messageReservation.getSendDate());
        return MessageReservationResponseDTO.builder().id(messageReservation.getId()).chatroomId(messageReservation.getChatroom().getId()).message(messageReservation.getMessage()).username(messageReservation.getSender().getUsername()).sendDate(sendTime).messageType(messageReservation.getMessageType()).build();
    }


    @Transactional
    public void deleteReservationMessage(Long reservationMessageId) {
        MessageReservation messageReservation = messageReservationService.getMessageReservation(reservationMessageId);
        messageReservationService.delete(messageReservation);
    }

    @Transactional
    public MessageReservationResponseDTO updateReservationMessage(Long id,
                                                                  MessageReservationRequestDTO messageReservationRequestDTO,
                                                                  String username) throws DataNotFoundException {
        MessageReservation messageReservation = messageReservationService.getMessageReservation(id);
        if (messageReservation.getSender().getUsername().equals(username) && messageReservation.getChatroom().getId().equals(messageReservationRequestDTO.chatroomId())) {
            messageReservationService.update(messageReservation, messageReservationRequestDTO);
        }
        return getMessageReservation(messageReservation);
    }

    @Transactional
    public ChatroomResponseDTO notification(NoticeRequestDTO noticeRequestDTO, String username) {
        Chatroom chatroom = chatroomService.getChatRoomById(noticeRequestDTO.chatroomId());
        Message message = messageService.getMessageById(noticeRequestDTO.messageId());
        chatroomService.notification(chatroom, message);

        return getChatRoom(chatroom, username);
    }

    @Transactional
    public int alarmCount(Long chatroomId, Long endId) {
        List<Message> messageList = messageService.getUpdatedList(chatroomId, endId);
        return messageList.size() - 1;
    }
    /*
     * Time
     */

    private Long dateTimeTransfer(LocalDateTime dateTime) {
        return dateTime == null ? 0 : dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /*
     * File
     */
    public void deleteFileWithFolder(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File list : file.listFiles())
                    deleteFileWithFolder(list);
            }
            file.delete();
        }
    }
}