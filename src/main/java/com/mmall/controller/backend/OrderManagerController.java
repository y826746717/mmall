package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by YangYang on 2019/3/10.
 */
@Controller
@RequestMapping("/backend/order")
public class OrderManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse orderList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkRoleAdmin(user).isSuccess()){
            ServerResponse response = iOrderService.manageList(pageNum, pageSize);
            return  response;
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，没有权限操作");
        }
    }


    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkRoleAdmin(user).isSuccess()){
            ServerResponse response = iOrderService.manageDetail(orderNo);
            return  response;
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，没有权限操作");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpSession session, long orderNo,
                                      @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                      @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkRoleAdmin(user).isSuccess()){
            ServerResponse response = iOrderService.manageSearch(orderNo,pageNum,pageSize);
            return  response;
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，没有权限操作");
        }
    }

    //f发货
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> sendGood(HttpSession session, long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkRoleAdmin(user).isSuccess()){
            ServerResponse response = iOrderService.sendGood(orderNo);
            return  response;
        }else {
            return ServerResponse.createByErrorMessage("您不是管理员，没有权限操作");
        }
    }

}
