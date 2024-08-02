package com.team.HoneyBadger;

import com.team.HoneyBadger.Entity.Cycle;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Enum.KeyPreset;
import com.team.HoneyBadger.Enum.UserRole;
import com.team.HoneyBadger.Repository.CycleRepository;
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

    @Autowired
    private CycleRepository cycleRepository;

    @Test
    void contextLoads() {
//        for (int i = 0; i < 50; i++)
//            userRepository.save(SiteUser.builder().username("user" + i).password(encoder.encode("1")).phoneNumber("011" + String.format("%04d", i) + String.format("%04d", i)).name("사원" + i).role(UserRole.STAFF).build());
        userRepository.save(SiteUser.builder().username("admin").password(encoder.encode("1")).phoneNumber("00000000000").name("관리자").role(UserRole.ADMIN).build());
        for (int i = 1; i <= 100; i++)
            userRepository.save(SiteUser.builder().username("user" + i).password(encoder.encode("1")).phoneNumber("010" + String.format("%04d", i) + String.format("%04d", i)).name("유저" + i).role(UserRole.STAFF).build());
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
    void cycle() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(5).plusHours(1);
        for (int i = 0; i < 10; i++) {
            cycleRepository.save(Cycle.builder().title("그룹일정 테스트" + i).content("그룹일정 테스트" + i).k(KeyPreset.DC.getValue("성언이에용")).startDate(startDate).endDate(endDate).tag(null).build());
            startDate = startDate.plusDays(1);
            endDate = endDate.plusDays(1);
        }
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
