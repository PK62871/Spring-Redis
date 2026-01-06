//package com.redis.controller;
//
//import com.redis.UserResponse;
//import com.redis.config.RedisConfig;
//import com.redis.model.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//public class UserController {
//
//    @Autowired
//    private  RedisConfig config;
//    @GetMapping("/get")
//    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestHeader("env") String env){
//        if(env.equals("Redis")){
//            return  new ResponseEntity<>(config.getAllUsers(), HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//    }
//}
