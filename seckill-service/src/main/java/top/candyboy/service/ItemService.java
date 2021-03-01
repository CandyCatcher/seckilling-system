package top.candyboy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.candyboy.ItemDao;
import top.candyboy.pojo.ItemImg;
import top.candyboy.pojo.SeckillItem;
import top.candyboy.pojo.vo.ItemVo;

import java.util.List;

@Service
public class ItemService {

    ItemDao itemDao;
    @Autowired
    private void setItemDao(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public List<ItemVo> getListItemVO() {
        return itemDao.getListItemVO();
    }

    public ItemVo getItemVoById(Long itemId) {
        return itemDao.getItemVoById(itemId);
    }

    public boolean reduceStock(ItemVo itemVo) {
        SeckillItem seckillItem = new SeckillItem();
        seckillItem.setItemId(itemVo.getId());
        return itemDao.reduceStock(seckillItem) > 0;
    }

    public List<ItemImg> getItemImgs(Long itemId) {
        return itemDao.getItemImgById(itemId);
    }
}
