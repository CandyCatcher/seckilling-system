package top.candyboy.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import top.candyboy.redis.KeyPrefix;


@Service
public class RedisOperation {

    JedisPool jedisPool;
    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    //获取到的是一个bean

    /**
     * 获取单个对象
     * @param prefix
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> tClass) {
        Jedis jedis = null;
        //连接池的话一定要记得关闭
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String s = jedis.get(realKey);
            //System.out.println("s" + s);
            return stringToBean(s, tClass);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     * @param prefix 前缀
     * @param key 键
     * @param value 值
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String s = beanToString(value);
            if (s == null || s.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, s);
            } else {
                jedis.setex(realKey, seconds, s);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean exist(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            //这是一个原子操作
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    public boolean del(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            Long del = jedis.del(realKey);
            return del > 0;
        } finally {
            returnToPool(jedis);
        }
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

    public void returnToPool(Jedis jedis) {
        if (jedis != null) {
            //将它放回到连接池中
            jedis.close();
        }
    }

}
