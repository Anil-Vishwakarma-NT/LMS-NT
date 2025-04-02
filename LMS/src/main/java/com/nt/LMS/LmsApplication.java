package com.nt.LMS;

import com.nt.LMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LmsApplication {

	public static void main(String[] args) {

		SpringApplication.run(LmsApplication.class, args);
		System.out.println("welcome.................");
//
//		UserService userService = new UserService();
//		userService.empDel(1L);
//		System.out.println("User deleted !!!!");





	}

}
