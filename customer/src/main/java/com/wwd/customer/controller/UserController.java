package com.wwd.customer.controller;

import com.wwd.customer.entity.User;
import com.wwd.customer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.controller.UserController
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-10-12
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-10-12     wangwd7          v1.0.0               创建
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 获取所有用户
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    // 根据用户名获取用户
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    // 创建用户
    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        boolean success = userService.save(user);
        if (success) {
            return ResponseEntity.ok("用户创建成功");
        } else {
            return ResponseEntity.badRequest().body("用户创建失败");
        }
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        user.setId(id);
        boolean success = userService.update(user);
        if (success) {
            return ResponseEntity.ok("用户更新成功");
        } else {
            return ResponseEntity.badRequest().body("用户更新失败");
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean success = userService.delete(id);
        if (success) {
            return ResponseEntity.ok("用户删除成功");
        } else {
            return ResponseEntity.badRequest().body("用户删除失败");
        }
    }

    // 分页查询用户
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getUsersByPage(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Map<String, Object> result = userService.findByPage(username, status, page, size);
        return ResponseEntity.ok(result);
    }
}
