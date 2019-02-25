package com.mmall.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YangYang on 2019/2/22.
 */
@Service("iProductService")
public class IProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    //平级调用递归方法
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 后台-产品模块新增产品或者更新产品
     * @param product
     * @return
     */
    public ServerResponse SaveOrUpdate(Product product){
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){
               String [] arr =  product.getSubImages().split(",");
                if(arr.length>0){
                    product.setMainImage(arr[0]);
                }
            }
            if(product.getId()!=null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }else {
                    return ServerResponse.createByErrorMessage("更新产品失败");
                }
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount>0){
                    return  ServerResponse.createBySuccess("新增产品成功");
                }else{
                    return ServerResponse.createByErrorMessage("新增产品失败");
                }
            }
        }
        return ServerResponse.createByErrorMessage("传入参数错误");
    }

    /**
     * 产品上下架
     * @param productId
     * @param status
     * @return
     */
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccess("修改商品状态成功");
        }else{
            return ServerResponse.createByErrorMessage("修改商品状态失败");
        }

    }

    public ServerResponse getDeatil(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product!=null){
            ProductDetailVo vo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(vo);
        }else{
            return ServerResponse.createByErrorMessage("没有查询到相关商品");
        }
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse getProductList(int pageNum,int pageSize){
        Page<Object> page = PageHelper.startPage(pageNum, pageSize);
        List<Product> list = productMapper.getProductList();
        List productList = new ArrayList();
        for(Product product :list){
            ProductListVo listVo = assembleProductListVo(product);
            productList.add(listVo);
        }
        PageInfo<Product> pageResult = new PageInfo<>(list);
        pageResult.setList(productList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        if(StringUtils.isNoneBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> list = productMapper.selectByProductNameOrProductId(productName, productId);
        if(CollectionUtils.isNotEmpty(list)){
            List productList = new ArrayList();
            for(Product product :list){
                ProductListVo listVo = assembleProductListVo(product);
                productList.add(listVo);
            }
            PageInfo<Product> pageResult = new PageInfo<>(list);
            pageResult.setList(productList);
            return ServerResponse.createBySuccess(pageResult);
        }else {
            return ServerResponse.createByErrorMessage("找不到对应的结果");
        }
    }

    /**
     * 前台查看商品明细
     * @param productId
     * @return
     */
    public ServerResponse getPortalDeatil(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }else {
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product != null) {
                if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
                    return ServerResponse.createByErrorMessage("商品已经下架或删除");
                }
                ProductDetailVo vo = assembleProductDetailVo(product);
                return ServerResponse.createBySuccess(vo);
            } else {
                return ServerResponse.createByErrorMessage("没有查询到相关商品");
            }
        }
    }

    public ServerResponse getProtalList(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy){
        if(StringUtils.isBlank(keyword)&&categoryId==null){
            return ServerResponse.createByErrorCodeMessage
                    (ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryList = new ArrayList<>();
        if(categoryId!=null){
            Product product = productMapper.selectByPrimaryKey(categoryId);
            if(product==null&&StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum, pageSize);
                ArrayList<Product> productList = Lists.newArrayList(product);
                PageInfo<Product> pageResult = new PageInfo<>(productList);
//                pageResult.setList(productList);
                return ServerResponse.createBySuccess(pageResult);
            }
            categoryList = iCategoryService.getDeepCategory(categoryId).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String [] arr = orderBy.split("_");
                PageHelper.orderBy(arr[0]+" "+ arr[1]);
            }
        }
        List<Product> productList = productMapper.selectByProductNameAndCategoryIds
                (StringUtils.isBlank(keyword)?null:keyword,categoryList.size()==0?null:categoryList);
        List<ProductListVo> productVoList = new ArrayList<ProductListVo>();
        for(Product product :productList){
            ProductListVo listVo = assembleProductListVo(product);
            productVoList.add(listVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

}
