package com.example.authentication.service;

import com.example.authentication.dto.DtoUser;
import com.example.authentication.dto.DtoUserIU;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DtoUser registerUser(DtoUserIU user) {

        try {

            DtoUser dtoUser = new DtoUser();
            User newUser = new User();

            BeanUtils.copyProperties(user, newUser);

            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(newUser);

            redisUtil.set(0, "user:" + savedUser.getId(), savedUser, null);

            BeanUtils.copyProperties(savedUser, dtoUser);

            return dtoUser;

        } catch (RuntimeException e) {

            System.out.println(e.getMessage());
            throw new RuntimeException("Kullanıcı kaydı başarısız:" + e.getMessage());

        }

    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
