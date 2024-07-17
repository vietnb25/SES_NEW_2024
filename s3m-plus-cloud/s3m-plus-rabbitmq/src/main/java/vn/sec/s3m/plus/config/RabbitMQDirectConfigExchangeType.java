package vn.sec.s3m.plus.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Mak Sophea Date: 07/31/2020
 */
@Configuration
public class RabbitMQDirectConfigExchangeType {

	/**
	 * Direct Exchange vận chuyển message đến queue dựa vào routing key. Thường được sử dụng cho việc định tuyến tin nhắn unicast-đơn hướng (mặc dù nó có thể sử dụng cho định tuyến multicast-đa hướng). 
	 * Các bước định tuyến message:
	 * 	+ Một queue được ràng buộc với một direct exchange bởi một routing key K.
	 * 	+ Khi có một message mới với routing key R đến direct exchange. Message sẽ được chuyển tới queue đó nếu R=K
	 * @return
	 */

	@Bean
	Queue adminQueue() {
		return new Queue("adminQueue", false);
	}

	@Bean
	DirectExchange directExchange() {
		return new DirectExchange("direct-exchange");
	}

	@Bean
	Binding adminBinding(Queue adminQueue, DirectExchange directExchange) {
		return BindingBuilder.bind(adminQueue).to(directExchange).with("admin");
	}
}
