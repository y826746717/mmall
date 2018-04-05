package com.mmall.service.IUserService.impl;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by YangYang on 2018/3/10.
 */

public interface IUserService {
    public ServerResponse<User> login(String username, String pssword );

    public ServerResponse<String> register(User user );

    public ServerResponse<String> checkValid(String str,String type );

    public ServerResponse<String> forgetQuestion(String username);

    public ServerResponse<String> forgetCheckAnswer(String username,String question ,String answer);

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew ,String forgetToken);

    public ServerResponse<String> resetPassword(User user,String passwordOld, String passwordNew);

    public ServerResponse<User> updateInformation(User user);

    public ServerResponse<User> getInformation(int userId);
}
