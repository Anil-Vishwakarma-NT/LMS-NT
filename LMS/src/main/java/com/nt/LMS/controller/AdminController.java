package com.nt.LMS.controller;


import com.nt.LMS.dto.Admindto;
import com.nt.LMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    UserService userService;
    @DeleteMapping("/delemp")
    public ResponseEntity<String> delEmp(@RequestBody Admindto admindto) {
        String role = admindto.getRole();
        try {
            if (role.equals("manager")) {
                userService.managerDel(admindto.getUserId());
                return ResponseEntity.ok("Deletion successful");
            } else if (role.equals("employee")) {
                userService.empDel(admindto.getUserId());
                return ResponseEntity.ok("Deletion done!!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("Deletion done!!");

    }
}
