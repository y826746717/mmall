package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by YangYang on 2018/3/10.
 */
@Service("iUserService")
public class IUserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    public ServerResponse<User> login(String username, String password) {
        int count = userMapper.checkUsername(username);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //因为已经MD5加密过了，所以要传入加密后的密码
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, MD5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    public ServerResponse<String> register(User user) {

        //重用checkValid方法
        /*int count = userMapper.checkUsername(user.getUsername());
        if (count > 0) {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }*/

        ServerResponse response = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!response.isSuccess()){
            return response;
        }

        //重用checkValid方法
        /*count = userMapper.checkUsername(user.getEmail());
        if (count > 0) {
            return ServerResponse.createByErrorMessage("email已存在");
        }*/

        response = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
            return response;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int count = userMapper.insert(user);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("恭喜您，注册成功");
    }

    /**
     * 校验用户
     * @param str
     * @param type
     * @return
     */
    public ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNoneBlank(type)) {
            if (type.equals(Const.USERNAME)) {
                int count = userMapper.checkUsername(str);
                if (count > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (type.equals(Const.EMAIL)) {
                int count = userMapper.checkEmail(str);
                if (count > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 获取密码提示问题
     * @param username
     * @return
     */
    public ServerResponse<String> forgetQuestion(String username){
        ServerResponse response = this.checkValid(username,Const.USERNAME);
        if(response.isSuccess()){
            return  ServerResponse.createByErrorMessage("用户名不存在，无法获取密码提示问题");
        }
        String question =  userMapper.selectForgetQuestion(username);
        if(StringUtils.isNoneBlank(question)){
            return  ServerResponse.createBySuccess(question);
        }
        return  ServerResponse.createByErrorMessage("这个用户的密码提示问题是空的");
    }

    /**
     * 验证密码提示问题
     * @param username
     * @param question
     * @param answer
     * @return
     */
    public ServerResponse<String> forgetCheckAnswer(String username,String question ,String answer){
        int count = userMapper.forgetCheckAnswer(username,question,answer);
        if(count>0){
            String token = UUID.randomUUID().toString();
            TokenCache.setKey(Const.FORGETTOKEN_PREFIX+username,token);
            return ServerResponse.createBySuccess(token);
        }
        return ServerResponse.createBySuccessMessage("密码提示问题答案错误，请重新输入");
    }

    /**
     * 重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew ,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return  ServerResponse.createByErrorMessage("参数不存在，请输入token");
        }

        ServerResponse response = this.checkValid(username,Const.USERNAME);
        if(response.isSuccess()){
            return  ServerResponse.createByErrorMessage("用户名不存在，无法重置密码");
        }
        String cacheToken = TokenCache.getKey(Const.FORGETTOKEN_PREFIX+username);
        if(StringUtils.isBlank(cacheToken)){
            return ServerResponse.createByErrorMessage("token无效或已经过期");
        }
        if(StringUtils.equals(forgetToken,cacheToken)){
            String MD5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.forgetResetPassword(username,MD5Password);
            if(rowCount>0){
                return ServerResponse.createBySuccess("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登陆状态下的重置密码
     * @param user
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    public ServerResponse<String> resetPassword(User user,String passwordOld, String passwordNew){
        int count = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(count == 0){
            return ServerResponse.createByErrorMessage("旧密码错误，请重新输入");
        }
        //这里又犯了错误，谁他妈让你不加密的
        //user.setPassword(passwordNew);
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccess("密码更新成功");
        }else {
            return ServerResponse.createByErrorMessage("密码更新失败，可能是数据库断开连接了");
        }
    }

    /**
     * 登陆状态下更新个人用户信息
     * @param user
     * @return
     */
    public ServerResponse<User> updateInformation(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("此email以被使用，请重新输入");
        }
        User updateUser =  new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        if(updateUser!=null){
            int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
            if(updateCount>0){
                return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
            }
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    /**
     *获取当前登陆的所有信息
     * @param userId
     * @return
     */
    public ServerResponse<User> getInformation(int userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return  ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return  ServerResponse.createBySuccess(user);
    }

    /**
     * 校验当前登陆用户是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkRoleAdmin(User user){
        if(user!=null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}