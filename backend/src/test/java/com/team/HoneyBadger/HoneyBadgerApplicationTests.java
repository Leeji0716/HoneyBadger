package com.team.HoneyBadger;

import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Enum.Role;
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
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
		userRepository.save(SiteUser.builder().username("admin3").password(passwordEncoder.encode("3")).name("name3").phoneNumber("01000000000").role(Role.USER).build());
	}

	@Test
	void contextLoads2() {
		userRepository.save(SiteUser.builder().username("admin2").password("2").name("name").phoneNumber("01000000000").build());
	}

}
