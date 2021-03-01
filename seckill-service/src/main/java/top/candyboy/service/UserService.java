package top.candyboy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import top.candyboy.exception.GlobalException;
import top.candyboy.UserDao;
import top.candyboy.pojo.User;
import top.candyboy.redis.RedisOperation;
import top.candyboy.redis.key.UserKey;
import top.candyboy.result.CodeMsg;
import top.candyboy.util.MD5Util;

import javax.servlet.http.HttpServletResponse;

//service只能调用service，别的service可能里面有缓存
@Service
public class UserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    private RedisOperation redisOperation;
    private UserDao userDao;
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    @Autowired
    public void setRedisOperation(RedisOperation redisOperation) {
        this.redisOperation = redisOperation;
    }


    //返回CodeMsg在实际开发当中并不常见，应该返回真正能代表业务含义的东西
    //public CodeMsg login(LoginVo loginVo) {

    //private void addCookie(HttpServletResponse response, String token, User user) {
    //
    //    //将token存放在redis当中
    //    redisOperation.set(UserKey.token, token, user);
    //    Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
    //    //设置有效期
    //    cookie.setMaxAge(UserKey.token.expireSeconds());
    //    //网站的根目录
    //    cookie.setPath("/");
    //    response.addCookie(cookie);
    //}

    public User getUserById(Long id) {
        // 取缓存
        User user = redisOperation.get(UserKey.getById, "" + id, User.class);
        if (user != null) {
            return user;
        }

        // 如果是null的，那么就要从数据库中取
        user = userDao.getById(id);
        if (user != null) {
            redisOperation.set(UserKey.getById, "" + id, user);
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
        /*
         注意，不能先让缓存失效再更新数据库！
         修改缓存,那么与user相关的缓存都需要修改
         那么key为id和token的都需要修改
         假如我先删掉了缓存里面的数据，取数据的时候就会从数据库取旧的数据了，然后再更新缓存，那么只能等到缓存失效之后才能获取新的数据
         */
        redisOperation.del(UserKey.getById, "" + user.getId());
        user.setPassword(userToUpdate.getPassword());
        // token不能删除，需要更新
        redisOperation.set(UserKey.token, token, user);
        return true;
    }

    public User getUserByToken(HttpServletResponse response, String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        User user = redisOperation.get(UserKey.token, token, User.class);
        //if (user != null) {
        //    //重新设置一下过期时间
        //    addCookie(response, token, user);
        //}
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
