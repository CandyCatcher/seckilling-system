package top.candyboy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.candyboy.CommodityDao;
import top.candyboy.pojo.SeckillCommodity;
import top.candyboy.pojo.vo.CommodityVo;

import java.util.List;

@Service
public class CommodityService {

    CommodityDao commodityDao;
    @Autowired
    private void setCommodityDao(CommodityDao commodityDao) {
        this.commodityDao = commodityDao;
    }

    public List<CommodityVo> getListCommodityVO() {
        return commodityDao.getListCommodityVO();
    }

    public CommodityVo getCommodityVoById(Long commodityId) {
        return commodityDao.getCommodityVoById(commodityId);
    }

    public boolean reduceStock(CommodityVo commodityVo) {
        SeckillCommodity seckillCommodity = new SeckillCommodity();
        seckillCommodity.setCommodityId(commodityVo.getId());
        return commodityDao.reduceStock(seckillCommodity) > 0;
    }
}
