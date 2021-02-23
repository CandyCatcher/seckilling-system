package top.candy.seckilling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import top.candy.seckilling.dao.CommodityDao;
import top.candy.seckilling.pojo.Commodity;
import top.candy.seckilling.pojo.SeckillCommodity;
import top.candy.seckilling.pojo.User;
import top.candy.seckilling.vo.CommodityVo;

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
