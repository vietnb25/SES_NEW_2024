package vn.sec.s3m.plus.service;

import vn.sec.s3m.plus.model.Category;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Author: Mak Sophea
 * Date: 07/31/2020
 */
@Service
@Slf4j
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${backend.rabbitmq.exchange}")
    private String exchange;

    @Value("${backend.rabbitmq.routingkey}")
    private String routingkey;

    public void send(Category category) {
        rabbitTemplate.convertAndSend(exchange, routingkey, category);
        log.info("Send msg = {} ", category);
    }
    
    public void sendList(List<String> data) {
        rabbitTemplate.convertAndSend(exchange, routingkey, data);
        log.info("Send msg = {} ", data);
    }
}
