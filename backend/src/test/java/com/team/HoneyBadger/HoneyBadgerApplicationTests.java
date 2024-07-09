package com.team.HoneyBadger;

import com.team.HoneyBadger.Entity.SiteUser;
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
	@Test
	void contextLoads() {
			userRepository.save(SiteUser.builder().username("admin2").password(encoder.encode("2")).phoneNumber("01022222222").name("이름2").build());
	}
}
