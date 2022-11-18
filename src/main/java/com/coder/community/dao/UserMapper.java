package com.coder.community.dao;

import com.coder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByEmail(String email);
    User selectByName(String name);

    void updateStatus(int id,int status);
    void insertUser(User user);
    void updateHeader(int id,String headerUrl);
    void updatePassword(int id,String password);
}
