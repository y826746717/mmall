package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

/**
 * Created by YangYang on 2019/2/22.
 */
public interface IProductService {
    public ServerResponse SaveOrUpdate(Product product);
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    //后台获取商品详情
    public ServerResponse getDeatil(Integer productId);
    public ServerResponse getProductList(int pageNum,int pageSize);
    public ServerResponse searchProduct(String productName,Integer productId,int pageNum,int pageSize);
    //前台获取商品详情
    public ServerResponse getPortalDeatil(Integer productId);
    public ServerResponse getProtalList(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy);


}
