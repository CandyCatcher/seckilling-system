package top.candyboy;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import top.candyboy.pojo.OrderInfo;
import top.candyboy.pojo.SeckillOrder;

@Component
@Mapper
public interface OrderDao {

    @Select("select * from seckill_order so where so.user_id = #{userId} and so.item_id = #{itemId}")
    public SeckillOrder getSeckillOrderByUserIdItemId(@Param("userId")Long userId, @Param("itemId")Long itemId);

    @Insert("insert into order_info(user_id, item_id, item_name, item_count, item_price, order_channel, status, create_date)" +
            "values(#{userId}, #{itemId}, #{itemName}, #{itemCount}, #{itemPrice}, #{orderChannel}, #{status}, #{createDate}) ")
    //获取到返回值
    //列， 扫描对象对应列的属性，结果值，插入之后获取出来，最近一次查询的id
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "select last_insert_id()")
    public Long insertOrder(OrderInfo orderInfo);

    @Insert("insert into seckill_order(user_id, order_id, item_id) values(#{userId}, #{orderId}, #{itemId})")
    public int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("Select * from order_info where id = #{orderId}")
    OrderInfo getOrderInfoById(Long orderId);
}
