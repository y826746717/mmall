package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by YangYang on 2019/2/22.
 */
@Controller
@RequestMapping("/manager/product/")
public class ProductManagerController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "save.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        /**if(user.getRole().intValue()!=Const.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.)
        }*/
        if(!iUserService.checkRoleAdmin(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }else {
            return iProductService.SaveOrUpdate(product);
        }
    }

    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        /**if(user.getRole().intValue()!=Const.Role.ROLE_ADMIN){
         return ServerResponse.createByErrorCodeMessage(ResponseCode.)
         }*/
        if(!iUserService.checkRoleAdmin(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }else {
            return iProductService.setSaleStatus(productId,status);
        }
    }

}
