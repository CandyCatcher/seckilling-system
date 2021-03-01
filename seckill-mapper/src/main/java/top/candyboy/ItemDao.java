package top.candyboy;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import top.candyboy.pojo.ItemImg;
import top.candyboy.pojo.SeckillItem;
import top.candyboy.pojo.vo.ItemVo;

import java.util.List;

@Component
@Mapper
public interface ItemDao {
    //查询语句的书写，是和在mysql中查询一模一样的，不能写类的名称！
    @Select("select co.id, co.item_name itemName, co.item_title itemTitle, co.item_img itemImg, " +
            "co.item_detail itemDetail, co.item_price itemPrice, co.item_stock itemStock, " +
            "sc.seckill_price seckillingPrice, sc.stock_count stockCount, sc.start_date startDate, sc.end_date endDate " +
            "from seckill_item sc left join item co on sc.item_id = co.id")
    public List<ItemVo> getListItemVO();

    @Select("select co.id, co.item_name itemName, co.item_title itemTitle, co.item_img itemImg, " +
            "co.item_detail itemDetail, co.item_price itemPrice, co.item_stock itemStock, " +
            "sc.seckill_price seckillingPrice, sc.stock_count stockCount, sc.start_date startDate, sc.end_date endDate " +
            "from seckill_item sc left join item co on sc.item_id = co.id where sc.item_id = #{itemId}")
    public ItemVo getItemVoById(@Param("itemId") Long itemId);

    //更新一部分数据,保证商品不卖超
    @Update("update seckill_item sc set stock_count = stock_count -1 where sc.item_id = #{itemId}")
    public int reduceStock(SeckillItem seckillItem);

    @Select("select ii.id, ii.item_id itemId, ii.url, ii.sort, ii.is_main isMain, ii.created_time createTime, ii.updated_time updatedTime " +
            "from items_img ii where ii.item_id = #{itemId}")
    public List<ItemImg> getItemImgById(Long itemId);
}
