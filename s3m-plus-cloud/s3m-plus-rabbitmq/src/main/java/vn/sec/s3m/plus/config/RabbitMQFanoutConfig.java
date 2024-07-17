package vn.sec.s3m.plus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Mak Sophea
 * Date: 07/31/2020
 */
@Slf4j
@Configuration
public class RabbitMQFanoutConfig {

	/**
	 * Fanout exchange định tuyến message tới tất cả queue mà nó bao quanh, routing key bị bỏ qua. 
	 * Giả sử, nếu nó N queue được bao quanh bởi một Fanout exchange, khi một message mới published, exchange sẽ vận chuyển message đó tới tất cả N queues. 
	 * Fanout exchange được sử dụng cho định tuyến thông điệp broadcast - quảng bá
	 * @return
	 */
	
    @Bean
    Queue adminQueueFanout() {
        return new Queue("adminQueue-f", false);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout-exchange");
    }

    @Bean
    Binding adminBindingFanout(Queue adminQueueFanout, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(adminQueueFanout).to(fanoutExchange);
    }
}
