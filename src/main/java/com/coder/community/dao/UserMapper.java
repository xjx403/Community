package com.coder.community.dao;

import com.coder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByEmail(String email);
    User selectByName(String name);


    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
    void deleteUserById(int id);
    void deleteUserByName(String name);

}
