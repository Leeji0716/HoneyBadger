package com.team.HoneyBadger.Service;

import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.EmailReservation;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataDuplicateException;
import com.team.HoneyBadger.Repository.EmailRepository;
import com.team.HoneyBadger.Repository.UserRepository;
import com.team.HoneyBadger.Security.CustomUserDetails;
import com.team.HoneyBadger.Security.JWT.JwtTokenProvider;
import com.team.HoneyBadger.Service.Module.EmailReceiverService;
import com.team.HoneyBadger.Service.Module.EmailService;
import com.team.HoneyBadger.Service.Module.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MultiService {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final EmailReceiverService emailReceiverService;
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final EmailReceiverService emailReservationRepository;

    // 파일을 저장할 경로 설정
    private final String uploadDir = "/path/to/upload/directory";

    /**
     * Auth
     */
    public TokenDTO checkToken(String accessToken) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        String username = null;
        if (accessToken != null && accessToken.length() > 7) {
            String token = accessToken.substring(7);
            if (this.jwtTokenProvider.validateToken(token)) {
                httpStatus = HttpStatus.OK;
                username = this.jwtTokenProvider.getUsernameFromToken(token);
            } else {
                httpStatus = HttpStatus.UNAUTHORIZED;
            }
        }
        return TokenDTO.builder().httpStatus(httpStatus).username(username).build();
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

    /*
     * Email
     */
    public Email sendEmail(String title, String content, String senderId, List<String> receiverIds, LocalDateTime sendTime, List<MultipartFile> attachments) {
        SiteUser sender = userService.get(senderId);
        Email email = emailService.save(title, content, sender, sendTime);

        for (String receiverId : receiverIds) {
            SiteUser receiver = userService.get(receiverId);
            emailReceiverService.save(email, receiver);
        }

        // 첨부 파일 저장
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                saveFile(file);
            }
        }

        return email;
    }

    @Transactional
    public void scheduleEmail(String title, String content, SiteUser sender, List<String> receivers, LocalDateTime sendTime, List<MultipartFile> attachments) {
        EmailReservation emailReservation = new EmailReservation();
        emailReservation.setTitle(title);
        emailReservation.setContent(content);
        emailReservation.setSender(sender);
        emailReservation.setSendTime(sendTime);
        emailReservation.setReceiverList(receivers);

        // 첨부 파일 저장
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                saveFile(file);
            }
        }

        emailReservationRepository.save(emailReservation);
    }

    private void saveFile(MultipartFile file) {
        try {
            String filePath = uploadDir + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public List<EmailResponseDTO> getEmailsForUser(String username) {
        List<EmailResponseDTO> list = new ArrayList<>();
        List<Email> emails = emailReceiverService.getEmailsForUser(username);
        for (Email email : emails) {
            List<String> receivers = new ArrayList<>();
            for (EmailReceiver receiver : email.getReceiverList()) {
                receivers.add(receiver.getReceiver().getUsername());
            }
            list.add(EmailResponseDTO.builder() //
                    .id(email.getId()) //
                    .title(email.getTitle()) //
                    .content(email.getContent()) //
                    .senderId(email //
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

    private EmailResponseDTO getEmail(Email email) {
        return EmailResponseDTO.builder()
                .id(email.getId())
                .title(email.getTitle())
                .senderName(email.getSender().getName())
                .build();
    }

    public void markEmailAsRead(Long emailId, String receiverId) {
        emailReceiverService.markEmailAsRead(emailId, receiverId);
    }

    @Transactional
    public void deleteEmail(Long emailId, String username) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new RuntimeException("Email not found with id: " + emailId));

        SiteUser user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Check if the user is either sender or receiver of the email
        if (email.getSender().equals(user) || email.getReceiverList().stream().anyMatch(receiver -> receiver.getReceiver().equals(user))) {
            emailRepository.delete(email);
        } else {
            throw new RuntimeException("User is not authorized to delete this email");
        }
    }
}