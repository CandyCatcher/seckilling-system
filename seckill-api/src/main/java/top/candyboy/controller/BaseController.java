package top.candyboy.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import top.candyboy.pojo.User;
import top.candyboy.pojo.vo.UserVo;
import top.candyboy.redis.key.UserKey;
import top.candyboy.redis.RedisOperation;

import java.util.UUID;

@RestController
public class BaseController {

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    public RedisOperation redisOperation;

    public UserVo convertUserVo(User user) {
         /*
        session会话其实就是设置了用户登录的状态
        实现用户的redis会话
        生成token，存到缓存中
         */
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperation.set(UserKey.token, user.getId().toString(), uniqueToken);
        // 将user和token放在一起，放到cookie中
        UserVo userVo = new UserVo();
        // 多余出来的就不会拷贝进去
        BeanUtils.copyProperties(user, userVo);
        userVo.setUserUniqueToken(uniqueToken);
        return userVo;
    }
}
