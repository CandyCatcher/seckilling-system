package top.candy.seckilling.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {
    private String host;
    private int port;
    private int timeout;
    private MyJedis jedis = new MyJedis();

    //内部类的访问
    @Data
    @Component
    public class MyJedis {
        HashMap<String, Integer> pool = new HashMap<>();
    }

}




