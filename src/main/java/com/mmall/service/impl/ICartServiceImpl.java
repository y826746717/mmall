package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YangYang on 2019/2/25.
 */
@Service("iCartService")
public class ICartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if(productId == null||count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.getUserIdAndProductId(userId,productId);
        if(cart == null){
            //如果不在购物车里面，那么新增
            Cart cart2 = new Cart();
            cart2.setQuantity(count);
            cart2.setChecked(Const.Cart.CHECKED);
            cart2.setProductId(productId);
            cart2.setUserId(userId);
            cartMapper.insert(cart2);
        }else{
            //如果存在，那么就是数量相加
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //体现封装思想的关键代码，注释吊这俩句话，可以用list方法
//        CartVo cartVo = this.getCartVoLimit(userId);
//        return ServerResponse.createBySuccess(cartVo);
        return  this.list(userId);
    }


    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(productId == null||count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.getUserIdAndProductId(userId,productId);
        if(cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        //体现封装思想的关键代码，注释吊这俩句话，可以用list方法
//        CartVo cartVo = this.getCartVoLimit(userId);
//        return ServerResponse.createBySuccess(cartVo);
        return  this.list(userId);
    }

    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }else{
            cartMapper.deleteByUserIDAndProductIds(userId,productList);
            //体现封装思想的关键代码，注释吊这俩句话，可以用list方法
//            CartVo cartVo = this.getCartVoLimit(userId);
//            return ServerResponse.createBySuccess(cartVo);
            return  this.list(userId);
        }
    }

    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList =  cartMapper.getCartByUserId(userId);
        ArrayList<CartProductVo> cartProductVoList = Lists.newArrayList();
        //此购物车的总价
        BigDecimal cartTotalPrice = new BigDecimal("0");
        //如何处理精度问题
        //记住一个原则，涉及到商业计算的，一定要用Bigdecimal的String构造器
        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cart : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());
                cartProductVo.setId(cart.getId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int byLimitCount = 0;
                    if(product.getStock()>cart.getQuantity()){
                        byLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        byLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车存入有效库存，即最大库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(byLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(byLimitCount);
                    //计算总价
                    //当前购物车中某一个产品的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());
                    //计算当前购物车的总价,如果已经勾选，就增加到购物车的总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),
                            cartProductVo.getProductTotalPrice().doubleValue());

                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private Boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        int count = cartMapper.selectCartProductCheckedStatusByUserId(userId);
        return count == 0;
    }

    //全选或者全反选或单选或者取消单选
    public ServerResponse<CartVo> checkedOrUnchecked(Integer userId,Integer productId,Integer checked){
        int count = cartMapper.checkedOrUnchecked(userId, productId,checked);
        return  this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.getCartProductCount(userId));
    }
}
