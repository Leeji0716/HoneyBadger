package com.team.HoneyBadger;

import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Enum.UserRole;
import com.team.HoneyBadger.Repository.DepartmentRepository;
import com.team.HoneyBadger.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

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
//        for (int i = 0; i < 50; i++)
//            userRepository.save(SiteUser.builder().username("user" + i).password(encoder.encode("1")).phoneNumber("011" + String.format("%d", i) + String.format("%04d", i)).name("사원" + i).role(UserRole.STAFF).build());
        userRepository.save(SiteUser.builder().username("user5").password(encoder.encode("1")).phoneNumber("01011111111" ).name("사원5").role(UserRole.STAFF).build());
//        userRepository.save(SiteUser.builder().username("admin5").password(encoder.encode("1")).phoneNumber("01022222343").name("직원").role(UserRole.STAFF).build());
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

    @Test
    void getMyCycle() {
        System.out.println("for");
        {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now().plusDays(5);

            for (; !startDate.isAfter(endDate); startDate = startDate.plusDays(1)) {
                System.out.println(startDate.toString());
            }
        }
        System.out.println("while");
        {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now().plusDays(5);

            while (!(startDate = startDate.plusDays(1)).isAfter(endDate)) {
                System.out.println(startDate.toString());
            }
        }


    }
}
