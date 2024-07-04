package com.team.HoneyBadger.Service.Module;


import com.team.HoneyBadger.DTO.SignupRequestDTO;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void save(SignupRequestDTO signupRequestDTO) {
        userRepository.save(SiteUser.builder()
//                .username(signupRequestDTO.getUsername())
//                .name(signupRequestDTO.getName())
//                .password(passwordEncoder.encode(signupRequestDTO.getPassword()))
//                .nickname(signupRequestDTO.getNickname())
//                .email(signupRequestDTO.getEmail())
//                .gender(Gender.values()[signupRequestDTO.getGender()])
//                .role(UserRole.values()[signupRequestDTO.getRole()])
//                .birthday(signupRequestDTO.getBirthday())
//                .phoneNumber(signupRequestDTO.getPhoneNumber())
                .build());
    }

    public boolean isMatch(String password1, String password2) {
        return passwordEncoder.matches(password1, password2);
    }

    public SiteUser get(String username) throws IllegalArgumentException {
        return this.userRepository.findById(username).orElseThrow(() -> new DataNotFoundException("user not found"));
    }
}


