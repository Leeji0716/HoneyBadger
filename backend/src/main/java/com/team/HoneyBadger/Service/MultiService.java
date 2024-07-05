package com.team.HoneyBadger.Service;

import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Entity.Email;
import com.team.HoneyBadger.Entity.EmailReceiver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataDuplicateException;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MultiService {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final EmailReceiverService emailReceiverService;

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
            } else httpStatus = HttpStatus.UNAUTHORIZED;
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
                    .receiverIds(receivers) //
                    .build());
        }
        return list;
    }

    public void markEmailAsRead(Long emailId, String receiverId) {
        emailReceiverService.markEmailAsRead(emailId, receiverId);
    }
}