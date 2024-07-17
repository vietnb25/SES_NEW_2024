package vn.sec.s3m.plus.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Mak Sophea
 * Date: 07/31/2020
 */
@Configuration
public class RabbitMQTopicConfig {
	
	/**
	 * Topic exchange định tuyến message tới một hoặc nhiều queue dựa trên sự trùng khớp giữa routing key và pattern. 
	 * Topic exchange thường sử dụng để thực hiện định tuyến thông điệp multicast. 
	 * Ví dụ một vài trường hợp sử dụng: 
	 * 	+ Phân phối dữ liệu liên quan đến vị trí địa lý cụ thể.
	 * 	+ Xử lý tác vụ nền được thực hiện bởi nhiều workers, mỗi công việc có khả năng xử lý các nhóm tác vụ cụ thể.
	 * 	+ Cập nhật tin tức liên quan đến phân loại hoặc gắn thẻ (ví dụ: chỉ dành cho một môn thể thao hoặc đội cụ thể).
	 * 	+ Điều phối các dịch vụ của các loại khác nhau trong cloud
	 * @return
	 */
	
    @Bean
    Queue adminQueueTopic() {
        return new Queue("adminQueueTopic", false);
    }

    @Bean
    Queue allQueueTopic() {
        return new Queue("allQueueTopic", false);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange("topic-exchange");
    }

    @Bean
    Binding adminBindingTopic(Queue adminQueueTopic, TopicExchange topicExchange) {
        return BindingBuilder.bind(adminQueueTopic).to(topicExchange).with("queue.admin");
    }

    @Bean
    Binding allBinding(Queue allQueueTopic, TopicExchange topicExchange) {
        return BindingBuilder.bind(allQueueTopic).to(topicExchange).with("queue.*");
    }
}
