package com.team.HoneyBadger;

import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Enum.UserRole;
import com.team.HoneyBadger.Repository.DepartmentRepository;
import com.team.HoneyBadger.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class HoneyBadgerApplicationTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void contextLoads() {
        userRepository.save(SiteUser.builder().username("admin").password(encoder.encode("1")).phoneNumber("01055344735").name("관리자").role(UserRole.ADMIN).build());
//        for (int i = 0; i < 5; i++) {
//            Department top = departmentRepository.save(Department.builder().name("top" + i).build());
//            for (int j = 0; j < 5; j++) {
//                Department second = departmentRepository.save(Department.builder().name("second" + i + "-" + j).parent(top).build());
//                for (int k = 0; k < 5; k++) {
//                    departmentRepository.save(Department.builder().name("last" + i + "-" + j + "-" + k).parent(second).build());
//                }
//            }
//        }


    }
}
