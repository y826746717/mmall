package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username,@Param("password") String password );

    String selectForgetQuestion(String username);

    int forgetCheckAnswer(@Param("username") String username,@Param("question") String question ,@Param("answer") String answer);

    int forgetResetPassword(@Param("username") String username,@Param("password") String passwordNew);

    int checkPassword(@Param("userID")Integer userID,@Param("password")String password);

    int checkEmailByUserId(@Param("userID")Integer userID,@Param("email")String email);
}