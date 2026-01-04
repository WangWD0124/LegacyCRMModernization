package com.wwd.gateway;

import com.wwd.customer.CustomerApplication;
import com.wwd.customer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CustomerApplication.class)
class CustomerApplicationTests {

    @Autowired
    private UserService userService;

}
