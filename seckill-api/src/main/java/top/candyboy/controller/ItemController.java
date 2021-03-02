package top.candyboy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.candyboy.pojo.ItemImg;
import top.candyboy.pojo.User;
import top.candyboy.redis.key.ItemImgKey;
import top.candyboy.redis.key.ItemKey;
import top.candyboy.redis.RedisOperation;
import top.candyboy.result.Result;
import top.candyboy.service.ItemService;
import top.candyboy.service.UserService;
import top.candyboy.pojo.vo.ItemDetailVo;
import top.candyboy.pojo.vo.ItemVo;
import top.candyboy.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("item")
public class ItemController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    ItemService itemService;
    UserService userService;
    RedisOperation redisOperation;
    //ThymeleafViewResolver thymeleafViewResolver;
    ApplicationContext applicationContext;
    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    private void setRedisOperation(RedisOperation redisOperation) {
        this.redisOperation = redisOperation;
    }
    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }
    //@Autowired
    //public void setThymeleafViewResolver(ThymeleafViewResolver thymeleafViewResolver) {
    //    this.thymeleafViewResolver = thymeleafViewResolver;
    //}
    //@Autowired
    //public void setApplicationContext(ApplicationContext applicationContext) {
    //    this.applicationContext = applicationContext;
    //}

    /*
    分布式session就是使用redis，为啥redis就是分布式的呢？
    用户登录之后，将用户的信息以一个token区分，生成一个对象放在cookie中，然后将cookie放到缓存中

    用户需要信息的时候，就携带这个cookie里面的token去缓存中查找数据 页面请求会自动携带cookie
    那么就考虑通过这个token取得user的值
    每次切换页面什么的都从缓存中取数据
     */
    @RequestMapping("Item")
    public String toItem(Model model,
                          //HttpServletResponse response,
                          //这里是在服务端拿到token
                          //@CookieValue(value = UserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
                          //@RequestParam(value = UserService.COOKIE_NAME_TOKEN, required = false) String paramToken
                          //直接保存user就可以了
                          User user
    ) {

        /*
            //参数判断
            if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
                return "login";
            }
            //当用户登陆之后，操作其他页面的时候还需要进行判断，繁琐
            String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
            User user = userService.getUserByToken(response, token);
        */
        model.addAttribute("user", user);
        return "true";
    }

    /*
    页面缓存，之前是动态的页面，现在是前后端分离了，只考虑数据的redis缓存即可
     */
    @GetMapping(value = "/itemList")
    public Result<List<ItemVo>> list() {

        /*
        1.redis缓存中是否有
        2.  没有的话添加
            有的话直接获取
         */
        List<ItemVo>  itemVoList = new ArrayList<>();
        String itemVoListStr = redisOperation.get(ItemKey.getItemList, "", String.class);
        if (StringUtils.isBlank(itemVoListStr)) {
            itemVoList = itemService.getListItemVO();
            redisOperation.set(ItemKey.getItemList, "", JsonUtil.objectToJson(itemVoList));
        } else {
            itemVoList = JsonUtil.jsonToList(itemVoListStr, ItemVo.class);
        }
        return Result.success(itemVoList);
    }

    @GetMapping(value = "/detail/{itemId}")
    public Result<ItemDetailVo> detail(@PathVariable("itemId")Long itemId) {

        // 取缓存，取不到的话就去数据库拿数据
        ItemVo itemVo;
        String itemVoStr = redisOperation.get(ItemKey.getItemDetail, "", String.class);
        if (StringUtils.isBlank(itemVoStr)) {
            itemVo = itemService.getItemVoById(itemId);
            redisOperation.set(ItemKey.getItemDetail, "", JsonUtil.objectToJson(itemVo));
        } else {
            itemVo = JsonUtil.jsonToPojo(itemVoStr, ItemVo.class);
        }

        List<ItemImg> itemImgs = new ArrayList<>();
        String itemImgStr = redisOperation.get(ItemImgKey.getItemImg, "", String.class);
        if (StringUtils.isBlank(itemImgStr)) {
            itemImgs = itemService.getItemImgs(itemId);
            redisOperation.set(ItemImgKey.getItemImg, "", JsonUtil.objectToJson(itemImgs));
        } else {
            itemImgs = JsonUtil.jsonToList(itemImgStr, ItemImg.class);
        }

        long startDate = itemVo.getStartDate().getTime();
        long endDate = itemVo.getEndDate().getTime();
        long currentTime = System.currentTimeMillis();
        //System.out.println(startDate);
        int status = 0;
        int remainSeconds = 0;
        //秒杀还未开始
        if (currentTime < startDate) {
            status = 0;
            remainSeconds = (int) ((startDate - remainSeconds) / 1000);
        } else if (currentTime > endDate) {
            //秒杀已经结束
            status = 2;
            remainSeconds = -1;
        } else {
            //秒杀还在进行中
            status = 1;
            remainSeconds = 0;
        }
        ItemDetailVo itemDetailVo = new ItemDetailVo();
        itemDetailVo.setItemVo(itemVo);
        itemDetailVo.setItemImg(itemImgs);
        //itemDetailVo.setUser(user);
        itemDetailVo.setRemainSeconds(remainSeconds);
        itemDetailVo.setSeckillingStatus(status);
        return Result.success(itemDetailVo);
    }

    /*
    // 数据库中的id很少是自增的，uuid的也没有， 用的都是snowflake
    @RequestMapping(value = "detail1/{itemId}", produces = "text/html")
    public String detail1(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable("itemId")Long itemId) {
        model.addAttribute("user", user);
        ItemVo itemVo = itemService.getItemVoById(itemId);
        model.addAttribute("item", itemVo);

        long startDate = itemVo.getStartDate().getTime();
        long endDate = itemVo.getEndDate().getTime();
        long currentTime = System.currentTimeMillis();
        //System.out.println(currentTime);
        int status = 0;
        int remainSeconds = 0;
        //秒杀还未开始
        if (currentTime < startDate) {
            status = 0;
            remainSeconds = (int) ((startDate - remainSeconds) / 1000);
        } else if (currentTime > endDate) {
            //秒杀已经结束
            status = 2;
            remainSeconds = -1;
        } else {
            //秒杀还在进行中
            status = 1;
            remainSeconds = 0;
        }
        model.addAttribute("status", status);
        model.addAttribute("remainSeconds", remainSeconds);
        //return "item_detail";
        //URL缓存，缓存时间比较短
        //先从redis缓存中取
        String html = redisOperation.get(ItemKey.getItemDetail, "" + itemId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //手动渲染 使用thymeleafViewResolver
        html = thymeleafViewResolver.getTemplateEngine().process("item_detail", webContext);
        if (!StringUtils.isEmpty(html)) {
            redisOperation.set(ItemKey.getItemDetail, "" + itemId, html);
        }
        return html;
    }
    */

}
