package top.candyboy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.candyboy.pojo.User;
import top.candyboy.rabbitmq.MQSender;
import top.candyboy.rabbitmq.SeckillMessage;
import top.candyboy.service.RedisService;
import top.candyboy.redis.key.UserKey;
import top.candyboy.result.CodeMsg;
import top.candyboy.result.Result;
import top.candyboy.service.SeckillService;
import top.candyboy.service.UserService;

@RestController
public class DemoController {

    UserService userService;
    RedisService redisService;
    MQSender sender;
    SeckillService seckillService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }
    @Autowired
    public void setSender(MQSender sender) {
        this.sender = sender;
    }
    @Autowired
    public void setSeckillService(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    @GetMapping("/getUser")
    public Result<User> getUserById() {
        User user = userService.getUserById(Long.valueOf("18654871682"));
        return Result.success(user);
    }

    @GetMapping("/helloSuccess")
    Result<String> home() {
        return Result.success("hello spring");
    }

    @GetMapping("/helloError")
    public Result<String> hello2() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "springboot");
        return "/hello";
    }


//    @GetMapping("/tx")
//    public Result<Boolean> tx() {
//        userService.tx();
//        return Result.success(true);
//    }


//    RedisConfig redisConfig;
//    @Autowired
//    public void setRedisConfig(RedisConfig redisConfig) {
//        this.redisConfig = redisConfig;
//    }
//
//    @GetMapping("/redis")
//    public String redis() {
//        System.out.println(redisConfig.getHost());
//        System.out.println(redisConfig.getJedis().getPool().get("max-idle"));
//        return "hello";
//    }

    /*
    通用缓存key的开发
    模版模式
    接口      实现一些契约
    抽象类     实现一些通用方法
    实用类     实现特有方法
     */
    //@GetMapping("/redis/set")
    //public Result<Boolean> set() {
    //    User user = new User();
    //    user.setNickname("lingLing");
    //    //使用一个前缀
    //    redisService.set(UserKey.getByName, "123", user);
    //    return Result.success(true);
    //}
    //
    //@GetMapping("/redis/get")
    //public Result<User> get() {
    //    User user = redisService.get(UserKey.getByName, "123", User.class);
    //    //System.out.println(user);
    //    return Result.success(user);
    //}

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
        User user = new User();
        user.setId((long) 1234567);
        user.setPassword("1234");
        user.setNickname("qwe");
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setItemId((long) 2345678);
        sender.sendMessage(seckillMessage);
        return Result.success("hello");
    }

    @RequestMapping(value = "/demo")
    @ResponseBody
    //public Result<OrderInfo> secSkill(Model model, User user, @RequestParam("itemId")Long itemId) {
    public Result<String> secSkillResult() {
        //获取状态
        /*
            orderId: 秒杀成功
            -1: 秒杀失败
            0: 排队成功
         */

        Long result = seckillService.getSeckillResult(Long.valueOf("18654871682"), Long.valueOf("1"));
        return Result.success("success");
    }

/*
    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> mqTopic() {
        sender.sendTopic("hello world");
        return Result.success("hello");
    }

    //swagger
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> mqFanout() {
        sender.sendFanout("hello world---fanOut  ");
        return Result.success("hello");
    }

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> mqHeader() {
        sender.sendHeader("hello world---header");
        return Result.success("hello");
    }*/
}