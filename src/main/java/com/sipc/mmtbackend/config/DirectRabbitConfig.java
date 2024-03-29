package com.sipc.mmtbackend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DirectRabbitConfig {

    public static final String QUEUE_NAME = "MessageRequestQueue"; //队列名称
    public static final String EXCHANGE_NAME = "MessageRequestExchange"; //交换器名称
    public static final String ROUTING_KEY = "MessageRequestRouting"; //路由键

    public static final String MESSAGE_DEAD_QUEUE_NAME = "MessageRequestDeadQueue";
    public static final String MESSAGE_DEAD_EXCHANGE_NAME = "MessageRequestDeadExchange";
    public static final String MESSAGE_DEAD_ROUTING_KEY = "MessageRequestDeadRouting";

    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);

        //确认消息送到交换机(Exchange)回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("\n确认消息送到交换机(Exchange)结果：");
            System.out.println("相关数据：" + correlationData);
            System.out.println("是否成功：" + ack);
            System.out.println("错误原因：" + cause);
        });

        //确认消息送到队列(Queue)回调
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            System.out.println("\n确认消息送到队列(Queue)结果：");
            System.out.println("发生消息：" + returnedMessage.getMessage());
            System.out.println("回应码：" + returnedMessage.getReplyCode());
            System.out.println("回应信息：" + returnedMessage.getReplyText());
            System.out.println("交换机：" + returnedMessage.getExchange());
            System.out.println("路由键：" + returnedMessage.getRoutingKey());
            //指定一个死信队列消费消息
//            rabbitTemplate.convertAndSend(SUBMIT_DEAD_EXCHANGE_NAME, SUBMIT_DEAD_ROUTING_KEY, returnedMessage);
        });

        rabbitTemplate.setMessageConverter(messageConverter());

        return rabbitTemplate;
    }

    //队列 起名：NodeRequestQueue
    @Bean
    public Queue NodeRequestQueue() {
        /*
          创建队列，参数说明：
          String name：队列名称。
          boolean durable：设置是否持久化，默认是 false。durable 设置为 true 表示持久化，反之是非持久化。
          持久化的队列会存盘，在服务器重启的时候不会丢失相关信息。
          boolean exclusive：设置是否排他，默认也是 false。为 true 则设置队列为排他。
          boolean autoDelete：设置是否自动删除，为 true 则设置队列为自动删除，
          当没有生产者或者消费者使用此队列，该队列会自动删除。
          Map<String, Object> arguments：设置队列的其他一些参数。
         */

        return new Queue(QUEUE_NAME, true, false, false);
    }

    //Direct交换机 起名：NodeRequestQueue
    @Bean
    public DirectExchange NodeRequestExchange() {
        /*
          创建交换器，参数说明：
          String name：交换器名称
          boolean durable：设置是否持久化，默认是 false。durable 设置为 true 表示持久化，反之是非持久化。
          持久化可以将交换器存盘，在服务器重启的时候不会丢失相关信息。
          boolean autoDelete：设置是否自动删除，为 true 则设置队列为自动删除，
         */

        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    //绑定
    @Bean
    public Binding bindingDirect() {
        //将队列和交换机绑定, 并设置用于匹配键：NodeRequestRouting
        return BindingBuilder.bind(NodeRequestQueue()).to(NodeRequestExchange()).with(ROUTING_KEY);
    }

    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyExchange");
    }

    @Bean
    public DirectExchange deadRequestExchange() {
        return new DirectExchange(MESSAGE_DEAD_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue deadRequestQueue() {

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MESSAGE_DEAD_EXCHANGE_NAME);
        arguments.put("x-dead-letter-routing-key", MESSAGE_DEAD_ROUTING_KEY);
        arguments.put("x-message-ttl", 5000); //TTL为5s

        return new Queue(MESSAGE_DEAD_QUEUE_NAME, true, false, false, arguments);
    }

    @Bean
    public Binding deadBindingDirect() {

        return BindingBuilder.bind(deadRequestQueue()).to(deadRequestExchange()).with(MESSAGE_DEAD_ROUTING_KEY);
    }
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean("batchQueueRabbitListenerContainerFactory")
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //确认方式,manual为手动ack.
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //每次处理数据数量，提高并发量
        //factory.setPrefetchCount(250);
        //设置线程数
        //factory.setConcurrentConsumers(30);
        //最大线程数
        //factory.setMaxConcurrentConsumers(50);
        /* setConnectionFactory：设置spring-amqp的ConnectionFactory。 */
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(30);
        factory.setPrefetchCount(10);
        //factory.setDefaultRequeueRejected(true);
        //使用自定义线程池来启动消费者。
        factory.setTaskExecutor(taskExecutor());
        return factory;
    }

    @Bean("correctTaskExecutor")
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(100);
        // 设置最大线程数
        executor.setMaxPoolSize(100);
        // 设置队列容量
        executor.setQueueCapacity(0);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(300);
        // 设置默认线程名称
        executor.setThreadNamePrefix("thread-file-queue");
        // 设置拒绝策略rejection-policy：当pool已经达到max size的时候，丢弃
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

}