package com.team.HoneyBadger.Service;


import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Entity.FileSystem;
import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Enum.*;
import com.team.HoneyBadger.Exception.*;
import com.team.HoneyBadger.HoneyBadgerApplication;
import com.team.HoneyBadger.Security.CustomUserDetails;
import com.team.HoneyBadger.Security.JWT.JwtTokenProvider;
import com.team.HoneyBadger.Service.Module.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


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
    private final DepartmentService departmentService;
    private final QuestionService questionService;
    private final CycleService cycleService;
    private final HolidayService holidayService;
    private final ApprovalService approvalService;
    private final ApproverService approverService;
    private final ViewerService viewerService;
    private final CycleTagService cycleTagService;
    private final TeamPeopleService teamPeopleService;
    private final HashMap<String, HashMap<String, List<byte[]>>> dataStack = new HashMap<>();


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
        if (user == null) throw new IllegalArgumentException("username");

        if (!this.userService.isMatch(requestDto.password(), user.getPassword()))
            throw new IllegalArgumentException("password");
        if (!user.isActive()) throw new IllegalArgumentException("disabled");
        deleteFileWithFolder(new File(HoneyBadgerApplication.getOsType().getLoc() + "/api/user/" + requestDto.username() + "/download_temp"));

        String accessToken = this.jwtTokenProvider.generateAccessToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
        String refreshToken = this.jwtTokenProvider.generateRefreshToken(new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
        return AuthResponseDTO.builder().tokenType("Bearer").accessToken(accessToken).refreshToken(refreshToken).build();
    }

    /*
     * User
     */
    @Transactional
    public UserResponseDTO signup(UserInfoRequestDTO requestDTO) throws DataDuplicateException {
        Optional<SiteUser> _user = userService.getOptional(requestDTO.username());
        if (_user.isPresent()) throw new DataDuplicateException("username");
        Department department = requestDTO.department_id() != null ? departmentService.get(requestDTO.department_id()) : null;
        SiteUser user = userService.save(requestDTO.username(), requestDTO.name(), requestDTO.password(), UserRole.values()[requestDTO.role()], requestDTO.phoneNumber(), requestDTO.joinDate(), department);
        return getUserResponseDTO(user);
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
                .active(user.isActive()) //
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

    public Page<UserResponseDTO> getUsers(String keyword, int page, int size) {
        return userService.getUsers(keyword, page, size).map(this::getUserResponseDTO);
    }

    public void changePassword(String username, PasswordChangeDTO passwordChangeDTO) {
        SiteUser user = userService.get(username);
        if (!userService.isMatch(passwordChangeDTO.prePassword(), user.getPassword()))
            throw new DataNotSameException("password");
        userService.update(user, passwordChangeDTO.newPassword());
    }

    public UserResponseDTO changeUser(UserInfoRequestDTO requestDTO) {
        SiteUser user = userService.get(requestDTO.username());
        Department department = requestDTO.department_id() != null ? departmentService.get(requestDTO.department_id()) : null;
        UserRole role = requestDTO.role() >= 0 && requestDTO.role() < UserRole.values().length ? UserRole.values()[requestDTO.role()] : null;
        user = userService.update(user, requestDTO.name(), role, requestDTO.password(), requestDTO.phoneNumber(), requestDTO.joinDate(), department);
        return getUserResponseDTO(user);
    }


    public UserResponseDTO changeUserStatus(String username) {
        SiteUser user = userService.get(username);
        user = userService.changeStatus(user);
        return getUserResponseDTO(user);
    }

    public void deleteUserTemp(String username) {
        Optional<MultiKey> _multiKey = multiKeyService.get(KeyPreset.USER_TEMP_MULTI.getValue(username));
        if (_multiKey.isPresent()) {
            MultiKey multiKey = _multiKey.get();
            String path = HoneyBadgerApplication.getOsType().getLoc();
            ;
            for (String k : multiKey.getKeyValues()) {
                Optional<FileSystem> _fileSystem = fileSystemService.get(k);
                if (_fileSystem.isPresent()) {
                    FileSystem fileSystem = _fileSystem.get();
                    File file = new File(path + fileSystem.getV());
                    deleteFileWithFolder(file);
                    fileSystemService.deleteByKey(fileSystem);
                }
            }
            multiKeyService.delete(multiKey);
        }
    }

    /*
     * ChatRoom
     */
    @Transactional
    public ChatroomResponseDTO getChatRoomType(ChatroomRequestDTO chatroomRequestDTO, String loginUser) throws NotAllowedException {
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
            throw new NotAllowedException("채팅방에 참여할 유저를 선택해주세요.");
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
                List<String> chatroomUsernames = chatroomParticipants.stream()
                        .map(p -> p.getUser().getUsername()).collect(Collectors.toList());

                // 요청된 사용자 목록과 동일여부
                if (new HashSet<>(chatroomRequestDTO.users()).containsAll(chatroomUsernames)) {
                    Chatroom chatroom = chatroomParticipants.get(0).getChatroom();
                    List<UserResponseDTO> users = this.userChange(chatroom, chatroomUsernames);

                    // 채팅방이 존재할 경우 ChatroomResponseDTO 생성하여 반환
                    return ChatroomResponseDTO.builder().id(chatroom.getId()).name(chatroom.getName()).users(users).build();
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
    public Page<ChatroomResponseDTO> getChatRoomListByUser(String username, String keyword, int page) throws DataNotFoundException {
        SiteUser siteUser = userService.get(username);
        Pageable pageable = PageRequest.of(page, 20);
        Page<Chatroom> chatroomPage = chatroomService.getChatRoomListByUser(siteUser, keyword, pageable);

        List<ChatroomResponseDTO> chatroomResponseDTOList = chatroomPage.stream()
                .map(chatroom -> getChatRoom(chatroom, username)).collect(Collectors.toList());

        return new PageImpl<>(chatroomResponseDTOList, pageable, chatroomPage.getTotalElements());
    }


    @Transactional
    public Page<MessageResponseDTO> getMessageList(Long chatroomId, int page) throws DataNotFoundException {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        Pageable pageable = PageRequest.of(page, 15);
        Page<MessageResponseDTO> messagePage = messageService.getMessageList(chatroom.getMessageList(), pageable);
        return messagePage;
    }

    @Transactional
    public List<MessageResponseDTO> updateMessageList(String username, Long chatroomId) throws DataNotFoundException {
        SiteUser user = userService.get(username);
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        LastReadMessage lastReadMessage = lastReadMessageService.get(user, chatroom);

        List<Message> updatedMessages;
        if (lastReadMessage != null) { // 특정 채팅방에서 유저의 startId 이후의 업데이트된 메시지 목록을 가져옴
            updatedMessages = messageService.getUpdatedList(chatroomId, lastReadMessage.getLastReadMessage());
        } else { // 특정 채팅방의 메세지 목록을 가져옴
            updatedMessages = chatroom.getMessageList();
        }

        Message lastMessage = updatedMessages.get(updatedMessages.size() - 1);
        Long lastMessageId = lastMessage.getId();

        this.saveLastMessage(user, chatroom, lastMessageId);

        return updatedMessages.stream().map(this::GetMessageDTO).toList();
    }

    private void saveLastMessage(SiteUser user, Chatroom chatroom, Long lastReadMessageId) {
        LastReadMessage lastReadMessage = lastReadMessageService.get(user, chatroom);
        if (lastReadMessage == null) {
            lastReadMessageService.create(user, chatroom, lastReadMessageId);
        } else {
            lastReadMessageService.updateMessage(lastReadMessage, lastReadMessageId);
        }
    }

    @Transactional
    private List<UserResponseDTO> userChange(Chatroom chatroom, List<String> usernames) {
        List<UserResponseDTO> users = new ArrayList<>();
        for (String user : usernames) {
            SiteUser siteUser = userService.get(user);
            UserResponseDTO userResponseDTO = getUserResponseDTO(siteUser);
            users.add(userResponseDTO);
        }
        return users;
    }

    @Transactional
    private ChatroomResponseDTO getChatRoom(Chatroom chatroom, String username) {
        List<String> usernames = chatroom.getParticipants().stream()
                .map(participant -> participant.getUser().getUsername()).toList();

        List<UserResponseDTO> users = this.userChange(chatroom, usernames);

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

        int alarmCnt;

        if (lastReadMessage == null) {
            if (!chatroom.getMessageList().isEmpty()) {
                alarmCnt = chatroom.getMessageList().size();
            } else {
                alarmCnt = 0;
            }
        } else {
            alarmCnt = alarmCount(chatroom.getId(), lastReadMessage.getLastReadMessage());
        }

        return ChatroomResponseDTO.builder()
                .id(chatroom.getId())
                .name(chatroom.getName())
                .users(users)
                .latestMessage(latestMessageDTO)
                .notification(notificationDTO)
                .alarmCount(alarmCnt)
                .createDate(dateTimeTransfer(chatroom.getCreateDate()))
                .build();
    }

    @Transactional
    public int alarmCount(Long chatroomId, Long endId) {
        List<Message> messageList = messageService.getUpdatedList(chatroomId, endId);
        return messageList.size() - 1;
    }


    @Transactional
    public ChatroomResponseDTO notification(NoticeRequestDTO noticeRequestDTO, String username) throws DataNotFoundException {
        Chatroom chatroom = chatroomService.getChatRoomById(noticeRequestDTO.chatroomId());
        Message message = messageService.getMessageById(noticeRequestDTO.messageId());
        chatroomService.notification(chatroom, message);

        return getChatRoom(chatroom, username);
    }


    @Transactional
    public ChatroomResponseDTO getChatRoomById(Long chatroomId, String username) throws DataNotFoundException {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        return getChatRoom(chatroom, username);
    }

    @Transactional
    public void deleteChatroom(Long chatroomId) throws DataNotFoundException {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        chatroomService.delete(chatroom);
    }

    @Transactional
    public ChatroomResponseDTO updateChatroom(Long chatroomId, ChatroomRequestDTO chatroomRequestDTO, String username) throws DataNotFoundException {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        chatroom = chatroomService.updateChatroom(chatroom, chatroomRequestDTO.name());
        return getChatRoom(chatroom, username);
    }

    @Transactional
    public ChatroomResponseDTO plusParticipant(Long chatroomId, String username, String loginUser) throws DataNotFoundException {
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

    @Transactional //이메일 파일 업로드
    public void emailFilesUpload(Long email_id, List<MultipartFile> files) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        String keyValue = KeyPreset.EMAIL_MULTI.getValue(email_id.toString());
        MultiKey key = multiKeyService.get(keyValue).orElseGet(() -> multiKeyService.save(keyValue));
        List<String> list = new ArrayList<>();//
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

    @Transactional
    public String emailContentUpload(String username, MultipartFile file) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        UUID uuid = UUID.randomUUID();
        String fileName = "/api/user/" + username + "/temp/" + uuid.toString() + "." + (Objects.requireNonNull(file.getOriginalFilename()).contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE

        // 멀티키
        String keyString = KeyPreset.USER_TEMP_MULTI.getValue(username);
        Optional<MultiKey> _key = multiKeyService.get(keyString);
        MultiKey key = _key.orElseGet(() -> multiKeyService.save(keyString));

        // 파일 저장
        FileSystem fileSystem = fileSystemService.save(KeyPreset.USER_TEMP.getValue(username + "_" + key.getKeyValues().size()), fileName);
        multiKeyService.updateOne(key, fileSystem.getK());

        File dest = new File(path + fileName);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);
        return fileName;
    }

    @Transactional
    public Long sendEmail(String title, String content, String senderId, List<String> receiverIds) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        if (receiverIds.isEmpty()) {
            throw new EmailReceiverNotFoundException("email not found");
        }
        SiteUser sender = userService.get(senderId);
        Email email = emailService.save(title, sender);
        if (content != null)
            emailService.update(email, content.replaceAll("/user/" + sender.getUsername(), "/email/" + email.getId().toString()).replaceAll("/temp/", "/").replaceAll("/emailReservation", "/email/" + email.getId().toString()));
        Optional<MultiKey> _key = multiKeyService.get(KeyPreset.USER_TEMP_MULTI.getValue(senderId));
        if (_key.isPresent()) {
            MultiKey key = _key.get();
            for (String k : key.getKeyValues()) {
                Optional<FileSystem> _fileSystem = fileSystemService.get(k);
                if (_fileSystem.isPresent()) {
                    FileSystem fileSystem = _fileSystem.get();
                    String value = fileSystem.getV();
                    Path prePath = Paths.get(path + value);
                    Path newPath = Paths.get(path + value.replaceAll("/user/" + sender.getUsername(), "/email/" + email.getId().toString()).replaceAll("/temp/", "/"));
                    if (!newPath.getParent().toFile().exists()) newPath.getParent().toFile().mkdirs();
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

    public Page<Object> getEmailsForUser(String username, int statusIndex, int page) throws DataNotFoundException, IllegalArgumentException {
        Pageable pageable = PageRequest.of(page, 15);
        switch (statusIndex) {
            case 0:
                Page<Email> senderEmails = emailReceiverService.getSentEmailsForUser(username, pageable);
                if (senderEmails == null) {
                    throw new DataNotFoundException("Failed to retrieve sent emails for user: " + username);
                }
                return new PageImpl<>(senderEmails.stream().map(email -> getEmailDTO(email, username)).collect(Collectors.toList()), pageable, senderEmails.getTotalElements());

            case 1:
                Page<Email> receiverEmails = emailReceiverService.getReceivedEmailsForUser(username, pageable);
                if (receiverEmails == null) {
                    throw new DataNotFoundException("Failed to retrieve received emails for user: " + username);
                }
                return new PageImpl<>(receiverEmails.stream().map(email -> getEmailDTO(email, username)).collect(Collectors.toList()), pageable, receiverEmails.getTotalElements());

            case 2:
                Page<EmailReservation> reservationEmails = emailReservationService.getReservedEmailsForUser(username, pageable);
                if (reservationEmails == null) {
                    throw new DataNotFoundException("Failed to retrieve reserved emails for user: " + username);
                }
                return new PageImpl<>(reservationEmails.stream().map(this::getEmailReservationDTO).collect(Collectors.toList()), pageable, reservationEmails.getTotalElements());

            default:
                throw new NotAllowedException("Invalid status index: " + statusIndex);
        }
    }

    @Transactional
    public EmailResponseDTO read(EmailReadRequestDTO emailReadRequestDTO) throws RuntimeException {
        Email email = emailService.getEmail(emailReadRequestDTO.emailId());
        emailReceiverService.markEmailAsRead(emailReadRequestDTO.emailId(), emailReadRequestDTO.receiverId());
        EmailResponseDTO emailResponseDTO = getEmailDTO(email, emailReadRequestDTO.receiverId());
        return emailResponseDTO;
    }

    @Transactional
    public void deleteEmail(Long emailId, String username) throws RuntimeException {
        Email email = emailService.getEmail(emailId);
        emailService.findByUsernameDelete(email, username);
    }

    private EmailResponseDTO getEmailDTO(Email email, String receiverId) throws DataNotFoundException {
        List<OriginFileResponseDTO> filePathList = new ArrayList<>();
        Optional<MultiKey> _multiKey = multiKeyService.get(KeyPreset.EMAIL_MULTI.getValue(email.getId().toString()));
        if (_multiKey.isPresent()) {
            for (String key : _multiKey.get().getKeyValues()) {
                OriginFileResponseDTO.OriginFileResponseDTOBuilder builder = OriginFileResponseDTO.builder();
                fileSystemService.get(key).ifPresent(fileSystem -> builder.value(fileSystem.getV())); // url
                fileSystemService.get(KeyPreset.EMAIL_ORIGIN.getValue(key)).ifPresent(fileSystem -> builder.original_name(fileSystem.getV())); // original Name
                builder.key(key); // key
                filePathList.add(builder.build());
            }
        }

        SiteUser user = userService.get(receiverId);

        // receiverId를 기반으로 읽음 상태를 조회
        EmailReceiver emailReceiver = emailReceiverService.getReadStatus(email, user);

        List<EmailReceiverDTO> receiverStatus = email.getReceiverList().stream().map(receiver -> EmailReceiverDTO.builder().receiverUsername(receiver.getReceiver().getUsername()).status(receiver.isStatus()).build()).collect(Collectors.toList());

        boolean status = false;
        if (emailReceiver != null) {
            status = emailReceiver.isStatus();
        }

        return EmailResponseDTO.builder() //
                .id(email.getId()) //
                .title(email.getTitle()) //
                .content(email.getContent()) //
                .senderId(email.getSender().getUsername()) //
                .senderName(email.getSender().getUsername()) //
                .receiverIds(email.getReceiverList().stream().map(er -> er.getReceiver().getUsername()).toList()) //
                .senderTime(this.dateTimeTransfer(email.getCreateDate())) //
                .files(filePathList).status(status) // emailReceiver가 null인 경우 기본값으로 false 설정
                .receiverStatus(receiverStatus).build();
    }

    public EmailResponseDTO getEmailDTO(Long emailId, String username) throws RuntimeException {
        Email email = emailService.getEmail(emailId);
        return getEmailDTO(email, username);
    }

    /*
     * Email Reservation
     */

    @Scheduled(cron = "0 0/1 * * * *")
    @Transactional

    public void sendEmailReservation() throws IOException {
        List<EmailReservation> emailReservationList = emailReservationService.getEmailReservationFromDate(LocalDateTime.now());
        for (EmailReservation emailReservation : emailReservationList) {
            if (emailReservation.getSendTime().toLocalTime().isBefore(LocalTime.now())) {
                Long emailId = sendEmail(emailReservation.getTitle(), emailReservation.getContent().replaceAll("/emailReservation/" + emailReservation.getId().toString(), "/emailReservation"), emailReservation.getSender().getUsername(), emailReservation.getReceiverList());
                String path = HoneyBadgerApplication.getOsType().getLoc();
                {
                    String keyValue = KeyPreset.EMAIL_RESERVATION_MULTI_TEMP.getValue(emailReservation.getId().toString());
                    Optional<MultiKey> _key = multiKeyService.get(keyValue);
                    if (_key.isPresent()) {
                        MultiKey key = _key.get();
                        for (String k : key.getKeyValues()) {
                            Optional<FileSystem> _fileSystem = fileSystemService.get(k);
                            if (_fileSystem.isPresent()) {
                                FileSystem fileSystem = _fileSystem.get();
                                String preUrl = fileSystem.getV();
                                String newUrl = preUrl.replaceAll("/emailReservation/" + emailReservation.getId().toString(), "/email/" + emailId.toString());
                                Path prePath = Paths.get(path + preUrl);
                                Path newPath = Paths.get(path + newUrl);
                                if (!newPath.getParent().toFile().exists()) newPath.getParent().toFile().mkdirs();
                                Files.move(prePath, newPath, StandardCopyOption.REPLACE_EXISTING);
                                deleteFileWithFolder(prePath.getParent().toFile());
                                fileSystemService.deleteByKey(fileSystem);
                            }
                        }
                        multiKeyService.delete(key);
                    }
                }
                {
                    String keyValue = KeyPreset.EMAIL_RESERVATION_MULTI.getValue(emailReservation.getId().toString());
                    Optional<MultiKey> _key = multiKeyService.get(keyValue);
                    if (_key.isPresent()) {
                        MultiKey key = _key.get();
                        for (String k : key.getKeyValues()) {
                            Optional<FileSystem> _fileSystem = fileSystemService.get(k);
                            if (_fileSystem.isPresent()) {
                                FileSystem fileSystem = _fileSystem.get();
                                String preUrl = fileSystem.getV();
                                String newUrl = preUrl.replaceAll("/user/email/" + emailReservation.getId().toString(), "/email/" + emailId.toString());
                                Path prePath = Paths.get(path + preUrl);
                                Path newPath = Paths.get(path + newUrl);
                                if (!newPath.getParent().toFile().exists()) newPath.getParent().toFile().mkdirs();
                                Files.move(prePath, newPath, StandardCopyOption.REPLACE_EXISTING);
                                deleteFileWithFolder(prePath.getParent().toFile());
                                fileSystemService.deleteByKey(fileSystem);
                                String fileKey = k.replaceAll("_RESERVATION_" + emailReservation.getId().toString(), "_" + emailId.toString());
                                fileSystemService.save(fileKey, newUrl);

                                Optional<FileSystem> _preOrigin = fileSystemService.get(KeyPreset.EMAIL_RESERVATION_ORIGIN.getValue(k));
                                if (_preOrigin.isPresent()) {
                                    FileSystem preOrigin = _fileSystem.get();
                                    fileSystemService.save(KeyPreset.EMAIL_ORIGIN.getValue(fileKey), preOrigin.getV());
                                    fileSystemService.deleteByKey(preOrigin);
                                }
                            }
                        }
                        multiKeyService.delete(key);
                    }
                }
                emailReservationService.delete(emailReservation);
            }
        }
    }

    @Transactional //예약이메일 파일 업로드
    public void emailReservationFilesUpload(Long email_id, List<MultipartFile> files) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        String keyValue = KeyPreset.EMAIL_RESERVATION_MULTI.getValue(email_id.toString());
        MultiKey key = multiKeyService.get(keyValue).orElseGet(() -> multiKeyService.save(keyValue));
        List<String> list = key.getKeyValues();//
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

    @Transactional
    public void deleteEmailReservation(Long reservationId, String username) throws DataNotFoundException {
        EmailReservation emailReservation = emailReservationService.getEmailReservation(reservationId);
        if (emailReservation.getSender().getUsername().equals(username)) {
            emailReservationService.delete(emailReservation);
        }
    }

    @Transactional
    public Long reservationEmail(EmailReservationRequestDTO requestDTO, String username) throws IOException, EmailReceiverNotFoundException {
        SiteUser sender = userService.get(username);
        if (requestDTO.receiverIds().isEmpty()) {
            throw new EmailReceiverNotFoundException("email not found");
        }
        EmailReservation emailReservation = emailReservationService.save(requestDTO.title(), requestDTO.receiverIds(), sender, requestDTO.sendTime());
        String content = requestDTO.content();

        if (content != null)
            emailReservationService.update(emailReservation, content.replaceAll("/user/" + sender.getUsername(), "/emailReservation/" + emailReservation.getId().toString()).replaceAll("/temp/", "/"));
        String path = HoneyBadgerApplication.getOsType().getLoc();
        Optional<MultiKey> _key = multiKeyService.get(KeyPreset.USER_TEMP_MULTI.getValue(username));

        if (_key.isPresent()) {
            MultiKey key = _key.get();
            String newMultiKey = KeyPreset.EMAIL_RESERVATION_MULTI_TEMP.getValue(emailReservation.getId().toString());
            MultiKey newMulti = multiKeyService.get(newMultiKey).orElseGet(() -> multiKeyService.save(newMultiKey));
            List<String> newKeys = newMulti.getKeyValues();

            for (String k : key.getKeyValues()) {
                Optional<FileSystem> _fileSystem = fileSystemService.get(k);

                if (_fileSystem.isPresent()) {
                    FileSystem fileSystem = _fileSystem.get();
                    String value = fileSystem.getV();
                    Path prePath = Paths.get(path + value);
                    String newUrl = value.replaceAll("/user/" + sender.getUsername(), "/emailReservation/" + emailReservation.getId().toString()).replaceAll("/temp/", "/");
                    Path newPath = Paths.get(path + newUrl);

                    if (!newPath.getParent().toFile().exists()) newPath.getParent().toFile().mkdirs();
                    Files.move(prePath, newPath, StandardCopyOption.REPLACE_EXISTING);
                    fileSystemService.deleteByKey(fileSystem);
                    String newKey = KeyPreset.EMAIL_RESERVATION_TEMP.getValue(emailReservation.getId().toString() + "_" + newKeys.size());
                    fileSystemService.save(newKey, newUrl);
                    newKeys.add(newKey);
                }
            }
            multiKeyService.delete(key);
            multiKeyService.updateAll(newMulti, newKeys);
        }
        return emailReservation.getId();
    }

    @Transactional
    private EmailReservationResponseDTO getEmailReservationDTO(EmailReservation reservation) {
        List<OriginFileResponseDTO> fileslist = new ArrayList<>();
        Optional<MultiKey> _multiKey = multiKeyService.get(KeyPreset.EMAIL_RESERVATION_MULTI.getValue(reservation.getId().toString()));

        if (_multiKey.isPresent()) for (String key : _multiKey.get().getKeyValues()) {
            OriginFileResponseDTO.OriginFileResponseDTOBuilder builder = OriginFileResponseDTO.builder();
            fileSystemService.get(key).ifPresent(file -> builder.value(file.getV()));
            fileSystemService.get(KeyPreset.EMAIL_RESERVATION_ORIGIN.getValue(key)).ifPresent(file -> builder.original_name(file.getV()));
            builder.key(key);
            fileslist.add(builder.build());
        }
        return EmailReservationResponseDTO.builder().title(reservation.getTitle()).content(reservation.getContent()).id(reservation.getId()).receiverIds(reservation.getReceiverList()).senderTime(this.dateTimeTransfer(reservation.getSendTime())).files(fileslist).build();
    }

    @Transactional
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
            throw new UnauthorizedException("권한이 없습니다.");
        }
    }

    /*
     * Message or Chat
     */

    @Transactional
    private MessageType getMessageType(int MessageTypeInt) throws NotAllowedException {
        MessageType messageType;
        return switch (MessageTypeInt) {
            case 0 -> {
                messageType = MessageType.TEXT;
                yield messageType;
            }
            case 1 -> {
                messageType = MessageType.IMAGE;
                yield messageType;
            }
            case 2 -> {
                messageType = MessageType.LINK;
                yield messageType;
            }
            case 3 -> {
                messageType = MessageType.FILE;
                yield messageType;
            }
            default -> throw new NotAllowedException("Unknown message type: " + MessageTypeInt);
        };
    }

    @Transactional
    public MessageResponseDTO sendMessage(Long id, MessageRequestDTO messageRequestDTO) throws DataNotFoundException, NotAllowedException {
        Chatroom chatroom = chatroomService.getChatRoomById(id);
        SiteUser siteUser = userService.get(messageRequestDTO.username());
        MessageType messageType = this.getMessageType(messageRequestDTO.messageType());

        if (messageRequestDTO.message().isEmpty()) {
            throw new NotAllowedException("메세지를 입력해주세요.");
        }

        return GetMessageDTO(messageService.save(messageRequestDTO.message(), siteUser, chatroom, messageType));
    }

    @Transactional
    private MessageResponseDTO GetMessageDTO(Message message) {
        Long sendTime = this.dateTimeTransfer(message.getCreateDate());
//        int readUsers = message.getReadUsers().size();

        int readUsers;
        if (message.getReadUsers() == null) {
            readUsers = 0;
        } else {
            readUsers = message.getReadUsers().size();
        }

        return MessageResponseDTO.builder()
                .id(message.getId())
                .sendTime(sendTime)
                .username(message.getSender().getUsername())
                .name(message.getSender().getName())
                .message(message.getMessage())
                .messageType(message.getMessageType().ordinal())
                .readUsers(readUsers)
                .build();
    }

    @Transactional
    public void deleteMessage(Long messageId, String username) throws DataNotFoundException, NotAllowedException {
        Message message = messageService.getMessageById(messageId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime send = message.getCreateDate();

        if (!message.getSender().getUsername().equals(username)) {
            throw new NotAllowedException("삭제 권한이 없습니다.");
        }

        // 메시지의 createDate가 now 기준으로 5분 이내인지 확인
        if (Duration.between(send, now).toMinutes() <= 5) {
            // 메시지를 삭제하는 로직을 추가합니다.
            messageService.deleteMessage(message);
        } else {
            // 메시지가 5분을 초과했을 때의 로직을 추가합니다.
            throw new NotAllowedException("5분이 지나 메세지를 삭제할 수 없습니다.");
        }
    }

    @Transactional
    public String fileUpload(Long chatroomId, MultipartFile file) throws IOException {

        String path = HoneyBadgerApplication.getOsType().getLoc();
        UUID uuid = UUID.randomUUID();
        String fileName = "/api/chatroom/" + chatroomId.toString()
                + "/" + uuid.toString() + "."
                + (file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE

        // 너굴맨이 해치우고 갔어요!
        File dest = new File(path + fileName);

        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);

        return fileName;
    }

    private final Lock lock = new ReentrantLock();

    @Transactional
    public void readMessage(Long chatroomId, String username) throws DataNotFoundException { //메세지 읽기 처리
        lock.lock();
        try {
            SiteUser reader = userService.get(username);
            Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
            Participant participant = participantService.get(reader, chatroom);
            if (participant != null){
                LastReadMessage lastReadMessage = lastReadMessageService.get(reader, chatroom);
                Long startId = (lastReadMessage != null) ? lastReadMessage.getLastReadMessage() : null; //마지막 메세지가 있으면 startId, 없으면 null

                for (Message message : startId != null ? messageService.getList(startId) : chatroom.getMessageList()) { //읽음처리
                    HashSet<String> sets = new HashSet<>(message.getReadUsers());
                    sets.add(reader.getUsername());
                    messageService.updateRead(message, sets.stream().toList());
                }
            } else {
                throw new DataNotFoundException("채팅방에 있는 유저가 아닙니다.");
            }
        } finally {
            lock.unlock();
        }
    }

    public List<MessageResponseDTO> getImageMessageList(Long chatroomId) throws DataNotFoundException {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        return messageService.getImageMessageList(chatroom);
    }

    public List<MessageResponseDTO> getLinkMessageList(Long chatroomId) throws DataNotFoundException {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        return messageService.getLinkMessageList(chatroom);
    }

    public List<MessageResponseDTO> getFileMessageList(Long chatroomId) throws DataNotFoundException {
        Chatroom chatroom = chatroomService.getChatRoomById(chatroomId);
        return messageService.getFileMessageList(chatroom);
    }

    /*
     * MessageReservation or ChatReservation
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    @Transactional
    public void sendReservation() {
        List<MessageReservation> messageReservationList = messageReservationService.getMessageReservationFromDate(LocalDateTime.now());
        for (MessageReservation messageReservation : messageReservationList) {
            if (messageReservation.getSendDate().toLocalTime().isBefore(LocalTime.now())) {
                MessageRequestDTO messageRequestDTO = new MessageRequestDTO(
                        messageReservation.getMessage(),
                        messageReservation.getSender().getUsername(),
                        messageReservation.getMessageType());
                sendMessage(messageReservation.getChatroom().getId(), messageRequestDTO);
                messageReservationService.delete(messageReservation);
            }
        }
    }

    @Transactional
    public MessageReservationResponseDTO reservationMessage(MessageReservationRequestDTO messageReservationRequestDTO, String username)
            throws DataNotFoundException, NotAllowedException {
        Chatroom chatroom = chatroomService.getChatRoomById(messageReservationRequestDTO.chatroomId());
        SiteUser sender = userService.get(username);

        if (messageReservationRequestDTO.message().isEmpty()) {
            throw new NotAllowedException("메세지를 입력해주세요.");
        }

        if (messageReservationRequestDTO.reservationDate().isBefore(LocalDateTime.now())) {
            throw new NotAllowedException("지난 시간으로 예약할 수 없습니다.");
        }

        MessageReservation messageReservation = MessageReservation.builder()
                .chatroom(chatroom)
                .message(messageReservationRequestDTO.message())
                .sender(sender)
                .sendDate(messageReservationRequestDTO.reservationDate())
                .messageType(messageReservationRequestDTO.messageType())
                .build();

        messageReservationService.save(messageReservation);
        return getMessageReservation(messageReservation);
    }

    @Transactional
    private MessageReservationResponseDTO getMessageReservation(MessageReservation messageReservation) {
        Long reservationDate = this.dateTimeTransfer(messageReservation.getSendDate());
        System.out.println(reservationDate);
        System.out.println(messageReservation.getSendDate());
        return MessageReservationResponseDTO.builder()
                .id(messageReservation.getId())
                .chatroomId(messageReservation.getChatroom().getId())
                .message(messageReservation.getMessage())
                .username(messageReservation.getSender().getUsername())
                .name(messageReservation.getSender().getName())
                .reservationDate(reservationDate)
                .messageType(messageReservation.getMessageType())
                .build();
    }


    @Transactional
    public void deleteReservationMessage(Long reservationMessageId) throws DataNotFoundException {
        MessageReservation messageReservation = messageReservationService.getMessageReservation(reservationMessageId);
        messageReservationService.delete(messageReservation);
    }

    @Transactional
    public MessageReservationResponseDTO updateReservationMessage(Long reservationMessageId, MessageReservationRequestDTO messageReservationRequestDTO, String username)
            throws DataNotFoundException, NotAllowedException {
        MessageReservation messageReservation = messageReservationService.getMessageReservation(reservationMessageId);

        if (!messageReservation.getSender().getUsername().equals(username)) {
            throw new NotAllowedException("권한이 없습니다.");
        } else if (!messageReservation.getChatroom().getId().equals(messageReservationRequestDTO.chatroomId())) {
            throw new NotAllowedException("채팅방이 다릅니다.");
        } else {
            messageReservationService.update(messageReservation, messageReservationRequestDTO);
        }
        return getMessageReservation(messageReservation);
    }

    public MessageReservationResponseDTO getMessageReservationById(Long reservationMessageId) throws DataNotFoundException {
        MessageReservation messageReservation = messageReservationService.getMessageReservation(reservationMessageId);
        return getMessageReservation(messageReservation);
    }

    public Page<MessageReservationResponseDTO> getMessageReservationByUser(String username, int page) throws DataNotFoundException {
        SiteUser user = userService.get(username);
        Pageable pageable = PageRequest.of(page, 10);

        return messageReservationService.getMessageReservationByUser(user, pageable);
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

    /*
     * Department
     */
    @Transactional
    public List<DepartmentTopResponseDTO> createDepartment(String username, DepartmentRequestDTO requestDTO) throws IOException {
        Department parent = departmentService.get(requestDTO.parentId());
        if (departmentService.get(requestDTO.name()) != null)
            throw new DataDuplicateException("department already exist");

        Department department = departmentService.save(requestDTO.name(), parent, DepartmentRole.values()[requestDTO.role()]);
        if (requestDTO.url() != null) {
            String path = HoneyBadgerApplication.getOsType().getLoc();
            Path pre = Paths.get(path + requestDTO.url());
            String newUrl = requestDTO.url().replaceAll("/user/" + username + "/temp/depart_", "/department/" + department.getName() + "/");
            Path now = Paths.get(path + newUrl);
            if (!now.getParent().toFile().exists()) now.getParent().toFile().mkdirs();
            Files.move(pre, now, StandardCopyOption.REPLACE_EXISTING);
            fileSystemService.save(KeyPreset.DEPARTMENT_PROFILE.getValue(department.getName()), newUrl);
        }
        return departmentService.getTopList().stream().map(this::getDepartmentTopDTO).toList();
    }

    @Transactional
    public String saveDepartmentImage(String username, MultipartFile file) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        String key = KeyPreset.DEPARTMENT_PROFILE.getValue(username);
        Optional<FileSystem> _fileSystem = fileSystemService.get(key);
        if (_fileSystem.isPresent()) {
            File preFile = new File(path + _fileSystem.get().getV());
            if (preFile.exists()) deleteFileWithFolder(preFile);
        }
        UUID uuid = UUID.randomUUID();
        String fileName = "/api/user/" + username + "/temp/depart_" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");
        fileSystemService.save(key, fileName);
        File dest = new File(path + fileName);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);
        return fileName;
    }

    private DepartmentResponseDTO getDepartmentDTO(Department department) {
        if (department == null) return null;
        DepartmentResponseDTO parent = department.getParent() != null ? getDepartmentDTO(department.getParent()) : null;
        Optional<FileSystem> _fileSystem = fileSystemService.get(KeyPreset.DEPARTMENT_PROFILE.getValue(department.getName()));
        return DepartmentResponseDTO.builder().name(department.getName()).parent(parent).createDate(this.dateTimeTransfer(department.getCreateDate())).modifyDate(this.dateTimeTransfer(department.getModifyDate())).url(_fileSystem.map(FileSystem::getV).orElse(null)).role(department.getRole().ordinal()).build();
    }

    private DepartmentTopResponseDTO getDepartmentTopDTO(Department department) {
        if (department == null) return null;
        Optional<FileSystem> _fileSystem = fileSystemService.get(KeyPreset.DEPARTMENT_PROFILE.getValue(department.getName()));
        return DepartmentTopResponseDTO.builder().name(department.getName()).child(department.getChild().stream().map(this::getDepartmentTopDTO).toList()).createDate(this.dateTimeTransfer(department.getCreateDate())).modifyDate(this.dateTimeTransfer(department.getModifyDate())).url(_fileSystem.map(FileSystem::getV).orElse(null)).role(department.getRole().ordinal()).build();
    }

    public List<DepartmentTopResponseDTO> getDepartmentTree() {
        return departmentService.getTopList().stream().map(this::getDepartmentTopDTO).toList();
    }

    @Transactional
    public List<DepartmentTopResponseDTO> deleteDepartment(String departmentId) throws RelatedException {
        Department department = departmentService.get(departmentId);
        check(department);
        departmentService.delete(department);
        return getDepartmentTree();
    }

    public void check(Department department) {
        if (!department.getUsers().isEmpty()) throw new RelatedException("부서에 인원이 남아있습니다.");
        for (Department child : department.getChild())
            check(child);
    }

    public DepartmentUserResponseDTO getDepartmentUsers(String departmentId) {
        if (departmentId == null)
            return DepartmentUserResponseDTO.builder().users(userService.getUsersDepartmentIsNull().stream().map(this::getUserResponseDTO).toList()).build();

        Department department = departmentService.get(departmentId);
        if (department == null) throw new DataNotFoundException("해당 부서는 존재하지 않습니다.");
        return getDepartmentUserResponseDTO(department);
    }

    private DepartmentUserResponseDTO getDepartmentUserResponseDTO(Department department) {
        List<UserResponseDTO> users = department.getUsers().stream().map(this::getUserResponseDTO).toList();
        List<DepartmentUserResponseDTO> list = new ArrayList<>();
        for (Department child : department.getChild())
            list.add(getDepartmentUserResponseDTO(child));
        return DepartmentUserResponseDTO.builder().users(users).name(department.getName()).child(list).role(department.getRole().ordinal()).build();
    }

    /*
     * Question
     */
    public Page<QuestionDTO> getQuestions(int page, String keyword) {
        return questionService.getList(page, keyword).map(this::getQuestionDTO);
    }

    public void createQuestion(QuestionDTO questionDTO) {
        questionService.save(questionDTO.title(), questionDTO.content(), questionDTO.author(), questionDTO.password(), questionDTO.lock());
    }

    public QuestionDTO createAnswer(QuestionDTO requestDto) throws DataNotFoundException {
        Question question = questionService.get(requestDto.id());
        question = questionService.update(question, requestDto.answer());
        return getQuestionDTO(question);
    }

    private QuestionDTO getQuestionDTO(Question question) {
        return QuestionDTO.builder()//
                .id(question.getId()).title(question.getTitle())//
                .content(question.getContent())//
                .answer(question.getAnswer())//
                .author(question.getAuthor())//
                .createDate(this.dateTimeTransfer(question.getCreateDate())) //
                .modifyDate(this.dateTimeTransfer(question.getModifyDate())) //
                .lock(question.isLock())//
                .build();

    }

    public boolean checkQuestion(QuestionDTO requestDto) {
        Question question = questionService.get(requestDto.id());
        return questionService.checkPassword(question, requestDto.password());
    }

    /*
     * Cycle
     */
    @Transactional

    public void createPersonalCycle(int status, String username, CycleRequestDTO cycleRequestDTO) throws NotAllowedException {
        if (cycleRequestDTO.title() == null || cycleRequestDTO.title().isEmpty()) {
            throw new NotAllowedException("제목을 입력해주세요.");
        } else if (cycleRequestDTO.content() == null || cycleRequestDTO.content().isEmpty()) {
            throw new NotAllowedException("내용을 입력해주세요.");
        } else if (cycleRequestDTO.startDate() == null) {
            throw new NotAllowedException("시작 시간을 입력해주세요.");
        } else if (cycleRequestDTO.endDate() == null) {
            throw new NotAllowedException("종료 시간을 입력해주세요.");
        } else if (cycleRequestDTO.endDate().isBefore(cycleRequestDTO.startDate()) || cycleRequestDTO.startDate().equals(cycleRequestDTO.endDate())) {
            throw new NotAllowedException("시간 설정을 다시 해주세요.");
        } else if (cycleRequestDTO.tagName() != null && cycleRequestDTO.tagColor() == null) {
            throw new NotAllowedException("색상 설정을 다시 해주세요.");
        } else if (cycleRequestDTO.tagName() == null && cycleRequestDTO.tagColor() != null) {
            throw new NotAllowedException("태그 설정을 다시 확인해주세요.");
        }
        SiteUser user = userService.get(username);
        switch (status) {
            case 0:
                if (cycleRequestDTO.tagName() == null) {
                    cycleService.create(KeyPreset.UC.getValue(user.getUsername()), cycleRequestDTO);
                } else {
                    CycleTag cycleTag = cycleTagService.findByName(KeyPreset.UC.getValue(user.getUsername()), cycleRequestDTO.tagName());
                    if (cycleTag == null) {
                        CycleTag cycleTag1 = cycleTagService.create(KeyPreset.UC.getValue(user.getUsername()), cycleRequestDTO.tagName(), cycleRequestDTO.tagColor());
                        cycleService.createByTag(KeyPreset.UC.getValue(user.getUsername()), cycleRequestDTO.title(), cycleRequestDTO.content(), cycleRequestDTO.startDate(), cycleRequestDTO.endDate(), cycleTag1);
                    } else {
                        cycleService.createByTag(KeyPreset.UC.getValue(user.getUsername()), cycleRequestDTO.title(), cycleRequestDTO.content(), cycleRequestDTO.startDate(), cycleRequestDTO.endDate(), cycleTag);
                    }
                }
                break;

            case 1:
                if (user.getDepartment() == null) {
                    throw new NotAllowedException("부서가 없습니다.");
                }
                if (cycleRequestDTO.tagName() == null) {
                    cycleService.create(KeyPreset.DC.getValue(user.getDepartment().getName()), cycleRequestDTO);
                } else {
                    CycleTag cycleTag = cycleTagService.findByName(KeyPreset.DC.getValue(user.getDepartment().getName()), cycleRequestDTO.tagName());
                    if (cycleTag == null) {
                        CycleTag cycleTag1 = cycleTagService.create(KeyPreset.DC.getValue(user.getDepartment().getName()), cycleRequestDTO.tagName(), cycleRequestDTO.tagColor());
                        cycleService.createByTag(KeyPreset.DC.getValue(user.getDepartment().getName()), cycleRequestDTO.title(), cycleRequestDTO.content(), cycleRequestDTO.startDate(), cycleRequestDTO.endDate(), cycleTag1);
                    } else {
                        cycleService.createByTag(KeyPreset.DC.getValue(user.getDepartment().getName()), cycleRequestDTO.title(), cycleRequestDTO.content(), cycleRequestDTO.startDate(), cycleRequestDTO.endDate(), cycleTag);
                    }
                }
                break;

            case 2:
                if (cycleRequestDTO.teamName() == null) {
                    throw new NotAllowedException("팀을 선택해주세요.");
                }
                if (cycleRequestDTO.tagName() == null) {
                    cycleService.create(KeyPreset.TC.getValue(cycleRequestDTO.teamName()), cycleRequestDTO);
                } else {
                    CycleTag cycleTag = cycleTagService.findByName(KeyPreset.TC.getValue(cycleRequestDTO.teamName()), cycleRequestDTO.tagName());
                    if (cycleTag == null) {
                        CycleTag cycleTag1 = cycleTagService.create(KeyPreset.TC.getValue(cycleRequestDTO.teamName()), cycleRequestDTO.tagName(), cycleRequestDTO.tagColor());
                        cycleService.createByTag(KeyPreset.TC.getValue(cycleRequestDTO.teamName()), cycleRequestDTO.title(), cycleRequestDTO.content(), cycleRequestDTO.startDate(), cycleRequestDTO.endDate(), cycleTag1);
                    } else {
                        cycleService.createByTag(KeyPreset.TC.getValue(cycleRequestDTO.teamName()), cycleRequestDTO.title(), cycleRequestDTO.content(), cycleRequestDTO.startDate(), cycleRequestDTO.endDate(), cycleTag);
                    }
                }
                break;
            default:
                throw new NotAllowedException("필터를 선택해주세요.");
        }
    }

    @Transactional
    public CycleDTO updatePersonalCycle(String username, int status, Long id, CycleRequestDTO cycleRequestDTO) {
        Cycle cycle = cycleService.findById(id);
        if (cycleRequestDTO.title() == null || cycleRequestDTO.title().isEmpty()) {
            throw new NotAllowedException("제목을 입력해주세요.");
        } else if (cycleRequestDTO.content() == null || cycleRequestDTO.content().isEmpty()) {
            throw new NotAllowedException("내용을 입력해주세요.");
        } else if (cycleRequestDTO.startDate() == null) {
            throw new NotAllowedException("시작 시간을 입력해주세요.");
        } else if (cycleRequestDTO.endDate() == null) {
            throw new NotAllowedException("종료 시간을 입력해주세요.");
        } else if (cycleRequestDTO.endDate().isBefore(cycleRequestDTO.startDate()) || cycleRequestDTO.startDate().equals(cycleRequestDTO.endDate())) {
            throw new NotAllowedException("시간 설정을 다시 해주세요.");
        } else if (cycleRequestDTO.tagName() != null && cycleRequestDTO.tagColor() == null) {
            throw new NotAllowedException("색상 설정을 다시 해주세요.");
        } else if (cycleRequestDTO.tagName() == null && cycleRequestDTO.tagColor() != null) {
            throw new NotAllowedException("태그없이 색상설정은 할수없습니다.");
        }
        SiteUser user = userService.get(username);
        switch (status) {
            case 0:
                if (!cycle.getK().equals(KeyPreset.UC.getValue(user.getUsername()))) {
                    throw new NotAllowedException("접근 권한이 없습니다.");
                }
                if (cycleRequestDTO.tagName() == null || cycleRequestDTO.tagName().isEmpty()) {
                    if (cycle.getTag() != null) {
                        return getCycleDTO(cycleService.upDateAndDeleteTag(cycle, cycleRequestDTO));
                    }
                    return getCycleDTO(cycleService.upDate(cycle, cycleRequestDTO));
                } else {
                    CycleTag cycleTag = cycleTagService.findByName(KeyPreset.UC.getValue(user.getUsername()), cycleRequestDTO.tagName());
                    if (cycleTag != null) {
                        return getCycleDTO(cycleService.upDateToTag(cycle, cycleRequestDTO, cycleTag));
                    } else {
                        CycleTag cycleTag1 = cycleTagService.create(KeyPreset.UC.getValue(user.getUsername()), cycleRequestDTO.tagName(), cycleRequestDTO.tagColor());
                        return getCycleDTO(cycleService.upDateToTag(cycle, cycleRequestDTO, cycleTag1));
                    }
                }

            case 1:
                if (!cycle.getK().equals(KeyPreset.DC.getValue(user.getDepartment().getName()))) {
                    throw new NotAllowedException("접근 권한이 없습니다.");
                }
                if (cycleRequestDTO.tagName() == null || cycleRequestDTO.tagName().isEmpty()) {
                    if (cycle.getTag() != null) {
                        return getCycleDTO(cycleService.upDateAndDeleteTag(cycle, cycleRequestDTO));
                    }
                    return getCycleDTO(cycleService.upDate(cycle, cycleRequestDTO));
                } else {
                    CycleTag cycleTag = cycleTagService.findByName(KeyPreset.DC.getValue(user.getDepartment().getName()), cycleRequestDTO.tagName());
                    if (cycleTag != null) {
                        return getCycleDTO(cycleService.upDateToTag(cycle, cycleRequestDTO, cycleTag));
                    } else {
                        CycleTag cycleTag1 = cycleTagService.create(KeyPreset.DC.getValue(user.getDepartment().getName()), cycleRequestDTO.tagName(), cycleRequestDTO.tagColor());
                        return getCycleDTO(cycleService.upDateToTag(cycle, cycleRequestDTO, cycleTag1));
                    }
                }

            case 2:
                if (!cycle.getK().equals(KeyPreset.TC.getValue(cycleRequestDTO.teamName()))) {
                    throw new NotAllowedException("접근 권한이 없습니다.");
                }
                if (cycleRequestDTO.tagName() == null || cycleRequestDTO.tagName().isEmpty()) {
                    if (cycle.getTag() != null) {
                        return getCycleDTO(cycleService.upDateAndDeleteTag(cycle, cycleRequestDTO));
                    }
                    return getCycleDTO(cycleService.upDate(cycle, cycleRequestDTO));
                } else {
                    CycleTag cycleTag = cycleTagService.findByName(KeyPreset.TC.getValue(cycleRequestDTO.teamName()), cycleRequestDTO.tagName());
                    if (cycleTag != null) {
                        return getCycleDTO(cycleService.upDateToTag(cycle, cycleRequestDTO, cycleTag));
                    } else {
                        CycleTag cycleTag1 = cycleTagService.create(KeyPreset.TC.getValue(cycleRequestDTO.teamName()), cycleRequestDTO.tagName(), cycleRequestDTO.tagColor());
                        return getCycleDTO(cycleService.upDateToTag(cycle, cycleRequestDTO, cycleTag1));
                    }
                }
            default:
                throw new NotAllowedException("필터를 선택해주세요.");
        }

    }

    @Transactional
    public void deleteCycle(Long id) {
        cycleService.delete(cycleService.findById(id));
    }

    @Transactional
    public List<CycleResponseDTO> getCycle(String username, int status, LocalDateTime startDate, LocalDateTime endDate) {
        SiteUser user = userService.get(username);
        List<CycleResponseDTO> cycleResponseDTOList = new ArrayList<>();
        List<CycleDTO> cycleDTOList = new ArrayList<>();
        switch (status) {
            case 0:
                List<Cycle> cycleList = cycleService.myMonthCycle(KeyPreset.UC.getValue(user.getUsername()), startDate, endDate);
                do {
                    boolean holiday = false;
                    String holidayTitle = "";
                    for (Cycle cycle : cycleList) {
                        if (startDate.getDayOfMonth() == cycle.getStartDate().getDayOfMonth() || cycle.getEndDate().getDayOfMonth() == startDate.getDayOfMonth()) {
                            if (cycle.getTag() != null) {
                                CycleDTO cycleDTO = CycleDTO.builder().id(cycle.getId()).title(cycle.getTitle()).content(cycle.getContent()).startDate(dateTimeTransfer(cycle.getStartDate())).endDate(dateTimeTransfer(cycle.getEndDate())).tag(CycleTagDTO.builder().id(cycle.getTag().getId()).name(cycle.getTag().getName()).color(cycle.getTag().getColor()).build()).build();
                                cycleDTOList.add(cycleDTO);
                            } else {
                                CycleDTO cycleDTO = CycleDTO.builder().id(cycle.getId()).title(cycle.getTitle()).content(cycle.getContent()).startDate(dateTimeTransfer(cycle.getStartDate())).endDate(dateTimeTransfer(cycle.getEndDate())).tag(CycleTagDTO.builder().id(null).name(null).color(null).build()).build();
                                cycleDTOList.add(cycleDTO);
                            }
                        }
                    }
                    if (holidayService.getHoliday(startDate.toLocalDate()) != null) {
                        Holiday holiday1 = holidayService.getHoliday(startDate.toLocalDate());
                        holiday = true;
                        holidayTitle = holiday1.getTitle();
                    }
                    CycleResponseDTO cycleResponseDTO = CycleResponseDTO.builder().cycleDTOList(new ArrayList<>(cycleDTOList)).holiday(holiday).holidayTitle(holidayTitle).build();
                    cycleResponseDTOList.add(cycleResponseDTO);
                    cycleDTOList.clear();
                } while (!(startDate = startDate.plusDays(1)).isAfter(endDate));
                return cycleResponseDTOList;

            case 1:
                if (user.getDepartment() == null)
                    return new ArrayList<>();
                List<Cycle> cycleList1 = cycleService.myMonthCycle(KeyPreset.DC.getValue(user.getDepartment().getName()), startDate, endDate);
                do {
                    boolean holiday = false;
                    String holidayTitle = "";
                    for (Cycle cycle : cycleList1) {
                        if (startDate.getDayOfMonth() == cycle.getStartDate().getDayOfMonth() || cycle.getEndDate().getDayOfMonth() == startDate.getDayOfMonth()) {
                            if (cycle.getTag() != null) {
                                CycleDTO cycleDTO = CycleDTO.builder().id(cycle.getId()).title(cycle.getTitle()).content(cycle.getContent()).startDate(dateTimeTransfer(cycle.getStartDate())).endDate(dateTimeTransfer(cycle.getEndDate())).tag(CycleTagDTO.builder().id(cycle.getTag().getId()).name(cycle.getTag().getName()).color(cycle.getTag().getColor()).build()).build();
                                cycleDTOList.add(cycleDTO);
                            } else {
                                CycleDTO cycleDTO = CycleDTO.builder().id(cycle.getId()).title(cycle.getTitle()).content(cycle.getContent()).startDate(dateTimeTransfer(cycle.getStartDate())).endDate(dateTimeTransfer(cycle.getEndDate())).tag(CycleTagDTO.builder().id(null).name(null).color(null).build()).build();
                                cycleDTOList.add(cycleDTO);
                            }
                        }
                    }
                    if (holidayService.getHoliday(startDate.toLocalDate()) != null) {
                        Holiday holiday1 = holidayService.getHoliday(startDate.toLocalDate());
                        holiday = true;
                        holidayTitle = holiday1.getTitle();
                    }
                    CycleResponseDTO cycleResponseDTO = CycleResponseDTO.builder().cycleDTOList(new ArrayList<>(cycleDTOList)).holiday(holiday).holidayTitle(holidayTitle).build();
                    cycleResponseDTOList.add(cycleResponseDTO);
                    cycleDTOList.clear();
                } while (!(startDate = startDate.plusDays(1)).isAfter(endDate));
                return cycleResponseDTOList;
            case 2:
                if (teamPeopleService.findMyTeam(user).isEmpty())
                    return new ArrayList<>();
                List<String> myTeamName = teamPeopleService.findMyTeamNameList(user);
                List<Cycle> cycleList2 = new ArrayList<>();
                for (String name : myTeamName) {
                    List<Cycle> temaCycleList = cycleService.myMonthCycle(KeyPreset.TC.getValue(name), startDate, endDate);
                    cycleList2.addAll(temaCycleList);
                }
                do {
                    boolean holiday = false;
                    String holidayTitle = "";
                    for (Cycle cycle : cycleList2) {
                        if (startDate.getDayOfMonth() == cycle.getStartDate().getDayOfMonth() || cycle.getEndDate().getDayOfMonth() == startDate.getDayOfMonth()) {
                            if (cycle.getTag() != null) {
                                CycleDTO cycleDTO = CycleDTO.builder().id(cycle.getId()).title(cycle.getTitle()).content(cycle.getContent()).startDate(dateTimeTransfer(cycle.getStartDate())).endDate(dateTimeTransfer(cycle.getEndDate())).tag(CycleTagDTO.builder().id(cycle.getTag().getId()).name(cycle.getTag().getName()).color(cycle.getTag().getColor()).build()).build();
                                cycleDTOList.add(cycleDTO);
                            } else {
                                CycleDTO cycleDTO = CycleDTO.builder().id(cycle.getId()).title(cycle.getTitle()).content(cycle.getContent()).startDate(dateTimeTransfer(cycle.getStartDate())).endDate(dateTimeTransfer(cycle.getEndDate())).tag(CycleTagDTO.builder().id(null).name(null).color(null).build()).build();
                                cycleDTOList.add(cycleDTO);
                            }
                        }
                    }
                    if (holidayService.getHoliday(startDate.toLocalDate()) != null) {
                        Holiday holiday1 = holidayService.getHoliday(startDate.toLocalDate());
                        holiday = true;
                        holidayTitle = holiday1.getTitle();
                    }
                    CycleResponseDTO cycleResponseDTO = CycleResponseDTO.builder().cycleDTOList(new ArrayList<>(cycleDTOList)).holiday(holiday).holidayTitle(holidayTitle).build();
                    cycleResponseDTOList.add(cycleResponseDTO);
                    cycleDTOList.clear();
                } while (!(startDate = startDate.plusDays(1)).isAfter(endDate));
                return cycleResponseDTOList;
            default:
                throw new NotAllowedException("필터를 선택해주세요.");
        }
    }

    @Transactional
    public CycleDTO getCycleDTO(Cycle cycle) {
        if (cycle.getTag() != null) {
            return CycleDTO.builder().id(cycle.getId()).title(cycle.getTitle()).content(cycle.getContent()).startDate(dateTimeTransfer(cycle.getStartDate())).endDate(dateTimeTransfer(cycle.getEndDate())).tag(CycleTagDTO.builder().id(cycle.getTag().getId()).name(cycle.getTag().getName()).color(cycle.getTag().getColor()).build()).build();
        } else {
            return CycleDTO.builder().id(cycle.getId()).title(cycle.getTitle()).content(cycle.getContent()).startDate(dateTimeTransfer(cycle.getStartDate())).endDate(dateTimeTransfer(cycle.getEndDate())).tag(CycleTagDTO.builder().id(null).name(null).color(null).build()).build();
        }

    }

    @Transactional
    public void deleteTag(Long id) {
        CycleTag cycleTag = cycleTagService.findById(id);
        List<Cycle> cycleList = cycleService.findTagCycle(cycleTag);
        if (cycleList.isEmpty()) {
            cycleTagService.delete(cycleTag);
        } else {
            for (Cycle cycle : cycleList) {
                cycle.setTag(null);
                cycleService.save(cycle);
            }
            cycleTagService.delete(cycleTag);
        }
    }

    @Transactional
    public List<CycleTagDTO> getTagList(String username, int status) {
        SiteUser user = userService.get(username);
        switch (status) {
            case 0:
                List<CycleTag> cycleTagList = cycleTagService.myTag(KeyPreset.UC.getValue(user.getUsername()));
                if (!cycleTagList.isEmpty()) {
                    return cycleTagList.stream().map(this::getTagDto).toList();
                } else {
                    return new ArrayList<>();
                }
            case 1:
                if (user.getDepartment() == null) {
                    return new ArrayList<>();
                }
                List<CycleTag> cycleTagListDC = cycleTagService.myTag(KeyPreset.DC.getValue(user.getDepartment().getName()));
                if (!cycleTagListDC.isEmpty()) {
                    return cycleTagListDC.stream().map(this::getTagDto).toList();
                } else {
                    return new ArrayList<>();
                }
            case 2:
                List<String> teamName = teamPeopleService.findMyTeamNameList(user);
                List<CycleTag> cycleTagListTC = new ArrayList<>();
                if (teamName.isEmpty())
                    return new ArrayList<>();
                for (String name : teamName) {
                    List<CycleTag> teamCycle = cycleTagService.myTag(KeyPreset.TC.getValue(name));
                    cycleTagListTC.addAll(teamCycle);
                }
                if (cycleTagListTC.isEmpty())
                    return new ArrayList<>();
                return cycleTagListTC.stream().map(this::getTagDto).toList();
            default:
                throw new NotAllowedException("필터를 선택해주세요.");
        }


    }

    @Transactional
    public CycleTagDTO getTagDto(CycleTag cycleTag) {
        return CycleTagDTO.builder().id(cycleTag.getId()).name(cycleTag.getName()).color(cycleTag.getColor()).build();
    }

    @Transactional
    public CycleTagDTO updateTag(Long id, CycleTagRequestDTO cycleTagRequestDTO) {
        CycleTag cycleTag = cycleTagService.findById(id);
        if (cycleTagRequestDTO.name() == null) {
            throw new NotAllowedException("태그이름을 입력해주세요.");
        } else if (cycleTagRequestDTO.color() == null) {
            throw new NotAllowedException("태그색상을 설정해주세요.");
        }
        return getTagDto(cycleTagService.updateTag(cycleTag, cycleTagRequestDTO));
    }

    @Transactional
    public Page<CycleDTO> getTagCycle(Long id, int page) {
        Pageable pageable = PageRequest.of(page, 5);
        CycleTag cycleTag = cycleTagService.findById(id);
        Page<Cycle> cycleList = cycleService.findTagCycleToPaging(cycleTag, pageable);
        return new PageImpl<>(cycleList.stream().map(this::getCycleDTO).toList(), pageable, cycleList.getTotalElements());

    }

    /*
       teamCycle
     */
// 내 팀들찾기
    public List<String> findMyTeams(String username) {
        SiteUser user = userService.get(username);
        List<String> teamName = teamPeopleService.findMyTeamNameList(user);
        if (teamName.isEmpty()) {
            return new ArrayList<>();
        }
        return teamName;

    }


    /*
     * `Approval`
     */

    @Transactional //승인 파일 업로드
    public void approvalFilesUpload(Long approvalId, List<MultipartFile> files) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        String keyValue = KeyPreset.APPROVAL_MULTI.getValue(approvalId.toString());
        MultiKey key = multiKeyService.get(keyValue).orElseGet(() -> multiKeyService.save(keyValue));
        List<String> list = new ArrayList<>();//
        for (MultipartFile file : files) {
            UUID uuid = UUID.randomUUID();
            String fileName = "/api/approval/" + approvalId.toString() + "/" + uuid.toString() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().split("\\.")[1] : "");// IMAGE
            String fileKey = KeyPreset.APPROVAL.getValue(approvalId.toString() + "_" + list.size());
            fileSystemService.save(fileKey, fileName);
            fileSystemService.save(KeyPreset.APPROVAL_ORIGIN.getValue(fileKey), file.getOriginalFilename());
            list.add(fileKey);
            File dest = new File(path + fileName);
            if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
            file.transferTo(dest);
        }
        multiKeyService.updateAll(key, list);
    }


    @Transactional // 승인 DTO 생성
    private ApprovalResponseDTO getApproval(Approval approval) {

        List<OriginFileResponseDTO> filePathList = new ArrayList<>();
        Optional<MultiKey> _multiKey = multiKeyService.get(KeyPreset.APPROVAL_MULTI.getValue(approval.getId().toString()));
        if (_multiKey.isPresent()) {
            for (String key : _multiKey.get().getKeyValues()) {
                OriginFileResponseDTO.OriginFileResponseDTOBuilder builder = OriginFileResponseDTO.builder();
                fileSystemService.get(key).ifPresent(fileSystem -> builder.value(fileSystem.getV())); // url
                fileSystemService.get(KeyPreset.APPROVAL_ORIGIN.getValue(key)).ifPresent(fileSystem -> builder.original_name(fileSystem.getV())); // original Name
                builder.key(key); // key
                filePathList.add(builder.build());
            }
        }

        List<Approver> approvers = approverService.getAll(approval);
        List<String> approverusernames = approvers.stream().map(approver -> approver.getUser().getUsername()) // 사용자 이름 추출
                .collect(Collectors.toList());
        List<Viewer> viewers = viewerService.getAll(approval);
        List<String> viewernames = viewers.stream().map(approver -> approver.getUser().getUsername()) // 사용자 이름 추출
                .collect(Collectors.toList());

        // 승인자(UserResponseDTO 리스트) 생성
        List<ApproverResponseDTO> approversUser = approverUserFind(approval, approverusernames);

        // 참고인(UserResponseDTO 리스트) 생성
        List<UserResponseDTO> viewerUser = viewerUserFind(approval, viewernames);

        // 승인 요청자(sender) 정보 생성
        UserResponseDTO senderDTO = getUserResponseDTO(approval.getSender());

        // 승인 여부 처리
        int approvalStatus = approval.getStatus().ordinal();

        List<String> readUser = approvalService.get(approval.getId()).getReadUsers();

        Long sendDate = dateTimeTransfer(approval.getCreateDate());


        return ApprovalResponseDTO.builder()
                .id(approval.getId())
                .title(approval.getTitle()).content(approval.getContent()).files(filePathList)
                .sender(senderDTO).approvers(approversUser).viewers(viewerUser)
                .approvalStatus(approvalStatus).readUsers(readUser)
                .sendDate(sendDate).build();
    }

    @Transactional // 승인자 디티오 생성
    private List<ApproverResponseDTO> approverUserFind(Approval approval, List<String> usernames) {

        List<ApproverResponseDTO> users = new ArrayList<>();

        for (String username : usernames) {
            SiteUser siteUser = userService.get(username);

            Approver approver = approverService.get(siteUser.getUsername(), approval);

            UserResponseDTO userResponseDTO = getUserResponseDTO(siteUser);

            int approverStatus = 0;
            if (approver.getApproverStatus() != null) {
                approverStatus = approver.getApproverStatus().ordinal();
            } else {
                approverStatus = -1;
            }


            Long approvalDate = dateTimeTransfer(approver.getCreateDate());

            ApproverResponseDTO approverResponseDTO = ApproverResponseDTO.builder().approver(userResponseDTO).approverStatus(approverStatus).approvalDate(approvalDate).build();


            users.add(approverResponseDTO);

        }

        for (ApproverResponseDTO approverResponseDTO : users) {
            if (approverResponseDTO.approverStatus() == 0) {
                approverService.updateApproverStatus(approval, approverResponseDTO.approver().username(), ApprovalStatus.RUNNING);

            }
            break;
        }

        return users;
    }

    @Transactional
    private List<UserResponseDTO> viewerUserFind(Approval approval, List<String> usernames) {
        List<UserResponseDTO> users = new ArrayList<>();

        for (String username : usernames) {
            SiteUser siteUser = userService.get(username);
            Viewer viewer = viewerService.get(siteUser, approval);
            SiteUser sitesUer1 = viewer.getUser();
            UserResponseDTO userResponseDTO = getUserResponseDTO(sitesUer1);
            users.add(userResponseDTO);
        }
        return users;
    }


    //기안 생성
    public ApprovalResponseDTO createApproval(ApprovalRequestDTO approvalRequestDTO, String loginUser) throws NotAllowedException {
        SiteUser sender = userService.get(loginUser);
        Approval approval = approvalService.create(approvalRequestDTO, sender);

        if (approvalRequestDTO.approversname().isEmpty()) throw new NotAllowedException("승인자를 한 명 이상 추가해주세요.");

        for (String username : approvalRequestDTO.approversname()) {
            SiteUser user = userService.get(username);
            approverService.save(user, approval);
        }

        String firstApprover = approvalRequestDTO.approversname().getFirst();
        approverService.updateApproverStatus(approval, firstApprover, ApprovalStatus.RUNNING);


        for (String username : approvalRequestDTO.viewersname()) {
            SiteUser user = userService.get(username);
            viewerService.save(user, approval);
        }


        return getApproval(approval);
    }

    // 기안 삭제
    public void deleteApproval(Long approvalId) throws NotAllowedException {

        if (approvalId == null) throw new NotAllowedException("아이디는 하나 이상 필수입니다.");

        Approval approval = approvalService.get(approvalId);
        approvalService.delete(approval);
    }

    public ApprovalResponseDTO addApproval(Long approvalId) throws NotAllowedException {
        if (approvalId == null) throw new NotAllowedException("아이디는 하나 이상 필수입니다.");
        Approval approval = approvalService.get(approvalId);

        return getApproval(approval);
    }

    public ApprovalResponseDTO addReader(Long approvalId, String username) throws NotAllowedException {
        Approval approval = approvalService.get(approvalId);
        Approval updateApproval = approvalService.addReader(approval, username);

        return getApproval(updateApproval);
    }


    public ApprovalResponseDTO acceptApprover(Long approvalId, String username, Boolean Binary) throws NotAllowedException {
        Approval approval = approvalService.get(approvalId);
        List<Approver> approvers = approval.getApprovers();
        int index = -1;
        for (int i = 0; i < approvers.size(); i++) {
            if (approvers.get(i).getUser().getUsername().equals(username)) {
                index = i;
                break;
            }
        }

        if (Binary.equals(true)) {
            // approver 나는 허용으로 변경 > 만약 내 뒤가 있다면 걔가 running 으로 변경, 없을 경우 approval 자체가 허용
            approverService.updateApproverStatus(approval, username, ApprovalStatus.ALLOW);
            if (index < approvers.size() - 1) {
                Approver nextApprover = approvers.get(index + 1);
                approverService.updateApproverStatus(approval, nextApprover.getUser().getUsername(), ApprovalStatus.RUNNING);
                if (!approval.getStatus().equals(ApprovalStatus.RUNNING)) {
                    approvalService.updateStatus(approvalId, ApprovalStatus.RUNNING);
                }
            } else {
                approvalService.updateStatus(approvalId, ApprovalStatus.ALLOW);
            }
        } else {
            // 내 뒤에는 이제 무조건 null 값으로 들어가야 한다
            // approval 자체는 반려로 변경 되어야 한다
            approverService.updateApproverStatus(approval, username, ApprovalStatus.DENY);
            for (int i = index + 1; i < approvers.size(); i++) {
                Approver nextApprover = approvers.get(i);
                approverService.updateApproverStatus(approval, nextApprover.getUser().getUsername(), null);
            }

            approvalService.updateStatus(approvalId, ApprovalStatus.DENY);
        }

        return getApproval(approval);
    }


    public Page<ApprovalResponseDTO> getApprovalList(String username, String keyword, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<Approval> approvalList = approvalService.getList(username, keyword, pageable);

        List<ApprovalResponseDTO> approvalResponseDTOS = new ArrayList<>();
        for (Approval approval : approvalList) {
            ApprovalResponseDTO approvalResponseDTO = getApproval(approval);
            approvalResponseDTOS.add(approvalResponseDTO);
        }

        return new PageImpl<>(approvalResponseDTOS, pageable, approvalList.getTotalElements());
    }

    public ApprovalResponseDTO addViewer(Long approvalId, ApprovalRequestDTO approvalRequestDTO, String username) throws NotAllowedException {

        Approval approval = approvalService.get(approvalId);

        if (!approval.getSender().getUsername().equals(username)) throw new NotAllowedException("생성자만 수정 가능합니다.");

        List<Viewer> currentViewers = approval.getViewers();
        Set<String> currentViewerUsernames = currentViewers.stream()
                .map(viewer -> viewer.getUser().getUsername())
                .collect(Collectors.toSet());
        Set<String> newViewerUsernames = new HashSet<>(approvalRequestDTO.viewersname());

        // 새로운 사용자 추가
        for (String newUsername : newViewerUsernames) {
            if (!currentViewerUsernames.contains(newUsername)) {
                SiteUser user = userService.get(newUsername);
                viewerService.save(user, approval);
            }
        }

        // 더 이상 목록에 없는 기존 사용자 삭제
        currentViewers.removeIf(viewer -> !newViewerUsernames.contains(viewer.getUser().getUsername()));

        approvalService.save(approval); // 변경사항 저장
        return getApproval(approval); // 업데이트된 Approval 객체 반환
    }

    /*
     * Storage
     */
    public void createFolder(String location, String base) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        File baseFolder = new File(path + base);
        long size = FileOrder.getSize(baseFolder);
        if (size > 10737418240L) throw new NotAllowedException("storage");
        File folder = new File(path + location + "/새폴더");
        if (!folder.exists()) folder.mkdirs();
        else for (int i = 0; ; i++) {
            folder = new File(path + location + "/새폴더 (" + i + ")");
            if (!folder.exists()) {
                folder.mkdirs();
                break;
            }
        }
    }

    public Page<FileResponseDTO> getStorageFiles(String location, int page, FileType type, FileOrder order, String keyword) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        Pageable pageable = PageRequest.of(page, 15);

        File file = new File(path + location);
        if (!file.exists()) file.mkdirs();
        if (!file.isDirectory()) throw new NotAllowedException("not folder");
        Stream<File> stream;
        if (keyword != null && !keyword.isBlank()) {
            List<File> list = new ArrayList<>();
            for (File child : file.listFiles())
                addAllFiles(list, child, keyword);
            stream = list.stream().sorted(order.getComparator());
        } else

            stream = Arrays.stream(file.listFiles()).sorted(order.getComparator());


        if (type != null) stream = stream.filter(type::isAllow);
        List<File> files = stream.toList();
        long total = files.size();
        stream = files.stream().skip(page * 15L).limit(15);
        List<FileResponseDTO> list = stream.map(f -> {
            try {
                return transferFileToDTO(f);
            } catch (IOException ignored) {
                return null;
            }
        }).toList();
        return new PageImpl<>(list, pageable, total);
    }

    public FileResponseDTO getStorageFile(String location) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        File file = new File(path + location);
        if (!file.exists()) throw new DataNotFoundException("file not found");
        return transferFileToDTO(file);
    }

    public List<FolderResponseDTO> getFileFolders(String location) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        File file = new File(path + location);
        if (file.exists()) file.createNewFile();
        List<FolderResponseDTO> list = new ArrayList<>();
        if (file.isDirectory()) for (File child : file.listFiles())
            if (child.isDirectory()) list.add(transferFolderToDTO(child));
        return list;
    }

    public Resource getFiles(String username, String name, List<String> urls) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        if (urls.size() == 1 && !new File(path + urls.getFirst()).isDirectory())
            return new UrlResource(Paths.get(path + urls.getFirst()).toUri());
        else {
            String zipUrl = path + "/api/user/" + username + "/download_temp/" + name + ".zip";
            File zipFile = new File(zipUrl);
            if (!zipFile.getParentFile().exists())
                zipFile.getParentFile().mkdirs();
            byte[] buf = new byte[4096];
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
                for (String url : urls) {
                    File file = new File(path + url);
                    if (file.isDirectory()) {
                        String zipEntryName = file.getName() + "/";
                        ZipEntry zipEntry = new ZipEntry(zipEntryName);
                        zipEntry.setTime(file.lastModified());
                        out.putNextEntry(zipEntry);
                        out.closeEntry();
                        zipDirectory(file, urls.size() == 1 ? "" : file.getName() + "/", out);
                    } else
                        try (FileInputStream in = new FileInputStream(file)) {
                            ZipEntry zf = new ZipEntry(file.getName());

                            out.putNextEntry(zf);
                            int len = 0;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            out.closeEntry();
                        }
                }
            }
            return new UrlResource(Paths.get(zipUrl).toUri());
        }
    }

    public static void zipDirectory(File directory, String parent, ZipOutputStream out) throws IOException {
        // 디렉토리 목록 가져오기
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            // 파일인 경우
            if (file.isFile()) {
                ZipEntry zipEntry = new ZipEntry(parent + file.getName());
                out.putNextEntry(zipEntry);
                try (FileInputStream in = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            } else if (file.isDirectory()) { // 디렉토리인 경우
                // 하위 디렉토리에 대한 ZipEntry 생성 (경로 포함)
                String zipEntryName = file.getName() + "/";
                ZipEntry zipEntry = new ZipEntry(zipEntryName);
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(zipEntry);
                out.closeEntry(); // 디렉토리 엔트리 닫기

                // 하위 디렉토리 재귀 호출
                zipDirectory(file, file.getName() + "/", out);
            }
        }
    }

    public FileUploadResponseDTO saveFile(String username, FileUploadRequestDTO uploadDTO) throws IOException, NotAllowedException {
        SiteUser user = userService.get(username);
        String base = uploadDTO.baseLocation();
        if (!base.equals("/api/user/" + username + "/storage") && (user.getDepartment() == null || (!base.equals("/api/department/" + user.getDepartment().getName() + "/storage") && (user.getRole() == null || !base.equals("/api/department/" + user.getDepartment().getName() + "/role/" + user.getRole().getName() + "/storage")))) && (user.getRole() == null || !base.equals("/api/role/" + user.getRole().getName() + "/storage")))
            throw new NotAllowedException("not allowed location");

        if (!dataStack.containsKey(username)) dataStack.put(username, new HashMap<>());
        HashMap<String, List<byte[]>> data = dataStack.get(username);
        String key = uploadDTO.key();
        if (!data.containsKey(key)) data.put(key, new ArrayList<>());
        List<byte[]> stored = data.get(key);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = Base64.decodeBase64(uploadDTO.chunk());

        byteArrayOutputStream.write(bytes);

        byte[] originBytes = byteArrayOutputStream.toByteArray();
        stored.add(originBytes);
        if (uploadDTO.index() == 0) {
            String path = HoneyBadgerApplication.getOsType().getLoc();
            File baseFolder = new File(path + uploadDTO.baseLocation());
            long size = FileOrder.getSize(baseFolder);
            if (size > 10737418240L) throw new NotAllowedException("storage");
            File file = new File(path + uploadDTO.location() + "/" + uploadDTO.name());
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (file.exists()) {
                if (uploadDTO.uploadType() == 0) {
                    file = new File(path + uploadDTO.location() + "/" + uploadDTO.name() + ".temp");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(new byte[0]);
                    fos.close();
                } else {
                    String name;
                    String pre = "";
                    String suf = "";
                    if (uploadDTO.name().contains(".")) {
                        int index = uploadDTO.name().lastIndexOf(".");
                        pre = uploadDTO.name().substring(0, index);
                        suf = uploadDTO.name().substring(index);
                    } else pre = uploadDTO.name();

                    int i = 1;
                    for (; ; i++) {
                        name = pre + " (" + i + ")" + suf;
                        File check = new File(path + uploadDTO.location() + "/" + name);
                        if (!check.exists()) break;
                    }
                    if (uploadDTO.totalIndex() == 1) {
                        file = new File(path + uploadDTO.location() + "/" + name);
                        FileOutputStream fos = new FileOutputStream(file, true);
                        for (byte[] writeBytes : stored)
                            fos.write(writeBytes);
                        fos.close();
                        stored.clear();
                        data.remove(key);
                        if (data.isEmpty()) dataStack.remove(username);
                    } else new File(path + uploadDTO.location() + "/" + name + ".temp").createNewFile();
                    return FileUploadResponseDTO.builder().key(uploadDTO.key()).name(name).index(1).build();
                }
            } else new File(path + uploadDTO.location() + "/" + uploadDTO.name() + ".temp").createNewFile();
        }
        if (stored.size() == 10 || uploadDTO.index() == uploadDTO.totalIndex() - 1) {
            String path = HoneyBadgerApplication.getOsType().getLoc();
            File baseFolder = new File(path + uploadDTO.baseLocation());
            long size = FileOrder.getSize(baseFolder);
            if (size > 10737418240L) throw new NotAllowedException("storage");
            File file = new File(path + uploadDTO.location() + "/" + uploadDTO.name() + ".temp");
            FileOutputStream fos = new FileOutputStream(file, true);
            for (byte[] writeBytes : stored)
                fos.write(writeBytes);
            fos.close();
            stored.clear();
            if (uploadDTO.index() == uploadDTO.totalIndex() - 1) {
                Files.move(file.toPath(), Paths.get(path + uploadDTO.location() + "/" + uploadDTO.name()), StandardCopyOption.REPLACE_EXISTING);
                data.remove(key);
                if (data.isEmpty()) dataStack.remove(username);
            }
        }
        return FileUploadResponseDTO.builder().key(uploadDTO.key()).index(uploadDTO.index() + 1).build();
    }

    public void cancelSaveFile(String username, String key, String location, String name) throws IOException {
        if (dataStack.containsKey(username)) {
            HashMap<String, List<byte[]>> data = dataStack.get(username);
            data.remove(key);
            if (data.isEmpty()) dataStack.remove(username);
        }
        String path = HoneyBadgerApplication.getOsType().getLoc();
        File file = new File(path + location + "/" + name + ".temp");
        if (file.exists()) file.delete();
    }

    @Async
    public void deleteFile(String url) throws IOException {
        String path = HoneyBadgerApplication.getOsType().getLoc();
        File file = new File(path + url);

        deleteFileWithFolder(file);
    }

    private void addAllFiles(List<File> list, File file, String keyword) {
        if (file.getName().toLowerCase().contains(keyword.toLowerCase())) list.add(file);
        if (file.listFiles() != null) for (File child : file.listFiles())
            addAllFiles(list, child, keyword);
    }

    private FolderResponseDTO transferFolderToDTO(File file) {
        List<FolderResponseDTO> list = new ArrayList<>();
        if (file.isDirectory()) for (File child : file.listFiles())
            if (child.isDirectory()) list.add(transferFolderToDTO(child));
        return FolderResponseDTO.builder().name(file.getName()).child(list).build();
    }

    private FileResponseDTO transferFileToDTO(File file) throws IOException {
        return FileResponseDTO.builder().name(file.getName()).type(FileType.get(file).ordinal()).createDate(((FileTime) Files.getAttribute(file.toPath(), "creationTime")).toMillis()).modifyDate(file.lastModified()).size(FileOrder.getSize(file)).url(file.getPath().replaceAll("\\\\", "/").replaceAll(HoneyBadgerApplication.getOsType().getLoc(), "")).build();
    }

}
