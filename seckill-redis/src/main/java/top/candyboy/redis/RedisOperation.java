package top.candyboy.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import top.candyboy.redis.KeyPrefix;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class RedisOperation {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //获取到的是一个bean

    public <T> T get(KeyPrefix prefix, String key, Class<T> tClass) {
        String realKey = prefix.getPrefix() + key;
        String value = redisTemplate.opsForValue().get(realKey);
        return stringToBean(value, tClass);
    }

    /**
     * 设置单个的值
     * @param prefix 前缀
     * @param key 键
     * @param value 值
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        try {
            //将value转为string
            String realValue = beanToString(value);
            if (realValue == null || realValue.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                redisTemplate.opsForValue().set(realKey, realValue);
            } else {
                redisTemplate.opsForValue().set(realKey, realValue, seconds, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置一个list类型的值
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean setList(KeyPrefix prefix, String key, T value) {

        try {
            //将value转为string
            String realValue = beanToString(value);
            if (realValue == null || realValue.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            redisTemplate.opsForList().rightPushAll(realKey, realValue);
            if (seconds > 0) {
                expire(key, seconds);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <T> boolean exist(KeyPrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(realKey));
    }

    public <T> Long incr(KeyPrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.opsForValue().increment(realKey, 1);
    }

    public <T> Long decr(KeyPrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.opsForValue().increment(realKey, -1);
    }

    public boolean del(KeyPrefix prefix, String key) {
        String realKey = prefix.getPrefix() + key;
        return Boolean.TRUE.equals(redisTemplate.delete(realKey));
    }

    public  <T> String beanToString(T value) {
        //参数校验必不可少
        if (value == null) {
            return null;
        }
        Class<?> valueClass = value.getClass();
        if (valueClass == int.class || valueClass == Integer.class) {
            return "" + value;
        } else if (valueClass == String.class) {
            return (String)value;
        } else if (valueClass == Long.class) {
            return "" + value;
        } else {
            //其他认为是一个bean
            return JSON.toJSONString(value);
        }
    }

    //将string转换为bean,bean的类型就是
    @SuppressWarnings("unchecked")
    public static  <T> T stringToBean(String s, Class<T> tClass) {
        if (s == null || s.length() <= 0 || tClass == null) {
            return null;
        }
        if (tClass == int.class || tClass == Integer.class) {
            return (T)Integer.valueOf(s);
        } else if (tClass == String.class) {
            return (T)s;
        } else if (tClass == Long.class) {
            return (T)Long.valueOf(s);
        } else {
            //其他认为是一个bean
            return JSON.toJavaObject(JSON.parseObject(s), tClass);
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
