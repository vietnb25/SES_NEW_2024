package vn.ses.s3m.plus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class S3mPlusEurekaApplication {

    public static void main(final String[] args) {
        SpringApplication.run(S3mPlusEurekaApplication.class, args);
    }

}
