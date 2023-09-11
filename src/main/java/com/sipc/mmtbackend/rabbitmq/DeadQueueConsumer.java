package com.sipc.mmtbackend.rabbitmq;

import com.sipc.mmtbackend.config.DirectRabbitConfig;
import com.sipc.mmtbackend.pojo.c.param.RegistrationFormParamPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * @author tzih
 * @version 1.0
 * @since 2023/4/10 15:53
 */
@Slf4j
public class DeadQueueConsumer {

    @RabbitListener(queues = DirectRabbitConfig.MESSAGE_DEAD_QUEUE_NAME)
    public void consumer(RegistrationFormParamPo registrationFormParamPo) {
        log.error("发送消息请求失败, 消息参数为: {}", registrationFormParamPo);
    }

}
