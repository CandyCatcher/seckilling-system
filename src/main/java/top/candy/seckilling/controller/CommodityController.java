package top.candy.seckilling.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import top.candy.seckilling.pojo.User;
import top.candy.seckilling.redis.CommodityKey;
import top.candy.seckilling.redis.RedisService;
import top.candy.seckilling.result.Result;
import top.candy.seckilling.service.CommodityService;
import top.candy.seckilling.service.UserService;
import top.candy.seckilling.vo.CommodityDetailVo;
import top.candy.seckilling.vo.CommodityVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class CommodityController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    CommodityService commodityService;
    UserService userService;
    RedisService redisService;
    ThymeleafViewResolver thymeleafViewResolver;
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
    public void setCommodityService(CommodityService commodityService) {
        this.commodityService = commodityService;
    }
    @Autowired
    public void setThymeleafViewResolver(ThymeleafViewResolver thymeleafViewResolver) {
        this.thymeleafViewResolver = thymeleafViewResolver;
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @RequestMapping("to_Commodity")
    public String toCommodity(Model model,
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
        return "items";
    }

    @RequestMapping(value = "to_list", produces = "text/html")
    @ResponseBody
    //@ResponseBody什么作用
    public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
        //先从redis缓存中取，动态的数据也缓存了，所以先看缓存
        String html = redisService.get(CommodityKey.getCommodityList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        List<CommodityVo> listCommodityVO = commodityService.getListCommodityVO();
        model.addAttribute("commodityList", listCommodityVO);
        //return "commodity_list";


        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //手动渲染 使用thymeleafViewResolver
        html = thymeleafViewResolver.getTemplateEngine().process("commodity_list", webContext);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(CommodityKey.getCommodityList, "", html);
        }
        return html;
    }

    //数据库中的id很少是自增的，uuid的也没有， 用的都是snowflake
    @RequestMapping(value = "to_detail1/{commodityId}", produces = "text/html")
    public String detail1(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable("commodityId")Long commodityId) {
        model.addAttribute("user", user);
        CommodityVo commodityVo = commodityService.getCommodityVoById(commodityId);
        model.addAttribute("commodity", commodityVo);

        long startDate = commodityVo.getStartDate().getTime();
        long endDate = commodityVo.getEndDate().getTime();
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
        //return "commodity_detail";
        //URL缓存，缓存时间比较短
        //先从redis缓存中取
        String html = redisService.get(CommodityKey.getCommodityDetail, "" + commodityId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //手动渲染 使用thymeleafViewResolver
        html = thymeleafViewResolver.getTemplateEngine().process("commodity_detail", webContext);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(CommodityKey.getCommodityDetail, "" + commodityId, html);
        }
        return html;
    }

    @RequestMapping(value = "to_detail/{commodityId}")
    @ResponseBody
    public Result<CommodityDetailVo> detail(HttpServletRequest request, HttpServletResponse response, User user, @PathVariable("commodityId")Long commodityId) {
        CommodityVo commodityVo = commodityService.getCommodityVoById(commodityId);
        long startDate = commodityVo.getStartDate().getTime();
        long endDate = commodityVo.getEndDate().getTime();
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
        CommodityDetailVo commodityDetailVo = new CommodityDetailVo();
        commodityDetailVo.setCommodity(commodityVo);
        commodityDetailVo.setUser(user);
        commodityDetailVo.setRemainSeconds(remainSeconds);
        commodityDetailVo.setSeckillingStatus(status);
        return Result.success(commodityDetailVo);
    }
}
