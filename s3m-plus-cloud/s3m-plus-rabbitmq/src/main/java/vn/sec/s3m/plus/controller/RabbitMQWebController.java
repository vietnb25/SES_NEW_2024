package vn.sec.s3m.plus.controller;

import vn.sec.s3m.plus.model.Category;
import vn.sec.s3m.plus.service.RabbitMQSender;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Author: Mak Sophea
 * Date: 07/31/2020
 */
@RestController
@RequestMapping(value = "/api/rabbitmq/")
@Slf4j
public class RabbitMQWebController {

    @Autowired
    private AmqpTemplate rabbitTemplateBean;


    @Autowired
    private RabbitMQSender rabbitMQSender;

    //direct
    //http://localhost:4567/api/rabbitmq/producer?code=001&description=MyDescription
    @GetMapping(value = "/producer")
    public String producer(@ModelAttribute Category category) {
        log.info(" category {} , {} ", category.getCode(), category.getDescription() );
        rabbitMQSender.send(category);

        return "Message sent to the RabbitMQ Successfully";
    }
    
    @GetMapping(value = "/list")
    public List<String> getList() {
        List<String> data = new ArrayList<>();
        data.add("Minh");
        data.add("Quang");
        data.add("Thắng");
        data.add("Lâm");
        data.add("Chinh");
        rabbitMQSender.sendList(data);
        return data;
    }
    

    //http://localhost:4567/api/rabbitmq/topic?exchangeName=topic-exchange&routingKey=queue.admin&messageData=MessageData
    @GetMapping(value = "/exchange")
    public String producer(@RequestParam("exchangeName") String exchange, @RequestParam("routingKey") String routingKey,
                           @RequestParam("messageData") String messageData) {
        List<String> data = new ArrayList<>();
        data.add("Minh");
        data.add("Quang");
        data.add("Thắng");
        data.add("Lâm");
        data.add("Chinh");
        rabbitTemplateBean.convertAndSend(exchange, routingKey, data);

        return "Message sent to the RabbitMQ Successfully";
    }

    //http://localhost:4567/api/rabbitmq/fanout?exchangeName=fanout-exchange&messageData=message
    @GetMapping(value = "/fanout")
    public String fanout(@RequestParam("exchangeName") String exchange,
                           @RequestParam("messageData") String messageData) {

        log.info("send to mq {} {}", exchange, messageData);
        rabbitTemplateBean.convertAndSend(exchange, "", messageData);

        return "Message sent to the RabbitMQ Successfully";
    }

    //http://localhost:4567/api/rabbitmq/topic?exchangeName=topic-exchange&routingKey=queue.admin&messageData=MessageData
    @GetMapping(value = "/topic")
    public String producerTopic(@RequestParam("exchangeName") String exchange, @RequestParam("routingKey") String routingKey, @RequestParam("messageData") String messageData) {

        log.info("send to mq with topic type{} {}", exchange, messageData);
        rabbitTemplateBean.convertAndSend(exchange, routingKey, messageData);
        return "Message sent to the RabbitMQ Topic Exchange Successfully";
    }


    //http://localhost:4567/api/rabbitmq/header?exchangeName=header-exchange&department=admin&messageData=MessageValue
    @GetMapping(value = "/header")
    public String producerHeader(@RequestParam("exchangeName") String exchange, @RequestParam("department") String department, @RequestParam("messageData") String messageData) {

        final MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("department", department);
        MessageConverter messageConverter = new SimpleMessageConverter();
        Message message = messageConverter.toMessage(messageData, messageProperties);
        rabbitTemplateBean.send(exchange, "", message);

        return "Message sent to the RabbitMQ Header Exchange Successfully";
    }
}
