package top.candyboy.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//@Component
//@Data
@Configuration
//@ConfigurationProperties(prefix = "spring.redis")
public class RedisClusterConfig {
    //private String host;
    //private int port;
    //private int timeout;
    //private MyJedis jedis = new MyJedis();
    //
    //内部类的访问
    //@Data
    //@Component
    //public class MyJedis {
    //    HashMap<String, Integer> pool = new HashMap<>();
    //}

    //@Bean
    //public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    //    RedisTemplate<String, Object> template = new RedisTemplate<>();
    //    template.setConnectionFactory(factory);
    //
    //    // 使用Jackson2JsonRedisSerialize 替换默认的jdkSerializeable序列化
    //    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    //    ObjectMapper om = new ObjectMapper();
    //    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    //    om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance , ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    //    jackson2JsonRedisSerializer.setObjectMapper(om);
    //
    //    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    //
    //    // key采用String的序列化方式
    //    template.setKeySerializer(stringRedisSerializer);
    //    // hash的key也采用String的序列化方式
    //    template.setHashKeySerializer(stringRedisSerializer);
    //    // value序列化方式采用jackson
    //    template.setValueSerializer(jackson2JsonRedisSerializer);
    //    // hash的value序列化方式采用jackson
    //    template.setHashValueSerializer(jackson2JsonRedisSerializer);
    //    template.afterPropertiesSet();
    //    return template;
    //}

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}