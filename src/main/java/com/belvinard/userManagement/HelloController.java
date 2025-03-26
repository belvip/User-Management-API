package com.belvinard.userManagement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello UserManagement";
    }


    @GetMapping("/user")
    public String user() {
        return "Hello User";
    }

    @GetMapping("/contact")
    public String sayContact() {
        return "Hello contact";
    }
}