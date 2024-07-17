package vn.ses.s3m.plus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class S3mPlusLoadConsumerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(S3mPlusLoadConsumerApplication.class, args);
    }
}
