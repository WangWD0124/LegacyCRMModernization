package com.wwd.gateway;

import com.wwd.customer.CustomerApplication;
import com.wwd.customer.entity.User;
import com.wwd.customer.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = CustomerApplication.class)
class CustomerApplicationTests {

    @Autowired
    private UserService userService;

}
