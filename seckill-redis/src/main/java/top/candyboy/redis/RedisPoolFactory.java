package top.candyboy.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import top.candyboy.redis.config.RedisConfig;

@Service
public class RedisPoolFactory {
    RedisConfig redisConfig;
    @Autowired
    public void setRedisConfig(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Bean
    public JedisPool jedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getJedis().getPool().get("max-idle"));
        poolConfig.setMaxTotal(redisConfig.getJedis().getPool().get("max-active"));
        poolConfig.setMaxWaitMillis(redisConfig.getJedis().getPool().get("max-wait") * 1000);
        //timeout是设置socket的
        return new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout()*1000);
    }
}
