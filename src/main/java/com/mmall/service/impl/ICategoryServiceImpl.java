package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * Created by YangYang on 2018/4/5.
 */
@Service(value = "ICategoryService")
public class ICategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(ICategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加商品分类
     * @param categoryName
     * @param parentId
     * @return
     */
    public ServerResponse addCategory(String categoryName, Integer parentId){
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加商品分类参数错误，请重新输入");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);//只有状态为true时这个分类才是可用的

        int count = categoryMapper.insert(category);
        if(count>0){
            return  ServerResponse.createBySuccess("添加商品分类成功");
        }
        return ServerResponse.createByErrorMessage("添加商品分类失败");
    }

    /**
     * 更新商品分类名称
     * @param categoryName
     * @param categoryId
     * @return
     */
    public ServerResponse updateCategoryName(String categoryName,Integer categoryId){
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改商品分类名字参数错误，请重新输入");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int count = categoryMapper.updateByPrimaryKeySelective(category);
        if(count >0){
            return  ServerResponse.createBySuccess("更新商品分类名称成功");
        }
        return ServerResponse.createByErrorMessage("更新商品分类名称失败");
    }

    /**
     *查询所有的平级的节点
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Category>> getParallelCategory(int categoryId){
        List<Category> list = categoryMapper.getParallelCategory(categoryId);
        if(CollectionUtils.isEmpty(list)){
            logger.error("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(list);
    }

    /**
     * 查询所有的平级的节点iD，并且递归所有的子节点的ID
     * @param categoryId
     * @return
     */
    public ServerResponse getDeepCategory(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Integer> list = Lists.newArrayList();
        if(categoryId!=null){
            for(Category category :categorySet){
                list.add(category.getId());
            }
        }
        return  ServerResponse.createBySuccess(list);
    }

    /**
     * 使用递归方法进行遍历
     * @param categorySet
     * @param categoryId
     * @return
     */
    private Set<Category> findChildCategory(Set<Category> categorySet,int categoryId){
        Category resultCategory = categoryMapper.selectByPrimaryKey(categoryId);
        if(resultCategory!= null){
            categorySet.add(resultCategory);
        }
        //查找子节点
        List<Category> categoryList = categoryMapper.getParallelCategory(categoryId);
        for(Category category :categoryList){
            findChildCategory(categorySet,category.getId());
        }
        return categorySet;
    }
}
