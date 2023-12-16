package com.luckysj.chatgpt.data.infrastructure.dao;

import com.luckysj.chatgpt.data.infrastructure.po.OpenAIProductPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 商品dao
 * @create 2023/12/10 11:15:09
 */
@Mapper
public interface IOpenAIProductDao {

    /**
    * @description 根据id查询商品
    * @param productId
    * @return OpenAIProductPO 商品实体类
    * @date 2023/12/10 16:38:27
    */
    OpenAIProductPO queryProductByProductId(Integer productId);

    /**
    * @description 获取商品列表
    * @param
    * @return 商品数组信息
    * @date 2023/12/10 16:38:57
    */
    List<OpenAIProductPO> queryProductList();
}
