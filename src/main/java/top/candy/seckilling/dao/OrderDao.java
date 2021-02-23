package top.candy.seckilling.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import top.candy.seckilling.pojo.OrderInfo;
import top.candy.seckilling.pojo.SeckillOrder;

@Component
@Mapper
public interface OrderDao {

    @Select("select * from seckill_order so where so.user_id = #{userId} and so.commodity_id = #{commodityId}")
    public SeckillOrder getSeckillOrderByUserIdCommodityId(@Param("userId")Long userId, @Param("commodityId")Long commodityId);

    @Insert("insert into order_info(user_id, commodity_id, commodity_name, commodity_count, commodity_price, order_channel, status, create_date)" +
            "values(#{userId}, #{commodityId}, #{commodityName}, #{commodityCount}, #{commodityPrice}, #{orderChannel}, #{status}, #{createDate}) ")
    //获取到返回值
    //列， 扫描对象对应列的属性，结果值，插入之后获取出来，最近一次查询的id
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "select last_insert_id()")
    public Long insertOrder(OrderInfo orderInfo);

    @Insert("insert into seckill_order(user_id, order_id, commodity_id) values(#{userId}, #{orderId}, #{commodityId})")
    public int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("Select * from order_info where id = #{orderId}")
    OrderInfo getOrderInfoById(Long orderId);
}
