package com.team.HoneyBadger.Service.Module;


import com.team.HoneyBadger.Entity.Department;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Enum.UserRole;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SiteUser save(String username, String name, String password, UserRole role, String phoneNumber, LocalDateTime joinDate, Department department) {
        return userRepository.save(SiteUser.builder()//
                .username(username)//
                .name(name)//
                .password(passwordEncoder.encode(password))//
                .role(role)//
                .phoneNumber(phoneNumber)//
                .joinDate(joinDate)//
                .department(department)//
                .build());
    }

    @Transactional
    public void update(SiteUser user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setModifyDate(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public SiteUser update(SiteUser user, String name, UserRole role, String password, String phoneNumber, LocalDateTime joinDate, Department department) {
        if (name != null && !name.isBlank()) user.setName(name);
        if (role != null) user.setRole(role);
        if (password != null && !password.isBlank()) user.setPassword(passwordEncoder.encode(password));
        if (phoneNumber != null && !phoneNumber.isBlank()) user.setPhoneNumber(phoneNumber);
        if (joinDate != null) user.setJoinDate(joinDate);
        user.setDepartment(department);
        user.setModifyDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean isMatch(String password1, String password2) {
        return passwordEncoder.matches(password1, password2);
    }

    public SiteUser get(String username) throws IllegalArgumentException {
        return this.userRepository.findById(username).orElseThrow(() -> new DataNotFoundException("user not found"));
    }

    public Optional<SiteUser> getOptional(String username) {
        return this.userRepository.findById(username);
    }

    public List<SiteUser> getUsernameAll(String username) {
        return userRepository.findAll().stream().filter(u -> !u.getUsername().equals(username)) // username과 동일하지 않은 이름만 필터링
                .toList();
    }

    public Page<SiteUser> getUsers(String keyword, int page, int size) {
        return userRepository.getUsers(keyword, PageRequest.of(page, size));
    }

    public List<SiteUser> getUsersDepartmentIsNull() {
        return userRepository.getUsersDepartmentIsNull();
    }

    public SiteUser changeStatus(SiteUser user) {
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
}


