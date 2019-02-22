package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by YangYang on 2018/4/5.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManagerController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,@RequestParam(value ="parentId",defaultValue = "0")int parentId)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登陆");
        }
        //校验是否管理员
        if(iUserService.checkRoleAdmin(user).isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateCategoryName(HttpSession session, String categoryName,int categoryId)
    {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登陆");
        }
        //校验是否管理员
        if(iUserService.checkRoleAdmin(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryName,categoryId);
        }else {
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无此操作权限，需要管理员权限");
        }
    }

    /**
     * 查询所有的平级的节点
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getParallelCategory(HttpSession session,@RequestParam(value ="categoryId",defaultValue = "0")int categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登陆");
        }
        //校验是否管理员
        if(iUserService.checkRoleAdmin(user).isSuccess()){
            //查询子节点并且只查平级的，不递归
            return iCategoryService.getParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无此操作权限，需要管理员权限");
        }
    }

    /**
     *查询当前节点的ID ，并且递归所有的子节点的ID
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDeepCategory(HttpSession session,int categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登陆");
        }
        //校验是否管理员
        if(iUserService.checkRoleAdmin(user).isSuccess()){
            //查询当前节点的ID，并且递归所有的子节点的ID
            return iCategoryService.getDeepCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无此操作权限，需要管理员权限");
        }
    }
}
