package com.xiaojie.ware.configuration;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@EnableRabbit
@Configuration
public class MyRabbitMQConfiguratioin {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange", true, false);
    }

    /**
     * 延迟队列
     *
     * @return
     */
    @Bean
    public Queue stockDelayQueue() {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        // 消息过期时间2分钟
        arguments.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue", true, false, false, arguments);
    }

    /**
     * 普通队列，用于解锁库存
     *
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue("stock.release.stock.queue", true, false, false, null);
    }


    /**
     * 交换机和延迟队列绑定
     *
     * @return
     */
    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null);
    }

    /**
     * 交换机和普通队列绑定
     *
     * @return
     */
    @Bean
    public Binding stockReleaseBinding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }
/**
 * 第1站：发送到交换机
 * lockedTo对象 → 序列化成JSON → 消息
 * ↓
 * 发送到 stock-event-exchange 交换机
 * 第2站：路由到延迟队列
 * 交换机根据路由规则 "stock.locked"
 * ↓
 * 路由到 stock.delay.queue (延迟队列)
 * 第3站：等待2分钟
 * 在 stock.delay.queue 中等待
 * ↓
 * 2分钟后消息过期 (TTL = 120000ms)
 * 第4站：转到死信交换机
 * 过期消息 → 自动转发到死信交换机
 * ↓
 * stock-event-exchange (同一个交换机)
 * ↓
 * 使用新的路由键 "stock.release"
 * 第5站：到达解锁队列
 * 根据路由 "stock.release"
 * ↓
 * 路由到 stock.release.stock.queue (解锁队列)
 */

}
