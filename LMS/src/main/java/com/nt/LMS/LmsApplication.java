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
//when two concurrent users are using the portal if one deletes a group and at the same time other user access its users what will happen ?
		//DTOs in or out dto only

		//constants
		//serialize deletion


	}

}
