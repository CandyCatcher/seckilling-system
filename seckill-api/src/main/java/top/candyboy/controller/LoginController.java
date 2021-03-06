package top.candyboy.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.candyboy.UserDao;
import top.candyboy.exception.GlobalException;
import top.candyboy.pojo.User;
import top.candyboy.pojo.vo.UserVo;
import top.candyboy.redis.RedisOperation;
import top.candyboy.redis.key.UserKey;
import top.candyboy.result.CodeMsg;
import top.candyboy.result.Result;
import top.candyboy.service.UserService;
import top.candyboy.pojo.vo.LoginVo;
import top.candyboy.util.CookieUtils;
import top.candyboy.util.JsonUtil;
import top.candyboy.util.MD5Util;
import top.candyboy.util.UUIDUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

@RestController
public class LoginController extends BaseController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/dologin")
    public Result<Boolean> doLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginVo loginVo) {

        log.info(loginVo.toString());

        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        //输入的手机号和密码
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //判断手机号在数据库是否存在
        User user = userService.getUserById(Long.valueOf(mobile));

        if (user == null) {
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码是否正确,需要将密码转换为md5格式的
        String userPassword = user.getPassword();
        String userSalt = user.getSalt();
        String calPass = MD5Util.formPassToDBPass(password, userSalt);
        if (!calPass.equals(userPassword)) {
            return Result.error(CodeMsg.PASSWORD_ERROR);
        }
        /*
        登录成功之后给这个用户创建一个类似于sessionId的东西标示这个用户，我们称之为token
        写到cookie当中，传递给客户端。客户端在随后访问当中都在cookie中上传token，
        服务端都根据token取到用户session信息，cookie是存放在reids中的
         */
        //生成cookie
        UserVo userVo = convertUserVo(user);

        CookieUtils.setCookie(request, response, "user", JsonUtil.objectToJson(userVo), true);

        return Result.success(true);
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public String doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        User user = getById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);

        return token;
    }

    @Autowired
    RedisOperation redisOperation;

    private void addCookie(HttpServletResponse response, String token, User user) {
        redisOperation.set(UserKey.token, token, user);
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Autowired
    UserDao userDao;

    public User getById(long id) {
        return userDao.getById(id);
    }



}
