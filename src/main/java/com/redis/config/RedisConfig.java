package com.redis.config;

import com.redis.UserResponse;
import com.redis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class RedisConfig {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private  RedisTemplate<String,String> redisTemplate2;

    @PostConstruct
    public void test(){
        redisTemplate.opsForList().rightPush("users",
                "Sumit,Lal,su@gmail.com,Patna");

        User user1 = new User("Prabhakar","Kumar","pk@gmail.com","Mumbai");
        User user2 = new User("Sumit","Lal","su@gmail.com","Patna");
        User user3 = new User("Ajay","Paswan","ap@gmail.com","Ranchi");
        User user4 = new User("Rohit","Raj","rr@gmail.com","Benglore");
        User user5 = new User("Vishal","Pandey","vp@gmail.com","HYD");


        save("user:1",user1);
        save("user:2",user2);
        save("user:3",user3);
        save("user:4",user4);
        save("user:5",user5);

    }

    private void save(String key,User user){
        redisTemplate.opsForHash().putAll(key,toMap(user));
    }

    private Map<String,String> toMap(User user){
        Map<String,String> map = new HashMap<>();
        map.put("fname",user.getFname());
        map.put("lname",user.getLname());
        map.put("email", user.getEmail());
        map.put("city",user.getCity());
        return  map;
    }

    public List<UserResponse> getAllUsers(){

        List<UserResponse> list = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("user:*");

        if(keys == null){
            return list;
        }

        for(String key :keys){

            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

            if(!entries.isEmpty()){
                list.add(mapToUser(entries));
            }
        }
        return list;
    }

    private UserResponse mapToUser(Map<Object, Object> map) {
        UserResponse user = new UserResponse();
        user.setFname((String) map.get("fname"));
        user.setLname((String) map.get("lname"));
        user.setEmail((String) map.get("email"));
        user.setCity((String) map.get("city"));
        return user;
    }
}
