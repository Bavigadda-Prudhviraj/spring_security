package com.prudhviraj.security.security;

import com.prudhviraj.security.security.entities.User;
import com.prudhviraj.security.security.service.impl.JwtServiceImpl;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityApplicationTests {

	@Autowired
	JwtServiceImpl jwtService;

	Logger log = LoggerFactory.getLogger(SecurityApplicationTests.class);

	@Test
	void contextLoads() {
		User user = new User(1l,"prudhviraj","passowrd");
		log.info("contextLoads method is here");
		String token = jwtService.generateToken(user);
		log.info("Jwt Token : {}", token);

		Long id = jwtService.getUserIdFromToken(token);
		log.info("Extracted User ID from Jwt Token ID : {}", id);



	}


}
