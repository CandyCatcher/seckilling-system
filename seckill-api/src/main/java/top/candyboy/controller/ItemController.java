package top.candyboy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.candyboy.pojo.ItemImgs;
import top.candyboy.pojo.User;
import top.candyboy.redis.key.ItemKey;
import top.candyboy.service.RedisService;
import top.candyboy.result.Result;
import top.candyboy.service.ItemService;
import top.candyboy.service.UserService;
import top.candyboy.pojo.vo.ItemDetailVo;
import top.candyboy.pojo.vo.ItemVo;
import top.candyboy.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("item")
public class ItemController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    ItemService itemService;
    UserService userService;
    RedisService redisService;
    //ThymeleafViewResolver thymeleafViewResolver;
    ApplicationContext applicationContext;
    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    private void setRedisService(RedisService redisService) {
        this.redisService = redisService;
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

    //@RequestMapping("Item")
    //public String toItem(Model model,
    //                      //HttpServletResponse response,
    //                      //这里是在服务端拿到token
    //                      //@CookieValue(value = UserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
    //                      //@RequestParam(value = UserService.COOKIE_NAME_TOKEN, required = false) String paramToken
    //                      //直接保存user就可以了
    //                      User user
    //) {
    //
    //    /*
    //        //参数判断
    //        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
    //            return "login";
    //        }
    //        //当用户登陆之后，操作其他页面的时候还需要进行判断，繁琐
    //        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
    //        User user = userService.getUserByToken(response, token);
    //    */
    //    model.addAttribute("user", user);
    //    return "true";
    //}

    @GetMapping(value = "/itemList")
    public Result<List<ItemVo>> list() {
        /*
        1.redis缓存中是否有
        2.  没有的话添加
            有的话直接获取
         */
        List<ItemVo>  itemVoList = new ArrayList<>();
        String itemVoListStr = redisService.get(ItemKey.getItemList, "", String.class);
        if (StringUtils.isBlank(itemVoListStr)) {
            itemVoList = itemService.getListItemVO();
            //redisService.set(ItemKey.getItemList, "", JsonUtil.objectToJson(itemVoList));
        } else {
            itemVoList = JsonUtil.jsonToList(itemVoListStr, ItemVo.class);
        }
        return Result.success(itemVoList);

        //model.addAttribute("itemList", listItemVO);
        //return "item_list";
        //WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //手动渲染 使用thymeleafViewResolver
        //html = thymeleafViewResolver.getTemplateEngine().process("item_list", webContext);
        //if (!StringUtils.isEmpty(html)) {
        //    redisService.set(ItemKey.getItemList, "", );
        //}
        //return Result.success(itemVoList);
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
        String html = redisService.get(ItemKey.getItemDetail, "" + itemId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //手动渲染 使用thymeleafViewResolver
        html = thymeleafViewResolver.getTemplateEngine().process("item_detail", webContext);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(ItemKey.getItemDetail, "" + itemId, html);
        }
        return html;
    }
    */

    @GetMapping(value = "/detail/{itemId}")
    public Result<ItemDetailVo> detail(@PathVariable("itemId")Long itemId) {
        ItemVo itemVo = itemService.getItemVoById(itemId);

        List<ItemImgs> itemImgList = itemService.getItemImgList(itemId);

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
        itemDetailVo.setItemImgs(itemImgList);
        //itemDetailVo.setUser(user);
        itemDetailVo.setRemainSeconds(remainSeconds);
        itemDetailVo.setSeckillingStatus(status);
        return Result.success(itemDetailVo);
    }
}
