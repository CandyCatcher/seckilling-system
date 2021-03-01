package top.candyboy.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.candyboy.redis.RedisOperation;

@Service
public class MQSender {
    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    RedisOperation redisOperation;
    AmqpTemplate amqpTemplate;

    @Autowired
    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }
    @Autowired
    public void setRedisOperation(RedisOperation redisOperation) {
        this.redisOperation = redisOperation;
    }

    /*
    使用最简单的模式
     */
    public void sendMessage(SeckillMessage message) {
        String s = redisOperation.beanToString(message);
        logger.info("sendMessage" + message);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, s);
    }

    /*
    发送消息
    public void send(Object message) {
        String s = redisOperation.beanToString(message);
        logger.info("send message:" + s);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, s);
    }

    public void sendTopic(Object message) {
        String s = redisOperation.beanToString(message);
        logger.info("send topic message:" + s);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", message + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", message + "2");
    }

    public void sendFanout(Object message) {
        String s = redisOperation.beanToString(message);
        logger.info("send fanout message:" + s);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", message + "1");
    }

    public void sendHeader(Object message) {
        String s = redisOperation.beanToString(message);
        logger.info("send header message:" + s);
        MessageProperties messageProperties = new MessageProperties();
        //头部信息
        messageProperties.setHeader("head1", "v1");
        messageProperties.setHeader("head2", "v2");
        Message message1 = new Message(s.getBytes(), messageProperties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", message1);
    }
    */

}
