package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

/**
 * Created by YangYang on 2019/2/22.
 */
public interface IProductService {
    public ServerResponse SaveOrUpdate(Product product);
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status);
}
