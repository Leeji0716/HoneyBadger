package com.team.HoneyBadger.Service.Module;


import com.team.HoneyBadger.DTO.SignupRequestDTO;
import com.team.HoneyBadger.Entity.Department;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Enum.Role;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional
    public void update(SiteUser user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setModifyDate(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public SiteUser update(SiteUser user, String name, Role role, String password, String phoneNumber, LocalDateTime joinDate, Department department) {
        if (name != null && !name.isBlank()) user.setName(name);
        if (role != null) user.setRole(role);
        if (password != null && !password.isBlank()) user.setPassword(passwordEncoder.encode(password));
        if (phoneNumber != null && !phoneNumber.isBlank()) user.setPhoneNumber(phoneNumber);
        if (joinDate != null) user.setJoinDate(joinDate);
        if (department != null) user.setDepartment(department);
        user.setModifyDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean isMatch(String password1, String password2) {
        return passwordEncoder.matches(password1, password2);
    }

    public SiteUser get(String username) throws IllegalArgumentException {
        return this.userRepository.findById(username).orElseThrow(() -> new DataNotFoundException("user not found"));
    }

    public List<SiteUser> getUsernameAll(String username) {
        return userRepository.findAll().stream().filter(u -> !u.getUsername().equals(username)) // username과 동일하지 않은 이름만 필터링
                .toList();
    }
}


