package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by YangYang on 2019/2/23.
 */
@Controller
@RequestMapping("/portal/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    /**
     * 与后台不同之处就是需要判断一下状态，如果商品下架了那么就查看不到
     * @param productId
     * @return
     */
    @RequestMapping("portal_detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        return iProductService.getPortalDeatil(productId);
    }

    @RequestMapping("portal_list.do")
    @ResponseBody
    public ServerResponse<PageInfo> listProduct(@RequestParam(value = "keyword",required = false) String keyword,
        @RequestParam(value = "categoryId",required = false) Integer categoryId,
        @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
        @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.getProtalList(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
