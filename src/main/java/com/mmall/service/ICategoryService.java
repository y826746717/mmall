package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by YangYang on 2018/4/5.
 */
public interface ICategoryService {

    public ServerResponse addCategory(String categoryName, Integer parentId);

    public ServerResponse updateCategoryName(String categoryName,Integer categoryId);

    public ServerResponse<List<Category>> getParallelCategory(int categoryId);

    public ServerResponse getDeepCategory(Integer categoryId);
}
