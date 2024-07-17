package vn.ses.s3m.plus.jms;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public final class RabbitmqLoadConfig {

    /** Logging */
    private static Log log = LogFactory.getLog(RabbitmqLoadConfig.class);

    private Connection connection;
    private Channel channel;

    private static RabbitmqLoadConfig instance;

    private RabbitmqLoadConfig() {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(Constants.S3mQueue.HOST_NAME);
            connectionFactory.setPort(Constants.S3mQueue.NUMBER_5672);
            connectionFactory.setUsername(Constants.S3mQueue.LOAD_USER_NAME);
            connectionFactory.setPassword(Constants.S3mQueue.LOAD_PASSWORD);

            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(Constants.S3mQueue.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            log.info("Chanel: " + channel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized RabbitmqLoadConfig getInstance() {
        if (instance == null) {
            instance = new RabbitmqLoadConfig();
        }
        return instance;
    }

}
