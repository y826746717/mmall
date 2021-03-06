package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart getUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> getCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteByUserIDAndProductIds(@Param("userId") Integer userId,
                                    @Param("productList") List<String> productList );

    int checkedOrUnchecked(@Param("userId") Integer userId,@Param("productId") Integer productId ,@Param("checked") Integer checked);

    int getCartProductCount(@Param("userId") Integer userId);

    //订单模块的接口
    List<Cart> selectCheckedByUserId(Integer userId);
}