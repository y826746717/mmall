package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YangYang on 2019/2/22.
 */
@Controller
@RequestMapping("/product/")
public class ProductManagerController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping(value = "save.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse save(HttpSession session, Product product){
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

    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(!iUserService.checkRoleAdmin(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }else {
            return iProductService.getDeatil(productId);
        }
    }

    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(!iUserService.checkRoleAdmin(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }else {
            return iProductService.getProductList(pageNum,pageSize);
        }
    }

    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName,Integer productId,
        @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(!iUserService.checkRoleAdmin(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }else {
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }
    }

    /**
     * springMVC的文件上传
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(!iUserService.checkRoleAdmin(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无权限操作");
        }else {
            String realPath = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, realPath);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = new HashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }
    }

    /**
     * simditor富文本上传
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "richtext_img_upload.do")
    @ResponseBody
    public Map richImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file,
                             HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录系统");
            return resultMap;
        }
        if(!iUserService.checkRoleAdmin(user).isSuccess()){
            resultMap.put("success",false);
            resultMap.put("msg","你不是管理员！没有操作权限！");
            return resultMap;
        }else {
            String realPath = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, realPath);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败！");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功！");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }
    }
}
