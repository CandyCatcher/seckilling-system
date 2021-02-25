package top.candyboy;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import top.candyboy.pojo.SeckillCommodity;
import top.candyboy.pojo.vo.CommodityVo;

import java.util.List;

@Component
@Mapper
public interface CommodityDao {
    //查询语句的书写，是和在mysql中查询一模一样的，不能写类的名称！
    @Select("select co.commodity_name commodityName, co.commodity_title commodityTitle, co.commodity_img commodityImg, co.commodity_detail commodityDetail, co.commodity_price commodityPrice, co.commodity_stock commodityStock, sc.seckill_price seckillingPrice, sc.stock_count stockCount, sc.start_date startDate, sc.end_date endDate from seckill_commodity sc left join commodity co on sc.commodity_id = co.id")
    public List<CommodityVo> getListCommodityVO();

    @Select("select co.*, sc.seckill_price, sc.stock_count, sc.start_date, sc.end_date from seckill_commodity sc left join commodity co on sc.commodity_id = co.id where sc.commodity_id = #{commodityId}")
    public CommodityVo getCommodityVoById(@Param("commodityId") Long commodityId);

    //更新一部分数据,保证商品不卖超
    @Update("update seckill_commodity sc set stock_count = stock_count -1 where sc.commodity_id = #{commodityId}")
    public int reduceStock(SeckillCommodity seckillCommodity);
}
