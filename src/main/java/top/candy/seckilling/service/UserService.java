package top.candy.seckilling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.candy.seckilling.dao.UserDao;
import top.candy.seckilling.exception.GlobalException;
import top.candy.seckilling.pojo.User;
import top.candy.seckilling.redis.RedisService;
import top.candy.seckilling.redis.UserKey;
import top.candy.seckilling.result.CodeMsg;
import top.candy.seckilling.util.MD5Util;
import top.candy.seckilling.util.UUIDUtil;
import top.candy.seckilling.vo.LoginVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

//service只能调用service，别的service可能里面有缓存
@Service
public class UserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    private RedisService redisService;
    private UserDao userDao;
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    //public User getUserById(Long id) {
    //    return userDao.getById(id);
    //}

    //返回CodeMsg在实际开发当中并不常见，应该返回真正能代表业务含义的东西
    //public CodeMsg login(LoginVo loginVo) {
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            //return CodeMsg.SERVER_ERROR;
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        //输入的手机号和密码
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //判断手机号在数据库是否存在
        User user = getUserById(Long.valueOf(mobile));
        if (user == null) {
            //return CodeMsg.MOBILE_NOT_EXIST;
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码是否正确,需要将密码转换为md5格式的
        String userPassword = user.getPassword();
        String userSalt = user.getSalt();
        String calPass = MD5Util.formPassToDBPass(password, userSalt);
        if (!calPass.equals(userPassword)) {
            //return CodeMsg.PASSWORD_ERROR;
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        /*
        登录成功之后给这个用户创建一个类似于sessionId的东西标示这个用户，我们称之为token
        写到cookie当中，传递给客户端。客户端在随后访问当中都在cookie中上传token，
        服务端都根据token取到用户session信息
         */

        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return true;
    }

    private void addCookie(HttpServletResponse response, String token, User user) {
        //将token存放在redis当中
        redisService.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //设置有效期
        cookie.setMaxAge(UserKey.token.expireSeconds());
        //网站的根目录
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public User getUserById(Long id) {
        //取缓存
        User user = redisService.get(UserKey.getById, "" + id, User.class);
        if (user != null) {
            return user;
        }
        //如果是null的，那么就要从数据库中取
        user = userDao.getById(id);
        if (user != null) {
            redisService.set(UserKey.getById, "" + id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, Long id, String formPass) {
        // 先拿到user
        User user = getUserById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        User userToUpdate = new User();
        // 为什么使用一个新的user，更新的字段越多，sal就越复杂
        userToUpdate.setId(id);
        userToUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        userDao.updatePassword(userToUpdate);
        // 注意，不能先让缓存失效再更新数据库！
        // 修改缓存,那么与user相关的缓存都需要修改
        // 那么key为id和token的都需要修改
        // 删掉user
        redisService.del(UserKey.getById, "" + user.getId());
        user.setPassword(userToUpdate.getPassword());
        //更新一下
        redisService.set(UserKey.token, token, user);
        return true;
    }

    public User getUserByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        if (user != null) {
            //重新设置一下过期时间
            addCookie(response, token, user);
        }
        return user;
    }

    //使用事务
    /*
    @Transactional
    public boolean tx() {
        User user1 = new User();
        user1.setId(2);
        user1.setUsername("daming");

        User user2 = new User();
        user2.setId(1);
        user2.setUsername("sam2");

        userDao.insert(user1);
        userDao.insert(user2);

        return true;
    }
     */
}
