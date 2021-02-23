package top.candy.seckilling.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String SECKILL_QUEUE = "seckillQueue";

    //@Bean
    //public Queue queue() {
    //    return new Queue(SECKILL_QUEUE, true);
    //}

/*    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topicQueue1";
    public static final String TOPIC_QUEUE2 = "topicQueue2";
    public static final String ROUTING_KEY1 = "topic.key1";
    //#代表多个单词
    public static final String ROUTING_KEY2 = "topic.#";
    public static final String HEADER_QUEUE = "header.queue";

    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String HEADERS_EXCHANGE = "headersExchange";

    *//**
     * Direct交换机模式,带有通配符
     *
     * @return
     *//*
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    *//**
     * Topic模式
     * 先把消息放在交换机里，然后再放在队列里
     *//*
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
    }

    *//**
     * 广播Fanout模式
     *//*
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    *//**
     * Header模式
     *//*
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Queue headerQueue() {
        return new Queue(HEADER_QUEUE, true);
    }

    @Bean
    public Binding headerBing() {
        Map<String, Object> map = new HashMap<>();
        map.put("head1", "v1");
        map.put("head2", "v2");
        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
    }*/
}