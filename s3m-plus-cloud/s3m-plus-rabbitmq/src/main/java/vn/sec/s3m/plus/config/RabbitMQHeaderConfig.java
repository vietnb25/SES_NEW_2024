package vn.sec.s3m.plus.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Mak Sophea
 * Date: 07/31/2020
 */
@Configuration
public class RabbitMQHeaderConfig {

	/**
	 * Header exchange được thiết kế để định tuyến với nhiều thuộc tính, đễ dàng thực hiện dưới dạng tiêu đề của message hơn là routing key. 
	 * Header exchange bỏ đi routing key mà thay vào đó định tuyến dựa trên header của message. 
	 * Trường hợp này, broker cần một hoặc nhiều thông tin từ application developer, cụ thể là, nên quan tâm đến những tin nhắn với tiêu đề nào phù hợp hoặc tất cả chúng
	 * @return
	 */
	
    @Bean
    Queue adminQueueHeader() {
        return new Queue("adminQueueHeader", false);
    }

    @Bean
    HeadersExchange headerExchange() {
        return new HeadersExchange("header-exchange");
    }

    @Bean
    Binding adminBindingHeader(Queue adminQueueHeader, HeadersExchange headerExchange) {
        return BindingBuilder.bind(adminQueueHeader).to(headerExchange).where("department").matches("admin");
    }
}
